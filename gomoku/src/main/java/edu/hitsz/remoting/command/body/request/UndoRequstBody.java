package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-20.
 */
public class UndoRequstBody extends AbstractRemotingCommandBody{
    private int id;
    private int lastY;
    private int lastX;

    public UndoRequstBody(int id, int lastY, int lastX) {
        this.id = id;
        this.lastY = lastY;
        this.lastX = lastX;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    @Override
    public void checkFields() throws Exception {

    }
}
