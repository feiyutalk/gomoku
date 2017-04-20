package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-20.
 */
public class ChallengeRequestBody extends AbstractRemotingCommandBody{
    private int id;
    private int oppoId;

    public ChallengeRequestBody(int id, int oppoId) {
        this.id = id;
        this.oppoId = oppoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOppoId() {
        return oppoId;
    }

    public void setOppoId(int oppoId) {
        this.oppoId = oppoId;
    }

    @Override
    public void checkFields() throws Exception {

    }
}
