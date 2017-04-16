package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.LoseRequestBody;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-16.
 */
public class LoseRequestProcessor implements RemotingProcessor {
    private ClientApplication application;

    public LoseRequestProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LoseRequestBody body = (LoseRequestBody) request.getBody();
        boolean white = body.isWhite();
        int y = body.getY();
        int x = body.getX();
        application.getBoard().lose(white, y, x);
        return null;
    }
}
