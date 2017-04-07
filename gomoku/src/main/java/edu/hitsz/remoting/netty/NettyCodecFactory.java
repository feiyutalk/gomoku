package edu.hitsz.remoting.netty;

import edu.hitsz.commons.utils.RemotingHelper;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.codec.Codec;
import edu.hitsz.remoting.command.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.apache.log4j.Logger;
import edu.hitsz.commons.constants.Constants;

import java.nio.ByteBuffer;

/**
 * Netty编码解码器工厂
 * @author Neuclil
 *
 */
public class NettyCodecFactory {
	private static final Logger LOGGER = Logger.getLogger(NettyCodecFactory.class);
	/**
	 * 编码解码器
	 */
    private Codec codec;
    /**
     * 构造函数
     * @param codec
     */
    public NettyCodecFactory(Codec codec) {
        super();
        this.codec = codec;
    }
    /**
     * Netty编码类
     * 主要用于将RemotingCommand编码为字节流存放到ByteBuffer中
     * 需要调用我们根据通信协议定义好的编码解码器codec
     * @author Neuclil
     *
     */
    public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {

        @Override
        protected void encode(ChannelHandlerContext ctx, RemotingCommand cmd, ByteBuf out) throws Exception {
            if (cmd == null) {
            	LOGGER.error("Message为空");
            	return;
            }
            try {
                ByteBuffer byteBuffer = codec.encode(cmd);
                out.writeBytes(byteBuffer);
            } catch (Exception e) {
                Channel channel = new NettyChannel(ctx);
                LOGGER.error("编码异常, addr= " + RemotingHelper.parseChannelRemoteAddr(channel)
                				+ "remotingCommand= " + cmd.toString(), e);
                RemotingHelper.closeChannel(channel);
            }
        }
    }
    
    /**
     * Netty解码类
     * 用于将通信的字节流解码为RemotingCommand
     * 需要用到我们根据通信协议写好的解码器
     * @author Neuclil
     *
     */
    public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    	
        private static final int FRAME_MAX_LENGTH = Constants.DEFAULT_BUFFER_SIZE;

        public NettyDecoder() {
            super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
            try {
                ByteBuf frame = (ByteBuf) super.decode(ctx, in);
                if (frame == null) {
                    return null;
                }
                byte[] tmpBuf = new byte[frame.capacity()];
                frame.getBytes(0, tmpBuf);
                frame.release();

                ByteBuffer byteBuffer = ByteBuffer.wrap(tmpBuf);
                return codec.decode(byteBuffer);
            } catch (Exception e) {
                Channel channel = new NettyChannel(ctx);
                LOGGER.error("解码异常," + RemotingHelper.parseChannelRemoteAddr(channel), e);
                RemotingHelper.closeChannel(channel);
            }
            return null;
        }
    }

    public ChannelHandler getEncoder() {
        return new NettyEncoder();
    }

    public ChannelHandler getDecoder() {
        return new NettyDecoder();
    }
}
