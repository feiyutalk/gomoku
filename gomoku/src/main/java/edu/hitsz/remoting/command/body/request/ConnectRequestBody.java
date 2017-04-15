package edu.hitsz.remoting.command.body.request;

import edu.hitsz.cluster.client.RemoteUserInfo;
import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-9.
 */
public class ConnectRequestBody extends AbstractRemotingCommandBody {
    private RemoteUserInfo remoteUserInfo;

    public ConnectRequestBody(RemoteUserInfo remoteUserInfo) {
        this.remoteUserInfo = remoteUserInfo;
    }

    @Override
    public void checkFields() throws Exception {

    }

    /************************* 	Getter & Setter	*************************/
    public RemoteUserInfo getRemoteUserInfo() {
        return remoteUserInfo;
    }

    public void setRemoteUserInfo(RemoteUserInfo remoteUserInfo) {
        this.remoteUserInfo = remoteUserInfo;
    }

    @Override
    public String toString() {
        return "ConnectRequestBody{" +
                "remoteUserInfo=" + remoteUserInfo +
                "} " + super.toString();
    }
}
