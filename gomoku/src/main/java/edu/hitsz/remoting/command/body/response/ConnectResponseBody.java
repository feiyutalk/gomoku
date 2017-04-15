package edu.hitsz.remoting.command.body.response;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ConnectResponseBody extends AbstractRemotingCommandBody{
    private int id;

    public ConnectResponseBody(int id) {
        this.id = id;
    }

    @Override
    public void checkFields() throws Exception {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
