package edu.hitsz.remoting.command.body;


import edu.hitsz.cluster.NodeConfig;

public class CommandBodyWrapper {

    private NodeConfig config;

    public CommandBodyWrapper(NodeConfig config) {
        this.config = config;
    }

    public <T extends AbstractRemotingCommandBody> T wrapper(T commandBody) {
        commandBody.setNodeType(config.getNodeType().name());
        commandBody.setIdentity(config.getIdentity());
        return commandBody;
    }

    public static <T extends AbstractRemotingCommandBody> T wrapper(NodeConfig config, T commandBody) {
        commandBody.setNodeType(config.getNodeType().name());
        commandBody.setIdentity(config.getIdentity());
        return commandBody;
    }
}