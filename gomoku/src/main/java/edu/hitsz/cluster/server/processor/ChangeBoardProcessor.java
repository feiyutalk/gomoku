package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.ChangBoardRequestBody;
import edu.hitsz.remoting.command.body.request.LoseRequestBody;
import edu.hitsz.remoting.command.body.request.NullRequestBody;
import edu.hitsz.remoting.command.body.request.OpponentChangeBoardRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ChangeBoardProcessor implements RemotingProcessor {
    private static final Logger LOG = Logger.getLogger(ChangeBoardProcessor.class);
    private ServerApplication application;

    public ChangeBoardProcessor(ServerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOG.debug("ChangeBoardProcessor正在处理请求....");
        ChangBoardRequestBody body = (ChangBoardRequestBody) request.getBody();
        int id = body.getId();
        int y = body.getY();
        int x = body.getX();
        boolean white = body.isWhite();
        //改变Server端自己的棋盘
        boolean success = application.getUserManager().changeUserBoard(id, white, y, x);
        UserInfo userInfo = application.getUserManager().getUserInfo(id);
        userInfo.setLastY(y);
        userInfo.setLastX(x);
        RemotingCommand response = null;
        if (success) {
            //改变Server端对手的棋盘
            Integer opponentId = application.getGameManager().getOpponentId(id);
            application.getUserManager().changeUserBoard(opponentId, white, y, x);
            UserInfo opponentUserInfo = application.getUserManager().getUserInfo(opponentId);
            //判断是否赢了
            boolean win = userInfo.isWin(y, x);
            if(win){
                RemotingCommand myRequest = RemotingCommand.createRequestCommand(
                        RemotingProtos.RequestCode.WIN.code(),
                        new NullRequestBody());
                application.getRemotingServer().invokeOneway(userInfo.getChannel(), myRequest);

                RemotingCommand opponentRequest = RemotingCommand.createRequestCommand(
                        RemotingProtos.RequestCode.LOSE.code(),
                        new LoseRequestBody(white, y, x));
                application.getRemotingServer().invokeOneway(opponentUserInfo.getChannel(), opponentRequest);
                LOG.debug("ChangeBoardProcessor处理请求完成!");
                return RemotingCommand.createResponseCommand(
                        RemotingProtos.ResponseCode.CHANGE_BOARD_SUCCESS.code(),
                        new NullResponseBody());
            }else{
                //改变Client端对手的棋盘
                RemotingCommand requestForOpponent = RemotingCommand.createRequestCommand(
                        RemotingProtos.RequestCode.OPPONENT_CHANGE_BOARD.code(),
                        new OpponentChangeBoardRequestBody(white, y, x));
                RemotingCommand opponentResponse = application.getRemotingServer().invokeSync(opponentUserInfo.getChannel(),
                        requestForOpponent);
                if(opponentResponse.getCode() == RemotingProtos.ResponseCode.CHANGE_OPPONENT_BOARD_SUCCESS.code()){
                    response = RemotingCommand.createResponseCommand(
                            RemotingProtos.ResponseCode.CHANGE_BOARD_SUCCESS.code(),
                            new NullResponseBody());
                }else{
                    response = RemotingCommand.createResponseCommand(
                            RemotingProtos.ResponseCode.CHANGE_BOARD_FAILURE.code(),
                            new NullResponseBody());
                }
                LOG.debug("ChangeBoardProcessor处理请求完成!");
                return response;
            }
        } else {
            response = RemotingCommand.createResponseCommand(
                    RemotingProtos.ResponseCode.CHANGE_BOARD_FAILURE.code(),
                    new NullResponseBody());
            LOG.debug("ChangeBoardProcessor处理请求完成!");
            return response;
        }
    }
}
