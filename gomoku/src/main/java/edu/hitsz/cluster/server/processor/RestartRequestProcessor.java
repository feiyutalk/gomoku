package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.NullRequestBody;
import edu.hitsz.remoting.command.body.request.RestartRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

/**
 * Created by Neuclil on 17-4-20.
 */
public class RestartRequestProcessor implements RemotingProcessor {
    private static final Logger LOG = Logger.getLogger(RestartRequestProcessor.class);
    private ServerApplication application;

    public RestartRequestProcessor(ServerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        LOG.debug("RestartRequestProcessor正在处理请求....");
        RestartRequestBody body = (RestartRequestBody) request.getBody();
        int id = body.getId();
        application.getUserManager().reset(id);
        UserInfo userInfo = application.getUserManager().getUserInfo(id);
        Integer opponentId = application.getGameManager().getOpponentId(id);
        application.getUserManager().reset(opponentId);

        UserInfo oppoentUserInfo = application.getUserManager().getUserInfo(opponentId);
        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.RESTART_SUCCESS.code(),
                new NullResponseBody()
        );

        RemotingCommand oppentRequest = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.OPPONENT_RESTART.code(),
                new NullRequestBody());
        application.getRemotingServer().invokeOneway(oppoentUserInfo.getChannel(), oppentRequest);
        return response;
    }
}
