package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-15.
 */
public class OpponentChangeBoardRequestBody extends AbstractRemotingCommandBody{
    private boolean white;
    private int y;
    private int x;

    public OpponentChangeBoardRequestBody(boolean white, int y, int x) {
        this.white = white;
        this.y = y;
        this.x = x;
    }

    @Override
    public void checkFields() throws Exception {

    }

    /************************* 	Getter & Setter	*************************/

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
