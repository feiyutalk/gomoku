package edu.hitsz.remoting.netty;

import edu.hitsz.commons.utils.RemotingHelper;
import edu.hitsz.remoting.*;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.net.SocketAddress;

/**
 * @author Neuclil
 */
public class NettyRemotingClient extends AbstractRemotingClient {
    /* 日志 */
    private static final Logger LOGGER = Logger.getLogger(NettyRemotingClient.class);
    /* NettyRPC启动引导对象 */
    private final Bootstrap bootstrap = new Bootstrap();
    /* NettyRPC连接池 */
    private final EventLoopGroup eventLoopGroup;

    /**
     * 构造函数
     */
    public NettyRemotingClient(final RemotingClientConfig remotingClientConfig) {
        this(remotingClientConfig, null);
    }

    /**
     * 构造函数
     */
    public NettyRemotingClient(final RemotingClientConfig remotingClientConfig,
                               final ChannelEventListener channelEventListener) {
        super(remotingClientConfig, channelEventListener);
        this.eventLoopGroup = new NioEventLoopGroup(remotingClientConfig.getClientSelectorThreads());
    }

    /**
     * Netty客户端启动
     *
     * @throws RemotingException
     */
    @Override
    protected void clientStart() throws RemotingException {
        final NettyCodecFactory nettyCodecFactory = new NettyCodecFactory(getCodec());

        this.bootstrap.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(nettyCodecFactory.getEncoder()).addLast(nettyCodecFactory.getDecoder())
//                        .addLast(new IdleStateHandler(remotingClientConfig.getReaderIdleTimeSeconds(),
//                                remotingClientConfig.getWriterIdleTimeSeconds(),
//                                remotingClientConfig.getClientChannelMaxIdleTimeSeconds()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    /* Netty客户端关闭 */
    @Override
    protected void clientShutdown() {
        this.eventLoopGroup.shutdownGracefully();
    }

    /**
     * 建立连接
     *
     * @param socketAddress
     * @return ChannelFuture
     */
    @Override
    protected edu.hitsz.remoting.ChannelFuture connect(SocketAddress socketAddress) {
        io.netty.channel.ChannelFuture channelFuture = this.bootstrap.connect(socketAddress);
        return new NettyChannelFuture(channelFuture);
    }

    /**
     * Netty IO Handler
     */
    class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) throws Exception {
            if (RemotingHelper.isRequestCommand(cmd)) {
                LOGGER.debug("收到请求指令:" + RemotingProtos.RequestCode.valueOf(cmd.getCode()));
            } else {
                LOGGER.debug("收到响应指令:" + RemotingProtos.ResponseCode.valueOf(cmd.getCode()));
            }
            processMessageReceived(new NettyChannel(ctx), cmd);
        }
    }
}
