package edu.hitsz.cluster.server;

import edu.hitsz.remoting.Channel;

import java.util.Map;

/**
 * Created by Neuclil on 17-4-9.
 */
public class UserManager {
    private Map<Channel, UserInfo> users;

    public void add(Channel channel, UserInfo userInfo){
        users.put(channel, userInfo);
    }
}
