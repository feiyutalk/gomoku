package edu.hitsz.remoting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import edu.hitsz.commons.support.Pair;
import edu.hitsz.commons.support.ServiceThread;
import edu.hitsz.commons.utils.RemotingHelper;
import edu.hitsz.commons.utils.SemaphoreReleaseOnlyOnce;
import edu.hitsz.remoting.codec.Codec;
import edu.hitsz.remoting.codec.FrobotCodec;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingSendRequestException;
import edu.hitsz.remoting.exception.RemotingTooMuchRequestException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;
import edu.hitsz.commons.utils.SystemClock;
import edu.hitsz.remoting.exception.RemotingTimeoutException;

/**
 * @author Neuclil
 */
public abstract class AbstractRemoting implements Remoting {

    /* 日志 */
    private static final Logger LOGGER = Logger.getLogger(AbstractRemoting.class);

    /* 信号量 用于控制本地 单向请求 同时并发数量*/
    protected final Semaphore semaphoreOneway;

    /* 信号量 用于控制本地 异步请求 同时并发数量*/
    protected final Semaphore semaphoreAsync;

    /**
     * Map 用于缓冲所有对外的请求
     * key 是request的opaque字段，用来匹配响应(其中opaque字段默认是requestId的值，requestId是静态变量)
     * value 存放ResponseFuture,用来抽象异步的响应
     */
    protected final ConcurrentHashMap<Integer, ResponseFuture> responseTable = new ConcurrentHashMap<Integer, ResponseFuture>(
            256);

    /*RPC处理器表，针对每种RPC指令，可以注册一个处理器，如果不注册，则采用默认处理器处理. 为每个处理器分配一个线程池，用来处理请求*/
    protected final HashMap<Integer, Pair<RemotingProcessor, ExecutorService>> processorTable = new HashMap<Integer, Pair<RemotingProcessor, ExecutorService>>(
            64);

    /*默认的RPC处理器*/
    protected Pair<RemotingProcessor, ExecutorService> defaultRequestProcessor;

    /*事件监听器 保留 暂时未使用*/
    protected final ChannelEventListener channelEventListener;

    /*事件处理类 保留 暂时未使用*/
    protected final RemotingEventExecutor remotingEventExecutor = new RemotingEventExecutor();

    /**
     * 构造函数
     *
     * @param permitsOneWay
     * @param permitsAsync
     * @param channelEventListener
     */
    public AbstractRemoting(final int permitsOneWay, final int permitsAsync,
                            ChannelEventListener channelEventListener) {
        this.semaphoreOneway = new Semaphore(permitsOneWay, true);
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
        this.channelEventListener = channelEventListener;
    }

    /**
     * 得到事件监听器，主要用于对事件进行处理
     *
     * @return
     */
    public ChannelEventListener getChannelEventListener() {
        return this.channelEventListener;
    }

    /**
     * 将通信产生的事件放入的到事件处理的阻塞队列中
     */
    public void putRemotingEvent(final RemotingEvent event) {
        this.remotingEventExecutor.putRemotingEvent(event);
    }

