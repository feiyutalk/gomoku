package edu.hitsz.remoting;

import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.hitsz.commons.support.Pair;
import edu.hitsz.commons.utils.RemotingHelper;
import edu.hitsz.remoting.exception.RemotingException;
import edu.hitsz.remoting.exception.RemotingSendRequestException;
import org.apache.log4j.Logger;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.exception.RemotingConnectException;
import edu.hitsz.remoting.exception.RemotingTimeoutException;
import edu.hitsz.remoting.exception.RemotingTooMuchRequestException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * 通信服务Client端的抽象父类
 *
 * @author Neuclil
 */
public abstract class AbstractRemotingClient extends AbstractRemoting
        implements RemotingClient {
    /* 日志 */
    protected static final Logger LOGGER = Logger.getLogger(AbstractRemotingClient.class);
    /* 尝试获得锁的超时时间 */
    private static final long LockTimeoutMillis = 3000;
    /* 锁 */
    private Lock lock;
    /* 通信服务客户端的配置信息 */
    protected final RemotingClientConfig remotingClientConfig;
    /* 需要连接的Master */
    private ChannelWrapper master;
    /* 定时器 用于扫描responseTable 清除过时的响应 */
    private final Timer timer = new Timer("Client", true);
    /* 公共的线程池 */
    private final ExecutorService publicExecutor;

    /**
     * 构造函数
     */
    public AbstractRemotingClient(final RemotingClientConfig remotingClientConfig,
                                  final ChannelEventListener channelEventListener) {
        super(remotingClientConfig.getClientOnewaySemaphoreValue(),
                remotingClientConfig.getClientAsyncSemaphoreValue(),
                channelEventListener);
        this.remotingClientConfig = remotingClientConfig;
        lock = new ReentrantLock();

        int publicThreadNums = remotingClientConfig.getClientCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }

        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums,
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "RemotingClientPublicExecutor_"
                                + this.threadIndex.incrementAndGet());
                    }
                });
    }

    /* 通信服务端启动 */
    @Override
    public void start() throws RemotingException {
        /* 具体RPC框架的启动逻辑 底层默认采用Netty框架*/
        clientStart();
        /* 定时器，用于扫描响应表，已清除超时的响应 */
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    AbstractRemotingClient.this.scanResponseTable();
                } catch (Exception e) {
                    LOGGER.error("扫描响应表出错", e);
                }
            }
        }, 1000 * 3, 1000);
        /* 事件处理器 */
        if (this.channelEventListener != null) {
            this.remotingEventExecutor.start();
        }
    }

    /**
     * 不同的RPC框架有不同的通信启动方式，留给子类去重写
     *
     * @throws RemotingException
     */
    protected abstract void clientStart() throws RemotingException;

    /**
     * 通信服务关闭，有几件事情需要做:
     * 1. 关闭扫描响应表的定时器
     * 2. 关闭与Master的连接
     * 3. 关闭客户端通信服务
     * 4. 关闭公共线程池
     */
    @Override
    public void shutdown() {
        try {
            this.timer.cancel();
            this.closeChannel(null, master.channelFuture.getChannel());

            if (this.remotingEventExecutor != null) {
                this.remotingEventExecutor.shutdown();
            }

            clientShutdown();

        } catch (Exception e) {
            LOGGER.error("通信客户端关闭异常", e);
        }

        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                LOGGER.error("关闭异步回调函数线程池异常,", e);
            }
        }
    }

    /**
     * 不同的RPC框架有不同的通信服务关闭方式，留给子类去重写
     */
    protected abstract void clientShutdown();

    /**
     * 用于从Channel表中获得channel，当前的逻辑上只有一个channel
     *
     * @param addr
     * @return
     * @throws InterruptedException
     */
    private Channel getAndCreateChannel(final String addr)
            throws InterruptedException {
        if (master != null && master.isConnected()) {
            return master.getChannel();
        }
        return this.createChannel(addr);
    }

    /**
     * 通过地址连接Master，获得管道
     *
     * @param addr
     * @return
     * @throws InterruptedException
     */
    private Channel createChannel(final String addr) throws InterruptedException {
        if (master != null && master.isConnected()) {
            return master.getChannel();
        }
        /* 用锁来避免建立连接的线程并发问题 */
        if (this.lock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
            try {
                boolean needCreateNewConnect = false;
                if (master != null) {
                    if (master.isConnected()) {
                        return master.getChannel();
                    } else if (!master.getChannelFuture().isDone()) {
                        needCreateNewConnect = false;
                    } else {
                        needCreateNewConnect = true;
                    }
                } else {
                    needCreateNewConnect = true;
                }

                if (needCreateNewConnect) {
                    ChannelFuture channelFuture = connect(RemotingHelper.string2SocketAddress(addr));
                    LOGGER.info("开始尝试建立通信连接:" + addr);
                    master = new ChannelWrapper(channelFuture);
                }
            } catch (Exception e) {
                LOGGER.error("Worker建立通信连接失败", e);
            } finally {
                this.lock.unlock();
            }
        } else {
            LOGGER.warn("在尝试创建通道时，获得锁超时，超时时间:" + LockTimeoutMillis + "ms");
        }

        /* 正常都不会空 */
        if (master != null) {
            /* 默认用的是Netty实现的，返回的是Netty的ChannelFuture */
            ChannelFuture channelFuture = master.getChannelFuture();
            /* 等待指定的时间建立连接 如果在指定的时间内完成，则返回true，否则返回false */
            if (channelFuture.awaitUninterruptibly(this.remotingClientConfig.getConnectTimeoutMillis())) {
                if (master.isConnected()) {
                    LOGGER.info("建立远程连接成功:" + addr + "," + channelFuture.toString());
                    return master.getChannel();
                } else {
                    LOGGER.warn("建立远程连接失败:" + addr + ","
                                    + channelFuture.toString(),
                            channelFuture.cause());
                }
            } else {
                LOGGER.warn("建立通信连接超时:" + addr + "，超时时间"
                        + this.remotingClientConfig.getConnectTimeoutMillis()
                        + ", " + channelFuture.toString());
            }
        }
        return null;
    }

    /**
     * 不同的RPC框架有不同的连接方式，留给子类去实现
     *
     * @param socketAddress
     * @return
     */
    protected abstract ChannelFuture connect(SocketAddress socketAddress);

    /**
     * 关闭连接
     *
     * @param addr
     * @param channel
     */
    public void closeChannel(final String addr, final Channel channel) {
        if (null == channel)
            return;

        final String addrRemote = null == addr ?
                RemotingHelper.parseChannelRemoteAddr(channel) : addr;

        try {
            if (this.lock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    boolean needToBeRemoved = true;
                    LOGGER.info("closeChannel:开始尝试关闭远程连接:" + addrRemote + ", 找到远程连接管道:"
                            + (master != null));
                    if (null == master) {
                        LOGGER.info("closeChannel:远程连接在之前已经被关闭:" + addrRemote);
                        needToBeRemoved = false;
                    } else if (master.getChannel() != channel) {
                        LOGGER.info("closeChannel:远程连接在之前已经被关闭，然后又被创建，不需要处理:" + addrRemote);
                        needToBeRemoved = false;
                    }

                    if (needToBeRemoved) {
                        LOGGER.info("closeChannel:远程连接被关闭 " + addrRemote);
                    }
                    RemotingHelper.closeChannel(channel);
                } catch (Exception e) {
                    LOGGER.error("closeChannel:关闭远程连接发生异常", e);
                } finally {
                    this.lock.unlock();
                }
            } else {
                LOGGER.warn("closeChannel: 尝试获得锁超时 " + LockTimeoutMillis);
            }
        } catch (InterruptedException e) {
            LOGGER.error("关闭连接异常", e);
        }
    }

    public void closeChannel(final Channel channel) {
        closeChannel(null, channel);
    }

    /**
     * 注册默认的请求处理器
     */
    @Override
    public void registerDefaultProcessor(RemotingProcessor processor,
                                         ExecutorService executor) {
        this.defaultRequestProcessor = new Pair<RemotingProcessor, ExecutorService>(
                processor, executor);
    }

    /**
     * 根据通信指令注册请求处理器
     */
    @Override
    public void registerClientProcessor(int requestCode,
                                        RemotingProcessor processor, ExecutorService executor) {
        ExecutorService executorThis = executor;
        if (null == executor) {
            executorThis = this.publicExecutor;
        }

        Pair<RemotingProcessor, ExecutorService> pair = new Pair<RemotingProcessor, ExecutorService>(
                processor, executorThis);
        this.processorTable.put(requestCode, pair);
    }

    /**
     * 得到执行回调函数的线程池
     */
    @Override
    protected ExecutorService getCallbackExecutor() {
        return this.publicExecutor;
    }

    private class ChannelWrapper {
        private final ChannelFuture channelFuture;

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        public boolean isConnected() {
            return channelFuture.isConnected();
        }

        private Channel getChannel() {
            return channelFuture.getChannel();
        }

        private ChannelFuture getChannelFuture() {
            return this.channelFuture;
        }
    }

    /**
     * 根据地址 发送同步请求
     *
     * @param addr
     * @param request
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     * @throws RemotingConnectException
     * @throws RemotingSendRequestException
     * @throws RemotingTimeoutException
     */
    @Override
    public RemotingCommand invokeSync(String addr, RemotingCommand request, long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isConnected()) {
            try {
                return this.invokeSync(channel, request, timeoutMillis);
            } catch (RemotingSendRequestException e) {
                LOGGER.warn("同步请求发生异常");
                throw e;
            } catch (RemotingTimeoutException e) {
                LOGGER.warn("同步请求超时");
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new RemotingConnectException(addr);
        }
    }

    /**
     * 根据地址 发送异步请求
     *
     * @throws InterruptedException
     * @throws RemotingTimeoutException
     * @throws RemotingTooMuchRequestException
     * @throws RemotingSendRequestException
     * @throws RemotingConnectException
     */
    @Override
    public void invokeAsync(String addr, RemotingCommand request,
                            long timeoutMillis, AsyncCallback asyncCallback) throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isConnected()) {
            try {
                this.invokeAsync(channel, request, timeoutMillis, asyncCallback);
            } catch (RemotingSendRequestException e) {
                LOGGER.warn("异步请求发生异常");
                throw e;
            }
        } else {
            throw new RemotingConnectException(addr);
        }
    }

    /**
     * 根据地址 发送单向请求
     *
     * @throws RemotingTimeoutException
     * @throws RemotingTooMuchRequestException
     * @throws RemotingSendRequestException
     */
    @Override
    public void invokeOneway(String addr, RemotingCommand request,
                             long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel != null && channel.isConnected()) {
            try {
                this.invokeOneway(channel, request, timeoutMillis);
            } catch (RemotingSendRequestException e) {
                LOGGER.warn("单向请求异常");
                throw e;
            }
        } else {
            throw new RemotingConnectException(addr);
        }
    }
}
