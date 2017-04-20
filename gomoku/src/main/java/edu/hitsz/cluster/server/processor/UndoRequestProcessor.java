package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.UndoRequstBody;
import edu.hitsz.remoting.command.body.response.UndoResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-20.
 */
public class UndoRequestProcessor implements RemotingProcessor {
    private ServerApplication application;

    public UndoRequestProcessor(ServerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        UndoRequstBody body = (UndoRequstBody) request.getBody();
        int id = body.getId();
        int lastX = body.getLastX();
        int lastY = body.getLastY();

        UserInfo userInfo = application.getUserManager().getUserInfo(id);
        Integer opponentId = application.getGameManager().getOpponentId(id);
        UserInfo opponentUserInfo = application.getUserManager().getUserInfo(opponentId);
        RemotingCommand response = application.getRemotingServer().invokeSync(
                opponentUserInfo.getChannel(),
                RemotingCommand.createRequestCommand(
                        RemotingProtos.RequestCode.OPPONENT_UNDO.code(),
                        new UndoRequstBody(id, lastY, lastX)));
        userInfo.undo();
        opponentUserInfo.undo();
        return RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.UNDO_SUCCESS.code(),
                new UndoResponseBody(opponentUserInfo.getLastY(), opponentUserInfo.getLastX())
        );
    }
}
