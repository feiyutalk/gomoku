package edu.hitsz.cluster.client;

import edu.hitsz.cluster.server.UserInfo;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.ConnectRequestBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.delegate.RemotingClientDelegate;

/**
 * Created by Neuclil on 17-4-9.
 */
public class Client {

    public static void main(String[] args) {
        RemotingClientDelegate remotingClientDelegate =
                new RemotingClientDelegate();
        UserInfo userInfo = new UserInfo();
        RemotingCommand request = RemotingCommand.createRequestCommand(RemotingProtos.RequestCode.CONNECT.code(),
                new ConnectRequestBody(userInfo));
        remotingClientDelegate.invokeSync("localhost:8000", request);
    }
}