    /**
     * 接受到通信指令调用的方法，该方法主要对接受到的指令进行判断，然后调用相应的方法。
     *
     * @param channel
     * @param cmd
     * @throws Exception
     */
    public void processMessageReceived(final Channel channel,
                                       final RemotingCommand cmd) throws Exception {
        if (cmd != null) {
            switch (RemotingHelper.getRemotingCommandType(cmd)) {
                case REQUEST_COMMAND:
                    processRequestCommand(channel, cmd);
                    break;
                case RESPONSE_COMMAND:
                    processResponseCommand(channel, cmd);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理请求，具体步骤如下
     * 1. 从处理器Map中取出该request指令的处理器，如果没有，用默认的处理器
     * 2. 新建一个线程，该线程负责将指令交给处理器去做。
     */
    @Override
    public void processRequestCommand(final Channel channel,
                                      final RemotingCommand cmd) {
        /**
         * 根据通信请求的命令从处理器表中取出对应的处理器，可能为空，如果为空
         * 则使用默认的处理器
         */
        final Pair<RemotingProcessor, ExecutorService> matched = this.processorTable
                .get(cmd.getCode());
        final Pair<RemotingProcessor, ExecutorService> pair = null == matched
                ? this.defaultRequestProcessor : matched;
        if (pair != null) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        final RemotingCommand response = pair.getKey()
                                .processRequest(channel, cmd);
                        if (!RemotingHelper.isOnewayCommand(cmd)) {
                            if (response != null) {
                                response.setOpaque(cmd.getOpaque());
                                try {
                                    channel.writeAndFlush(response).addListener(
                                            new ChannelHandlerListener() {

                                                @Override
                                                public void operationComplete(
                                                        Future future)
                                                        throws Exception {
                                                    if (!future.isSuccess()) {
                                                        LOGGER.error(
                                                                "发送通信响应到"
                                                                        + RemotingHelper
                                                                        .parseChannelRemoteAddr(
                                                                                channel)
                                                                        + "失败",
                                                                future.cause());
                                                        LOGGER.error(
                                                                cmd.toString());
                                                        LOGGER.error(response
                                                                .toString());
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    LOGGER.error("处理请求结束，但是发送响应失败", e);
                                    LOGGER.error(cmd.toString());
                                    LOGGER.error(response.toString());
                                }
                            } else {
                                //收到了请求，但是没有给予反馈，可能有一些请求不需要反馈
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("处理请求出现异常", e);
                        LOGGER.error(cmd.toString());

                        /*如果是单向的RPC，就不反馈 否则反馈处理器处理请求错误*/
                        if (!RemotingHelper.isOnewayCommand(cmd)) {
                            final RemotingCommand response = RemotingCommand
                                    .createResponseCommand(
                                            RemotingProtos.ResponseCode.COMMAND_PROCESS_ERROR
                                                    .code(),
                                            e.toString());
                            response.setOpaque(cmd.getOpaque());
                            channel.writeAndFlush(response);
                        }
                    }
                }
            };

            try {
                /* 将Runnable任务提交给线程池处理 */
                pair.getValue().submit(run);
            } catch (RejectedExecutionException e) {
                /* 线程池拒绝执行任务，可能线程池处于饱和状态 */
                LOGGER.warn(RemotingHelper.parseChannelRemoteAddr(channel)
                        + ", 任务过多，处理器线程池处于饱和，拒绝执行该任务, RejectedExecutionException "
                        + pair.getKey().toString() + " 请求指令: " + cmd.getCode());
                if (!RemotingHelper.isOnewayCommand(cmd)) {
                    final RemotingCommand response = RemotingCommand
                            .createResponseCommand(
                                    RemotingProtos.ResponseCode.SYSTEM_BUSY
                                            .code(),
                                    "该Master的处理器处于饱和，请尝试连接其他处理器");
                    response.setOpaque(cmd.getOpaque());
                    channel.writeAndFlush(response);
                }
            }
        } else {
            /* 处理器为空，一般不会出现这种情况 */
            String error = " 请求指令 " + cmd.getCode() + " 不支持";
            final RemotingCommand response = RemotingCommand
                    .createResponseCommand(
                            RemotingProtos
                                    .ResponseCode
                                    .REQUEST_CODE_NOT_SUPPORTED
                                    .code(),
                            error);
            response.setOpaque(cmd.getOpaque());
            channel.writeAndFlush(response);
            LOGGER.error(
                    RemotingHelper.parseChannelRemoteAddr(channel) + error);
        }
    }

    /**
     * 处理响应，基本步骤如下：
     * 1. 到响应表寻找与该响应对应的responseFuture，响应表的Key是Request的响应码
     * 2. 对responseFuture进行处理
     */
    @Override
    public void processResponseCommand(final Channel channel, RemotingCommand cmd) {
        final ResponseFuture responseFuture = responseTable.get(cmd.getOpaque());
        if (responseFuture != null) {
            responseFuture.setResponseCommand(cmd);

            /*释放信号量，该释放在发送请求的时候需要申请*/
            responseFuture.release();

            /*判断response的类别，就是看是同步还是异步 如果是异步，需要调用回调函数*/
            if (responseFuture.getAsyncCallback() != null) {
                boolean runInThisThread = false;

                /*需要给该异步回调函数传入一个线程池，该线程池负责处理异步回调函数,也可以在当前线程执行，如果该函数执行较快*/
                ExecutorService executor = this.getCallbackExecutor();
                if (executor != null) {
                    try {
                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    responseFuture.executeInvokeCallback();
                                } catch (Exception e) {
                                    LOGGER.warn("执行回调函数失败", e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        runInThisThread = true;
                        LOGGER.warn("线程池提交执行回调的任务失败，可以线程池太忙", e);
                    }
                } else {
                    runInThisThread = true;
                }

                if (runInThisThread) {
                    try {
                        responseFuture.executeInvokeCallback();
                    } catch (Exception e) {
                        LOGGER.warn("执行回调函数失败", e);
                    }
                }
            } else {
                /**
                 * 同步调用，此时需要做两件事：
                 * 1.将得到的response放入到responseFuture中
                 * 2.解除线程的阻塞状态，让同步阻塞继续执行
                 */
                responseFuture.putResponseAndReleaseSync(cmd);
            }
        } else {
            LOGGER.warn("接收到响应，但是该响应没有匹配任何请求,"
                    + RemotingHelper.parseChannelRemoteAddr(channel));
            LOGGER.warn(cmd.toString());
        }

        /*将响应从响应表中移除*/
        responseTable.remove(cmd.getOpaque());
    }

    /**
     * 扫描响应表，主要是为了清除超时响应
     * 需要单开定时器负责这块
     */
    public void scanResponseTable() {
        Iterator<Entry<Integer, ResponseFuture>> it = this.responseTable
                .entrySet().iterator();
        Set<Entry<Integer, ResponseFuture>> entrySet = this.responseTable.entrySet();
        while (it.hasNext()) {
            Entry<Integer, ResponseFuture> next = it.next();
            ResponseFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis()
                    + 1000) <= SystemClock.now()) {
                it.remove();
                rep.release();
                try {
                    rep.executeInvokeCallback();
                } catch (Exception e) {
                    LOGGER.error("扫描响应表出现异常", e);
                }
                LOGGER.warn("移除了超时未响应的请求" + rep);
            }
        }
    }

    /**
     * 同步调用
     */
    @Override
    public RemotingCommand invokeSync(final Channel channel,
                                      final RemotingCommand request, long timeoutMillis)
            throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException {
        try {
            final ResponseFuture responseFuture = new ResponseFuture(
                    request.getOpaque(), timeoutMillis, null, null);
            this.responseTable.put(request.getOpaque(), responseFuture);
            channel.writeAndFlush(request)
                    .addListener(new ChannelHandlerListener() {

                        @Override
                        public void operationComplete(Future future)
                                throws Exception {
                            if (future.isSuccess()) {
                                responseFuture.setSendRequestOK(true);
                                return;
                            } else {
                                responseFuture.setSendRequestOK(false);
                            }

                            responseTable.remove(request.getOpaque());
                            responseFuture.setCause(future.cause());
                            responseFuture.putResponseAndReleaseSync(null);
                            LOGGER.warn("发送请求指令到<" + channel.remoteAddress()
                                    + "> 失败");
                            LOGGER.warn(request.toString());
                        }
                    });

            /*线程阻塞给定的时间，如果给定时间内没有收到响应，则抛出异常*/
            RemotingCommand responseCommand = responseFuture.waitResponse(timeoutMillis);
            if (null == responseCommand) {
                if (responseFuture.isSendRequestOK()) {
                    throw new RemotingTimeoutException(
                            RemotingHelper.parseChannelRemoteAddr(channel),
                            timeoutMillis, responseFuture.getCause());
                } else {
                    throw new RemotingSendRequestException(
                            RemotingHelper.parseChannelRemoteAddr(channel),
                            responseFuture.getCause());
                }
            }
            return responseCommand;
        } finally {
            this.responseTable.remove(request.getOpaque());
        }
    }

    /**
     * 异步调用
     */
    @Override
    public void invokeAsync(final Channel channel, final RemotingCommand request,
                            long timeoutMillis, AsyncCallback asyncCallback)
            throws InterruptedException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException {
        /**
         * 为了控制并发的异步请求的数量，需要用一个信号量
         * 每个异步调用的线程，需要先获得许可
         * 许可在等到该请求被处理完后释放。
         */
        boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMillis,
                TimeUnit.MILLISECONDS);
        if (acquired) {
            final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(
                    this.semaphoreAsync);
            final ResponseFuture responseFuture = new ResponseFuture(
                    request.getOpaque(), timeoutMillis, asyncCallback, once);
            this.responseTable.put(request.getOpaque(), responseFuture);
            try {
                channel.writeAndFlush(request)
                        .addListener(new ChannelHandlerListener() {
                            @Override
                            public void operationComplete(Future future)
                                    throws Exception {
                                if (future.isSuccess()) {
                                    responseFuture.setSendRequestOK(true);
                                    return;
                                } else {
                                    responseFuture.setSendRequestOK(false);
                                }

                                /*发送失败，直接调用空的回调函数，移除该请求*/
                                responseFuture.putResponseAndReleaseSync(null);
                                responseFuture.executeInvokeCallback();
                                responseTable.remove(request.getOpaque());
                                LOGGER.warn("发送给<" + channel.remoteAddress()
                                        + ">的请求失败");
                                LOGGER.warn(request.toString());
                            }
                        });
            } catch (Exception e) {
                once.release();
                LOGGER.warn("往<" + channel.remoteAddress() + "> 的通道里面写请求失败");
                throw new RemotingSendRequestException(
                        RemotingHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            if (timeoutMillis <= 0) {
                throw new RemotingTooMuchRequestException("发送发送过多");
            } else {
                LOGGER.warn("异步调用尝试获得信号量超时" + timeoutMillis + ", 等待的线程数目:"
                        + this.semaphoreAsync.getQueueLength());
                LOGGER.warn(request.toString());
                throw new RemotingTimeoutException(
                        "尝试请求 时间(ms) " + timeoutMillis);
            }
        }
    }

    /**
     * 单向调用，这种请求不需要得到响应
     */
    @Override
    public void invokeOneway(final Channel channel, final RemotingCommand request,
                             long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException {
        RemotingHelper.markOnewayRPC(request);
        /**
         * 单向调用请求需要用一个信号量来控制请求数量，防止同一时间内，该请求过多。
         */
        boolean acquired = this.semaphoreOneway.tryAcquire(timeoutMillis,
                TimeUnit.MILLISECONDS);
        if (acquired) {
            final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(
                    this.semaphoreOneway);
            try {
                channel.writeAndFlush(request)
                        .addListener(new ChannelHandlerListener() {
                            @Override
                            public void operationComplete(Future future)
                                    throws Exception {
                                once.release();
                                if (!future.isSuccess()) {
                                    LOGGER.warn("往<" + channel.remoteAddress()
                                            + "> 发送请求指令失败");
                                    LOGGER.warn(request.toString());
                                }
                            }
                        });
            } catch (Exception e) {
                once.release();
                LOGGER.warn("往 <" + channel.remoteAddress() + "> 写入请求指令失败");
                throw new RemotingSendRequestException(
                        RemotingHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            if (timeoutMillis <= 0) {
                throw new RemotingTooMuchRequestException("请求过快");
            } else {
                LOGGER.warn("单向发送请求信号量超时，" + timeoutMillis + " 等待的线程数目:"
                        + this.semaphoreOneway.getQueueLength());
                LOGGER.warn(request.toString());
                throw new RemotingTimeoutException(
                        "请求信号量超时 时间(ms) " + timeoutMillis);
            }
        }
    }

    /**
     * 通信事件处理，目前未使用
     * @author Neuclil
     */
    class RemotingEventExecutor extends ServiceThread {
        private final LinkedBlockingQueue<RemotingEvent> eventQueue = new LinkedBlockingQueue<RemotingEvent>();
        private final int MaxSize = 10000;

        public void putRemotingEvent(final RemotingEvent event) {
            if (this.eventQueue.size() <= MaxSize) {
                this.eventQueue.add(event);
            } else {
                LOGGER.warn("事件队列已满，丢弃当前事件，" + event.toString());
            }
        }

        @Override
        public void run() {
            LOGGER.info(this.getServiceName() + " service started");
            final ChannelEventListener listener = AbstractRemoting.this
                    .getChannelEventListener();

            while (!this.isStopped()) {
                try {
                    /**
                     * 如果为空，会阻塞
                     */
                    RemotingEvent event = this.eventQueue.poll(3000,
                            TimeUnit.MILLISECONDS);
                    if (event != null) {
                        switch (event.getType()) {
                            case ALL_IDLE:
                                listener.onChannelIdle(IdleState.ALL_IDLE,
                                        event.getRemoteAddr(), event.getChannel());
                                break;
                            case WRITER_IDLE:
                                listener.onChannelIdle(IdleState.WRITER_IDLE,
                                        event.getRemoteAddr(), event.getChannel());
                                break;
                            case READER_IDLE:
                                listener.onChannelIdle(IdleState.READER_IDLE,
                                        event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(),
                                        event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(),
                                        event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(),
                                        event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn(
                            this.getServiceName() + " 产生异常 ",
                            e);
                }
            }
            LOGGER.info(this.getServiceName() + " 结束");
        }

        @Override
        public String getServiceName() {
            return RemotingEventExecutor.class.getSimpleName();
        }
    }

    /**
     * 获得编码解码器
     * 目前默认用Java序列化方式的编码解码器
     * @return
     */
    protected Codec getCodec() {
        return new FrobotCodec();
    }

    /**
     * 获得执行回调函数的线程池
     * 因为回调函数可能会执行比较长的时间，如果在主线程中去做，那么可能阻塞主线程
     *
     * @return
     */
    protected abstract ExecutorService getCallbackExecutor();

}
