package edu.hitsz.cluster.client;

import edu.hitsz.remoting.delegate.RemotingClientDelegate;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ClientApplication {
    private ClientConfig clientConfig;
    private RemotingClientDelegate remotingClient;
    private Client.Board board;

    /************************* 	Getter & Setter	*************************/

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RemotingClientDelegate getRemotingClient() {
        return remotingClient;
    }

    public void setRemotingClient(RemotingClientDelegate remotingClient) {
        this.remotingClient = remotingClient;
    }

    public Client.Board getBoard() {
        return board;
    }

    public void setBoard(Client.Board board) {
        this.board = board;
    }

}
