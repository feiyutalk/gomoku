package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.commons.support.GameBoot;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-20.
 */
public class OpponentStartProcessor implements RemotingProcessor{
    private ClientApplication application;

    public OpponentStartProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        GameBoot.start(application);
        return RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.NULL.code(),
                new NullResponseBody());
    }
}
