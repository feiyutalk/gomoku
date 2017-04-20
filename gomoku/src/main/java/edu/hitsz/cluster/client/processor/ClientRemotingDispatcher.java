package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ClientRemotingDispatcher implements RemotingProcessor{
    private ClientApplication application;
    private final Map<RemotingProtos.RequestCode, RemotingProcessor> processors =
            new HashMap<>();

    public ClientRemotingDispatcher(ClientApplication application){
        this.application = application;
        processors.put(RemotingProtos.RequestCode.MATCH, new MatchOpponentProcessor(application));
        processors.put(RemotingProtos.RequestCode.OPPONENT_CHANGE_BOARD, new OpponentChangeBoardProcessor(application));
        processors.put(RemotingProtos.RequestCode.WIN, new WinRequestProcessor(application));
        processors.put(RemotingProtos.RequestCode.LOSE, new LoseRequestProcessor(application));
        processors.put(RemotingProtos.RequestCode.OPPONENT_RESTART, new OpponentRestartProcessor(application));
        processors.put(RemotingProtos.RequestCode.OPPONENT_UNDO, new OpponentUndoProcessor(application));
        processors.put(RemotingProtos.RequestCode.OPPONENT_EXIT, new OpponentExitProcessor(application));
        processors.put(RemotingProtos.RequestCode.PUSH_WAIT_USERINFO, new PushWaitUserInfoRequsetProcessor(application));
        processors.put(RemotingProtos.RequestCode.OPPONENT_START, new OpponentStartProcessor(application));
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        RemotingProtos.RequestCode code =
                RemotingProtos.RequestCode.valueOf(request.getCode());
        RemotingProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.REQUEST_CODE_NOT_SUPPORTED.code(), "request code not supported!");
        }
        return processor.processRequest(channel, request);
    }
}
