package edu.hitsz.remoting.processor;

import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.exception.RemotingCommandException;

public interface RemotingProcessor {
    RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException;
}
