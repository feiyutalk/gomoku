package edu.hitsz.cluster.server.processor;

import edu.hitsz.cluster.server.ServerApplication;
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
public class ServerRemotingDispacher implements RemotingProcessor{
    private ServerApplication application;
    private final Map<RemotingProtos.RequestCode, RemotingProcessor> processors =
            new HashMap<>();

    public ServerRemotingDispacher(ServerApplication application){
        this.application = application;
        processors.put(RemotingProtos.RequestCode.CONNECT, new ConnectProcessor(application));
        processors.put(RemotingProtos.RequestCode.CHANGE_BOARD, new ChangeBoardProcessor(application));
        processors.put(RemotingProtos.RequestCode.RESTART, new RestartRequestProcessor(application));
        processors.put(RemotingProtos.RequestCode.UNDO, new UndoRequestProcessor(application));
        processors.put(RemotingProtos.RequestCode.CHALLENGE, new ChallengeRequestProcessor(application));
        processors.put(RemotingProtos.RequestCode.START, new StartRequestProcessor(application));
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
