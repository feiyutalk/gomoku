package edu.hitsz.remoting.command.body.response;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-20.
 */
public class UndoResponseBody extends AbstractRemotingCommandBody{
    private int lastY;
    private int lastX;

    public UndoResponseBody(int lastY, int lastX) {
        this.lastY = lastY;
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    @Override
    public void checkFields() throws Exception {

    }
}
