package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.Application;
import edu.hitsz.cluster.server.UserInfo;
import edu.hitsz.cluster.server.UserManager;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.ConnectRequestBody;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-9.
 */
public class ConnectProcessor implements RemotingProcessor {
    private Application application;

    public ConnectProcessor(Application application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        UserManager userManager = application.getUserManager();
        ConnectRequestBody body = (ConnectRequestBody) request.getBody();
        UserInfo userInfo = body.getUserInfo();
        userManager.add(channel, userInfo);
        return null;
    }
}
