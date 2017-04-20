package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.ChallengeRequestBody;
import edu.hitsz.remoting.command.body.request.MatchOpponentRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-20.
 */
public class ChallengeRequestProcessor implements RemotingProcessor {
    private ServerApplication application;

    public ChallengeRequestProcessor(ServerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        ChallengeRequestBody body = (ChallengeRequestBody) request.getBody();
        int id = body.getId();
        int oppoId = body.getOppoId();
        UserInfo userInfo = application.getUserManager().getUserInfo(id);
        UserInfo opponentInfo = application.getUserManager().getUserInfo(oppoId);
        if (userInfo != null && opponentInfo != null) {
            application.getGameManager().matchGame(userInfo, opponentInfo);
            userInfo.setWhite(true);
            opponentInfo.setWhite(false);
            MatchOpponentRequestBody body1 = new MatchOpponentRequestBody();
            body1.setOppoId(opponentInfo.getId());
            body1.setImage(opponentInfo.getImage());
            body1.setName(opponentInfo.getName());
            body1.setGender(opponentInfo.getGender());
            body1.setAge(opponentInfo.getAge());
            body1.setFrom(opponentInfo.getFrom());
            body1.setWhite(true);
            RemotingCommand request1 = RemotingCommand.createRequestCommand(
                    RemotingProtos.RequestCode.MATCH.code(),
                    body1);
            RemotingCommand response1 = application.getRemotingServer()
                    .invokeSync(userInfo.getChannel(), request1);
            if (response1.getCode() ==
                    RemotingProtos.ResponseCode.MATCH_SUCCESS.code()) {
                MatchOpponentRequestBody body2 = new MatchOpponentRequestBody();
                body2.setOppoId(userInfo.getId());
                body2.setImage(userInfo.getImage());
                body2.setName(userInfo.getName());
                body2.setGender(userInfo.getGender());
                body2.setAge(userInfo.getAge());
                body2.setFrom(userInfo.getFrom());
                body2.setWhite(false);
                RemotingCommand request2 = RemotingCommand.createRequestCommand(
                        RemotingProtos.RequestCode.MATCH.code(),
                        body2);
                RemotingCommand response2 = application.getRemotingServer()
                        .invokeSync(opponentInfo.getChannel(), request2);
                if (response2.getCode() ==
                        RemotingProtos.ResponseCode.MATCH_SUCCESS.code()) {
                }
            }
        }
        return RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.NULL.code(),
                new NullResponseBody());
    }
}
