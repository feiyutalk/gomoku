package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.UndoRequstBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-20.
 */
public class OpponentUndoProcessor implements RemotingProcessor{
    private ClientApplication application;

    public OpponentUndoProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        UndoRequstBody body = (UndoRequstBody) request.getBody();
        int lastY = body.getLastY();
        int lastX = body.getLastX();
        application.getBoard().undo(lastY, lastX);
        application.getBoard().undo();
        return RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.NULL.code(),
                new NullResponseBody());
    }
}
