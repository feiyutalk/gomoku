package edu.hitsz.remoting;

import edu.hitsz.commons.support.Pair;
import edu.hitsz.remoting.exception.RemotingException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Neuclil
 * 抽象的通信服务Server类
 */
public abstract class AbstractRemotingServer extends AbstractRemoting implements RemotingServer {
    /* 日志 */
	private static final Logger LOGGER = Logger.getLogger(AbstractRemotingServer.class);

	/*通信服务Server端的配置信息*/
    protected final RemotingServerConfig remotingServerConfig;
    /*共用的线程池 用于 1.处理器（当用户传入的线程池为空）2. 回调函数执行*/
    private final ExecutorService publicExecutor;
    /*该定时器用于扫描responseTable 清除过时的响应*/
    private final Timer timer = new Timer("server", true);
    /**
     * 构造函数
     * @param remotingServerConfig
     * @param channelEventListener
     */
    public AbstractRemotingServer(final RemotingServerConfig remotingServerConfig,
                                  final ChannelEventListener channelEventListener) {
        super(remotingServerConfig.getServerOnewaySemaphoreValue(), remotingServerConfig.getServerAsyncSemaphoreValue(),
                channelEventListener);
        this.remotingServerConfig = remotingServerConfig;

        int publicThreadNums = remotingServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 4) {
            publicThreadNums = 4;
        }

        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "RemotingServerPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
    }

    /**
     * 启动通信服务，主要有:
     * 1. 启动通信传输这一块功能
     * 2. 启动事件监听器 （未使用）
     * 3. 启动responseTable扫描定时器
     */
    @Override
    public void start() throws RemotingException {
        /*RPC启动*/
        serverStart();

        /*事件处理 未使用 为null*/
        if (channelEventListener != null) {
            this.remotingEventExecutor.start();
        }

        /*每隔1秒扫描一次responseTable*/
        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    AbstractRemotingServer.this.scanResponseTable();
                } catch (Exception e) {
                	LOGGER.error("扫描responseTable出现异常", e);
                }
            }
        }, 1000 * 3, 1000);
    }
    
    /*采用不同的RPC框架有不同的启动方式，留给子类去实现*/
    protected abstract void serverStart();

    /**
     * 关闭通信端服务，主要有:
     * 1. 关闭定时器
     * 2. 关闭事件处理器
     * 3. 关闭RPC
     * 4. 关闭公用线程池
     */
    @Override
    public void shutdown() {
        try {
            if (this.timer != null) {
                this.timer.cancel();
            }

            if (this.remotingEventExecutor != null) {
                this.remotingEventExecutor.shutdown();
            }

            serverShutdown();

        } catch (Exception e) {
        	LOGGER.error("通信服务Server端关闭异常", e);
        }

        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
            	LOGGER.error("通信服务Server端关闭异常", e);
            }
        }
    }
    
    /**
     * 不同的RPC框架有不同的关闭方式，留给子类去实现
     * @throws RemotingException
     */
    protected abstract void serverShutdown() throws RemotingException;
    
    /**
     * 注册默认的请求处理器，需要提供处理器和线程池
     */
    @Override
    public void registerDefaultProcessor(RemotingProcessor processor, ExecutorService executor) {
        this.defaultRequestProcessor = new Pair<RemotingProcessor, ExecutorService>(processor, executor);
    }
    
    /**
     * 根据通信命令注册响应的处理器
     */
    @Override
    public void registerServerProcessor(int requestCode, RemotingProcessor processor, ExecutorService executor) {
        ExecutorService executorThis = executor;
        if (null == executor) {
            executorThis = this.publicExecutor;
        }

        Pair<RemotingProcessor, ExecutorService> pair = new Pair<RemotingProcessor, ExecutorService>(processor,
                executorThis);
        this.processorTable.put(requestCode, pair);
    }
    
    /**
     * 回调函数使用的线程池
     */
    @Override
    protected ExecutorService getCallbackExecutor() {
        return this.publicExecutor;
    }

}
