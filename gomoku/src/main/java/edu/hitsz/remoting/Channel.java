package edu.hitsz.remoting;

import java.net.SocketAddress;


public interface Channel {

    SocketAddress localAddress();

    SocketAddress remoteAddress();

    ChannelHandler writeAndFlush(Object msg);

    ChannelHandler close();

    boolean isConnected();

    boolean isOpen();

    boolean isClosed();
}
