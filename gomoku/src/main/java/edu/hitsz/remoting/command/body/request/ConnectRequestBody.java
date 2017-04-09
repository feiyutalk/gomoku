package edu.hitsz.remoting.command.body.request;

import edu.hitsz.cluster.server.UserInfo;
import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-9.
 */
public class ConnectRequestBody extends AbstractRemotingCommandBody {
    private UserInfo userInfo;

    public ConnectRequestBody(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void checkFields() throws Exception {

    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
