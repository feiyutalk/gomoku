package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.OpponentChangeBoardRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

/**
 * Created by Neuclil on 17-4-15.
 */
public class OpponentChangeBoardProcessor implements RemotingProcessor{
    private static final Logger LOG = Logger.getLogger(OpponentChangeBoardProcessor.class);
    private ClientApplication application;

    public OpponentChangeBoardProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOG.debug("OpponentChangeBoardProcessor正在处理请求....");
        OpponentChangeBoardRequestBody body = (OpponentChangeBoardRequestBody) request.getBody();
        boolean white = body.isWhite();
        int y = body.getY();
        int x = body.getX();
        System.out.println("OpponentChangeBoardProcessor_"+"y="+y+" x="+x);
        boolean success = application.getBoard().changeState(white, y, x);
        if(success){
            LOG.debug("OpponentChangeBoardProcessor处理完成!");
            return RemotingCommand.createResponseCommand(
                    RemotingProtos.ResponseCode.CHANGE_OPPONENT_BOARD_SUCCESS.code(),
                    new NullResponseBody());
        }else{
            LOG.debug("OpponentChangeBoardProcessor处理完成!");
            return RemotingCommand.createResponseCommand(
                    RemotingProtos.ResponseCode.CHANGE_OPPONENT_BOARD_FAILURE.code(),
                    new NullResponseBody());
        }

    }
}
