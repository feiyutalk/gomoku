package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ChangBoardRequestBody extends AbstractRemotingCommandBody{
    private int id;
    private boolean white;
    private int y;
    private int x;

    public ChangBoardRequestBody(int id, boolean white, int y, int x) {
        this.id = id;
        this.white = white;
        this.y = y;
        this.x = x;
    }

    @Override
    public void checkFields() throws Exception {

    }


    /************************* 	Getter & Setter	*************************/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
