package edu.hitsz.remoting;

public interface ChannelFuture {

    boolean isConnected();

    Channel getChannel();

    boolean awaitUninterruptibly(long timeoutMillis);

    boolean isDone();

    Throwable cause();
}
