package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-15.
 */
public class MatchOpponentRequestBody extends AbstractRemotingCommandBody{
    private String name;
    private boolean white;

    public MatchOpponentRequestBody(String name, boolean white) {
        this.name = name;
        this.white = white;
    }

    @Override
    public void checkFields() throws Exception {

    }

    /************************* 	Getter & Setter	*************************/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }
}
