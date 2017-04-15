package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.commons.support.GameBoot;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.RemotingCommandBody;
import edu.hitsz.remoting.command.body.request.MatchOpponentRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

/**
 * Created by Neuclil on 17-4-15.
 */
public class MatchOpponentProcessor implements RemotingProcessor{
    private static final Logger LOG = Logger.getLogger(MatchOpponentProcessor.class);
    private ClientApplication application;

    public MatchOpponentProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOG.debug("MatchOpponentProcessor正在处理请求....");
        MatchOpponentRequestBody body = (MatchOpponentRequestBody)request.getBody();
        application.getBoard().setWhite(body.isWhite());
        application.getBoard().getSecondTextFiled().setText("match opponent success!");
        application.getBoard().getRightName().setText(body.getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                GameBoot.start(application);
            }
        }).start();
        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.MATCH_SUCCESS.code(),
                new NullResponseBody());
        LOG.debug("MatchOpponentProcessor处理请求完成!");
        return response;
    }
}
