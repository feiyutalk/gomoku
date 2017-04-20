package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.NullRequestBody;
import edu.hitsz.remoting.command.body.request.StartRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-20.
 */
public class StartRequestProcessor implements RemotingProcessor{
    private ServerApplication application;

    public StartRequestProcessor(ServerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        StartRequestBody body = (StartRequestBody) request.getBody();
        int id = body.getId();
        UserInfo opponentInfo = application.getGameManager().getOpponentInfo(id);
        if(opponentInfo != null){
            application.getRemotingServer().invokeOneway(opponentInfo.getChannel(),
                    RemotingCommand.createRequestCommand(
                            RemotingProtos.RequestCode.OPPONENT_START.code(),
                            new NullRequestBody()));
        }
        return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.NULL.code(),
                new NullResponseBody());
    }
}
