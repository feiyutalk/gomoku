package edu.hitsz.driver;

import edu.hitsz.cluster.server.Server;

/**
 * Created by Neuclil on 17-4-16.
 */
public class ServerDriver {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
