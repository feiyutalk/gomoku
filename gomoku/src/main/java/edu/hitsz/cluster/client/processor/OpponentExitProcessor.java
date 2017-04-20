package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.Client;
import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.cluster.client.state.GameState;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-20.
 */
public class OpponentExitProcessor implements RemotingProcessor{
    private ClientApplication application;

    public OpponentExitProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        if(Client.gameState == GameState.TURNING ||
                Client.gameState == GameState.WAITING){
            application.getBoard().exitWin();
        }else {
            application.getBoard().exit();
        }

        application.getBoard().reset();
        return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.NULL.code(),
                new NullResponseBody());
    }
}
