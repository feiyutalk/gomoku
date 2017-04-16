package edu.hitsz.driver;

import edu.hitsz.cluster.client.Client;

/**
 * Created by Neuclil on 17-4-16.
 */
public class ClientDriver {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}