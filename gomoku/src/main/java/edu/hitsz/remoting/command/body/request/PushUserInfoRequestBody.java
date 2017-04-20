package edu.hitsz.remoting.command.body.request;

import edu.hitsz.cluster.server.manager.UserInfo;
import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

import java.util.List;

/**
 * Created by Neuclil on 17-4-20.
 */
public class PushUserInfoRequestBody extends AbstractRemotingCommandBody{
    private List<UserInfo> waitUsers;

    public PushUserInfoRequestBody(List<UserInfo> waitUsers) {
        this.waitUsers = waitUsers;
    }

    public List<UserInfo> getWaitUsers() {
        return waitUsers;
    }

    public void setWaitUsers(List<UserInfo> waitUsers) {
        this.waitUsers = waitUsers;
    }

    @Override
    public void checkFields() throws Exception {

    }
}
