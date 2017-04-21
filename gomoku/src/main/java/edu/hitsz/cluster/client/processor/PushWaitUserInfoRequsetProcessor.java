package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.PushUserInfoRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.List;

/**
 * Created by Neuclil on 17-4-20.
 */
public class PushWaitUserInfoRequsetProcessor implements RemotingProcessor{
    private ClientApplication application;

    public PushWaitUserInfoRequsetProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        PushUserInfoRequestBody body = (PushUserInfoRequestBody) request.getBody();
        List<UserInfo> waitUsers = body.getWaitUsers();
        application.getBoard().clearPlayerList();
        for(int i =0; i<waitUsers.size(); i++){
            UserInfo userInfo = waitUsers.get(i);
            int id = userInfo.getId();
            String name = userInfo.getName();
            String info = id + "-" + name;
            application.getBoard().addPlayer(info);
        }
        application.getBoard().getWaitPlayerText().setText(waitUsers.size()+"");
        application.getBoard().getPlayerList().repaint();
        return RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.NULL.code(),
                new NullResponseBody());
    }
}
