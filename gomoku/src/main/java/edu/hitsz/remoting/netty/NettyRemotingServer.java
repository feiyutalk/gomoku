package edu.hitsz.remoting.netty;

import edu.hitsz.commons.utils.RemotingHelper;
import edu.hitsz.remoting.*;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
/**
 * 具体的通信服务Server端，本系统默认采用NettyRPC框架
 * @author Neuclil
 */
public class NettyRemotingServer extends AbstractRemotingServer {
    /* 日志 */
    private static final Logger LOGGER = Logger.getLogger(NettyRemotingServer.class);
    
    /*NettyRPC框架的启动引导程序*/
    private final ServerBootstrap serverBootstrap;

    /*Netty线程Reactor线程模型需要的两个线程池*/
    private final EventLoopGroup bossSelectorGroup;
    private final EventLoopGroup workerSelectorGroup;
    /**
     * 构造函数
     * @param remotingServerConfig
     */
    public NettyRemotingServer(RemotingServerConfig remotingServerConfig) {
        this(remotingServerConfig, null);
    }
    /**
     * 构造函数
     * @param remotingServerConfig
     * @param channelEventListener
     */
    public NettyRemotingServer(RemotingServerConfig remotingServerConfig, final ChannelEventListener channelEventListener) {
        super(remotingServerConfig, channelEventListener);
        this.serverBootstrap = new ServerBootstrap();
        this.bossSelectorGroup = new NioEventLoopGroup(1);
        this.workerSelectorGroup = new NioEventLoopGroup(remotingServerConfig.getServerSelectorThreads());
    }
    
    /**
     * Netty服务端启动
     */
    @Override
    protected void serverStart() {
    	/*编码解码器工厂类，该类可以获得编码解码器，默认的编码解码器采用Java序列化方式*/
        final NettyCodecFactory nettyCodecFactory = new NettyCodecFactory(getCodec());
        /*Netty引导程序启动流程*/
        this.serverBootstrap.group(this.bossSelectorGroup, this.workerSelectorGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 65535)//最大连接数量
                .option(ChannelOption.SO_KEEPALIVE, true)//保持长连接
                .localAddress(new InetSocketAddress(this.remotingServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(nettyCodecFactory.getEncoder())//注册编码器
                        		   	 .addLast(nettyCodecFactory.getDecoder())//注册解码器
                        			 .addLast(new NettyConnectManageHandler())//注册连接处理器
                        			 .addLast(new NettyServerHandler());//注册IO处理器
                    }

                });
        try {
            this.serverBootstrap.bind().sync();
            LOGGER.debug("Netty通信服务Server端启动");
            LOGGER.debug("Netty通信服务Server端配置信息 : " + remotingServerConfig);
        } catch (Exception e) {
            LOGGER.error("Netty通信服务Server端启动失败!",e);
        }
    }

    /**
     * 通信服务关闭
     * @throws RemotingException
     */
    @Override
    protected void serverShutdown() throws RemotingException {
        this.bossSelectorGroup.shutdownGracefully();
    }

    /**
     * Netty通信RPC IO Handler
     * @author Neuclil
     */
    class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) throws Exception {
            if(RemotingHelper.isRequestCommand(cmd)){
            	LOGGER.debug("收到请求指令:" + RemotingProtos.RequestCode.valueOf(cmd.getCode()));
            }else{
            	LOGGER.debug("收到响应指令:" + RemotingProtos.ResponseCode.valueOf(cmd.getCode()));
            }
            processMessageReceived(new NettyChannel(ctx), cmd);
        }
    }
    
    /**
     * Netty通信RPC Connect Handler
     * @author Neuclil
     */
    class NettyConnectManageHandler extends ChannelDuplexHandler {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        	final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(new NettyChannel(ctx));
        	LOGGER.info("channel注册成功:" + remoteAddress);
        	super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        	final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(new NettyChannel(ctx));
        	LOGGER.info("channel取消注册成功:"+remoteAddress);
        	super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            edu.hitsz.remoting.Channel channel = new NettyChannel(ctx);
            String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
            LOGGER.debug("客户端连接成功: " + remoteAddress);
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = new NettyChannel(ctx);
            String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
            LOGGER.info("客户端断开链接: " + remoteAddress);
            super.channelInactive(ctx);
            if(channelEventListener != null){
                putRemotingEvent(new RemotingEvent(RemotingEventType.CLOSE, remoteAddress, channel));
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            Channel channel = new NettyChannel(ctx);
            String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
            LOGGER.warn("通信连接出现异常,Worker地址:" + remoteAddress);
            LOGGER.warn("通信连接出现异常:" + cause);
            super.exceptionCaught(ctx, cause);
            RemotingHelper.closeChannel(channel);
        }

    }
}
