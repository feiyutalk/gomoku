package edu.hitsz.remoting;

import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

public interface RemotingServer extends Remoting {

    public void registerServerProcessor(final int requestCode, final RemotingProcessor processor,
                                        final ExecutorService executor);
}