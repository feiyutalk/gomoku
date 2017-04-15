package edu.hitsz.cluster.server;

import edu.hitsz.cluster.server.manager.GameManager;
import edu.hitsz.cluster.server.manager.UserManager;
import edu.hitsz.remoting.delegate.RemotingServerDelegate;

/**
 * Created by Neuclil on 17-4-9.
 */
public class ServerApplication {
    private ServerConfig serverConfig;
    private RemotingServerDelegate remotingServer;
    private GameManager gameManager;
    private UserManager userManager;

    /************************* 	Getter & Setter	*************************/
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public RemotingServerDelegate getRemotingServer() {
        return remotingServer;
    }

    public void setRemotingServer(RemotingServerDelegate remotingServer) {
        this.remotingServer = remotingServer;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
