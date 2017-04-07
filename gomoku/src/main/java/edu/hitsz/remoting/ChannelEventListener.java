package edu.hitsz.remoting;

public interface ChannelEventListener {

    public void onChannelConnect(final String remoteAddr, final Channel channel);

    public void onChannelClose(final String remoteAddr, final Channel channel);

    public void onChannelException(final String remoteAddr, final Channel channel);

    public void onChannelIdle(IdleState idleState, final String remoteAddr, final Channel channel);

}