package edu.hitsz.cluster.server;

import edu.hitsz.cluster.server.processor.ConnectProcessor;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.delegate.RemotingServerDelegate;

import java.util.concurrent.Executors;

/**
 * Created by Neuclil on 17-4-9.
 */
public class Server {
    private RemotingServerDelegate remotingServerDelegate;
    private Application application;

    public Server(Application application){
        this.application = application;
    }

    public Server(RemotingServerDelegate remotingServerDelegate){
        this.remotingServerDelegate = remotingServerDelegate;
    }

    public void start(){
        remotingServerDelegate.registerServerProcessor(RemotingProtos.RequestCode.CONNECT.code(),
                new ConnectProcessor(application), Executors.newCachedThreadPool());
    }
}
