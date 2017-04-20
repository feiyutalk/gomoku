package edu.hitsz.cluster.server;

import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.cluster.server.manager.UserState;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.ChannelEventListener;
import edu.hitsz.remoting.IdleState;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.NullRequestBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;

/**
 * Created by Neuclil on 17-4-20.
 */
public class ServerChannelEventListener implements ChannelEventListener{
    private ServerApplication application;

    public ServerChannelEventListener(ServerApplication application) {
        this.application = application;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    /**
     *
     * @param remoteAddr
     * @param channel
     */
    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        System.out.println("onChannelClose");
        UserInfo userInfo = application.getUserManager().remove(channel);
        if(null != userInfo && userInfo.getUserState() == UserState.GAMING){
            Integer opponentId = application.getGameManager().getOpponentId(userInfo.getId());
            application.getGameManager().removeMatch(userInfo.getId());
            UserInfo opponentUserInfo = application.getUserManager().getUserInfo(opponentId);
            opponentUserInfo.setUserState(UserState.WAITING);
            RemotingCommand request = RemotingCommand.createRequestCommand(
                    RemotingProtos.RequestCode.OPPONENT_EXIT.code(),
                    new NullRequestBody());
            if(opponentId!=null)
                application.getRemotingServer().invokeOneway(
                        opponentUserInfo.getChannel(),
                        request);
        }
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelIdle(IdleState idleState, String remoteAddr, Channel channel) {

    }
}
