package edu.hitsz.remoting.delegate;

import org.apache.log4j.Logger;
import edu.hitsz.remoting.AsyncCallback;
import edu.hitsz.remoting.RemotingClient;
import edu.hitsz.remoting.RemotingClientConfig;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.exception.RemotingException;
import edu.hitsz.remoting.netty.NettyRemotingClient;
import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

/**
 * Worker端通信服务代理类
 *
 * @author Neuclil
 */
public class RemotingClientDelegate {
    /* 日志 */
    private static final Logger LOGGER = Logger.getLogger(RemotingClientDelegate.class);
    /* 远程通信client 底层默认采用NettyRPC框架 */
    private RemotingClient remotingClient;
    /* 远程通信client配置信息 */
    private RemotingClientConfig remotingClientConfig;

    /**
     * 构造函数
     */
    public RemotingClientDelegate() {
        remotingClientConfig = new RemotingClientConfig();
        remotingClient = new NettyRemotingClient(remotingClientConfig);
    }

    /**
     * 构造函数
     */
    public RemotingClientDelegate(RemotingClient remotingClient) {
        this.remotingClient = remotingClient;
    }

    /**
     * 通信端启动
     */
    public void start() {
        try {
            remotingClient.start();
        } catch (RemotingException e) {
            LOGGER.error("Worker通信端启动失败！", e);
        }
    }

    /**
     * 发送同步请求
     *
     * @param addr
     * @param request
     * @return
     */
    public RemotingCommand invokeSync(String addr, RemotingCommand request) {
        try {
            RemotingCommand response = remotingClient.invokeSync(addr, request, remotingClientConfig.getInvokeTimeoutMillis());
            return response;
        } catch (Exception e) {
            LOGGER.error("发送同步请求异常!", e);
            return null;
        }
    }

    /**
     * 发送异步请求
     * @param addr
     * @param request
     * @param asyncCallback
     */
    public void invokeAsync(String addr, RemotingCommand request, AsyncCallback asyncCallback) {
        try {
            remotingClient.invokeAsync(addr, request, remotingClientConfig.getInvokeTimeoutMillis(), asyncCallback);
        } catch (Exception e) {
            LOGGER.error("发送异步请求异常!", e);
        }
    }

    /**
     * 发送单向请求
     * @param addr
     * @param request
     */
    public void invokeOneway(String addr, RemotingCommand request) {
        try {
            remotingClient.invokeOneway(addr, request, remotingClientConfig.getInvokeTimeoutMillis());
        } catch (Exception e) {
            LOGGER.error("发送单向请求异常!", e);
        }
    }

    /**
     * 注册处理器
     * @param requestCode
     * @param processor
     * @param executor
     */
    public void registerClientProcessor(int requestCode, RemotingProcessor processor, ExecutorService executor) {
        remotingClient.registerClientProcessor(requestCode, processor, executor);
    }

    /**
     * 注册默认处理器
     * @param processor
     * @param executor
     */
    public void registerDefaultProcessor(RemotingProcessor processor, ExecutorService executor) {
        remotingClient.registerDefaultProcessor(processor, executor);
    }

    /**
     * 关闭
     */
    public void shutdown() {
        remotingClient.shutdown();
    }

    /**
     * 获得真正的通信实体
     * @return
     */
    public RemotingClient getRemotingClient() {
        return remotingClient;
    }
}
