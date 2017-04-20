package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.client.RemoteUserInfo;
import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.cluster.server.manager.UserState;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.ConnectRequestBody;
import edu.hitsz.remoting.command.body.response.ConnectResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Neuclil on 17-4-9.
 */
public class ConnectProcessor implements RemotingProcessor {
    private static final Logger LOG = Logger.getLogger(ConnectProcessor.class);
    private static AtomicInteger counter = new AtomicInteger(1);
    private ServerApplication application;

    public ConnectProcessor(ServerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOG.debug("ConnectProcessor正在处理请求...");
        ConnectRequestBody body = (ConnectRequestBody)request.getBody();
        RemoteUserInfo remoteUserInfo = body.getRemoteUserInfo();
        int id =  counter.getAndIncrement();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserState(UserState.WAITING);
        userInfo.setChannel(channel);
        userInfo.setId(id);
        userInfo.setName(remoteUserInfo.getName());
        userInfo.setGender(remoteUserInfo.getGender());
        userInfo.setImage(remoteUserInfo.getImage());
        userInfo.setAge(remoteUserInfo.getAge());
        userInfo.setFrom(remoteUserInfo.getFrom());
        application.getUserManager().addUser(userInfo);
        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.CONNECT_SUCCESS.code(),
                new ConnectResponseBody(id));
        LOG.debug("ConnectProcessor处理请求完成!");
        return response;
    }
}
