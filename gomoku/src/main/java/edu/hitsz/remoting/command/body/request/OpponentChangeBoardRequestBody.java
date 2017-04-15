package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-15.
 */
public class OpponentChangeBoardRequestBody extends AbstractRemotingCommandBody{
    private boolean white;
    private int x;
    private int y;

    public OpponentChangeBoardRequestBody(boolean white, int x, int y) {
        this.white = white;
        this.x = x;
        this.y = y;
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
