package edu.hitsz.remoting.delegate;

import edu.hitsz.remoting.*;
import org.apache.log4j.Logger;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.netty.NettyRemotingServer;
import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

/**
 * 通信服务代理对象
 */
public class RemotingServerDelegate {
    /* 日志 */
    private static final Logger LOGGER = Logger.getLogger(RemotingServerDelegate.class);
    /* 真正通信的实体对象 默认底层采用NettyRPC框架 */
    private RemotingServer remotingServer;
    /* 通信服务端配置信息 */
    private RemotingServerConfig remotingServerConfig;

    /**
     * 构造函数
     */
    public RemotingServerDelegate() {
        remotingServerConfig = new RemotingServerConfig();
        remotingServer = new NettyRemotingServer(remotingServerConfig);
    }

    /**
     * 构造函数
     */
    public RemotingServerDelegate(RemotingServer remotingServer) {
        this.remotingServer = remotingServer;
    }

    public RemotingServerDelegate(RemotingServerConfig config,
                                  ChannelEventListener channelEventListener) {
        remotingServerConfig = config;
        remotingServer = new NettyRemotingServer(config, channelEventListener);
    }

    public void start() {
        try {
            remotingServer.start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void registerServerProcessor(int requestCode, RemotingProcessor processor, ExecutorService executor) {
        remotingServer.registerServerProcessor(requestCode, processor, executor);
    }

    public void registerDefaultProcessor(RemotingProcessor processor, ExecutorService executor) {
        remotingServer.registerDefaultProcessor(processor, executor);
    }

    public RemotingCommand invokeSync(Channel channel, RemotingCommand request) {
        try {
            return remotingServer.invokeSync(channel, request, remotingServerConfig.getInvokeTimeoutMillis());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void invokeAsync(Channel channel, RemotingCommand request, AsyncCallback asyncCallback) {
        try {
            remotingServer.invokeAsync(channel, request, remotingServerConfig.getInvokeTimeoutMillis(), asyncCallback);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void invokeOneway(Channel channel, RemotingCommand request) {
        try {
            remotingServer.invokeOneway(channel, request, remotingServerConfig.getInvokeTimeoutMillis());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void shutdown() {
        remotingServer.shutdown();
    }
}
