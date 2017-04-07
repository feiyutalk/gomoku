package edu.hitsz.remoting.netty;

import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.ChannelFuture;

/**
 * 该类是用了NettyRPC框架的ChannelFuture功能，实现了本系统的ChannelFuture接口
 * 
 * @author Neuclil
 *
 */
public class NettyChannelFuture implements ChannelFuture {

	/**
	 * Netty的ChannleFuture类，用于封装Channel上的一些异步I/O处理结果
	 */
	private io.netty.channel.ChannelFuture channelFuture;

	/**
	 * 构造函数
	 * 
	 * @param channelFuture
	 */
	public NettyChannelFuture(io.netty.channel.ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}

	/**
	 * 判断是否连接成功
	 */
	@Override
	public boolean isConnected() {
		return channelFuture.channel() != null
				&& channelFuture.channel().isActive();
	}

	/**
	 * 获得实际传输数据的通道 channel 注意： Netty的Channel也是实现系统通信框架的Channel接口的
	 */
	@Override
	public Channel getChannel() {
		return new NettyChannel(channelFuture.channel());
	}

	/**
	 * Waits for this future to be completed without interruption. This method
	 * catches an InterruptedException and discards it silently.
	 */
	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		return channelFuture.awaitUninterruptibly(timeoutMillis);
	}

	/**
	 * 判断异常处理结果是否完成
	 */
	@Override
	public boolean isDone() {
		return channelFuture.isDone();
	}

	/**
	 * Returns the cause of the failed I/O operation if the I/O operation has
	 * failed.
	 */
	@Override
	public Throwable cause() {
		return channelFuture.cause();
	}
}
