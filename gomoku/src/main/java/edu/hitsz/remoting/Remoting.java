package edu.hitsz.remoting;

import edu.hitsz.remoting.exception.RemotingSendRequestException;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.exception.RemotingException;
import edu.hitsz.remoting.exception.RemotingTimeoutException;
import edu.hitsz.remoting.exception.RemotingTooMuchRequestException;
import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

public interface Remoting {

    public void start() throws RemotingException;

    public void shutdown();

    public void processRequestCommand(final Channel channel, final RemotingCommand cmd);

    public void processResponseCommand(Channel channel, RemotingCommand cmd);

    public void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;

    public RemotingCommand invokeSync(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException;

    public void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                            final AsyncCallback asyncCallback) throws InterruptedException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;

    public void registerDefaultProcessor(final RemotingProcessor processor, final ExecutorService executor);
}
