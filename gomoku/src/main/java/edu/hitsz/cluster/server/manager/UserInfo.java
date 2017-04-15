package edu.hitsz.cluster.server.manager;

import edu.hitsz.cluster.server.Board;
import edu.hitsz.remoting.Channel;

/**
 * Created by Neuclil on 17-4-15.
 */
public class UserInfo {
    private int id;
    private Channel channel;
    private String name;
    private boolean white;
    private UserState userState;
    private Board board;

    public UserInfo(String name, int uid) {
        this.name = name;
        this.id = uid;
    }

    public boolean changeBoard(boolean white, int y, int x){
        return board.setPosition(y, x,
                white?Board.Color.WHITE:Board.Color.BLACK);
    }

    public boolean isWin(int x, int y){
        return board.isWin(x, y);
    }

    /************************* 	Getter & Setter	*************************/
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "channel=" + channel +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", userState=" + userState +
                ", board=\n" + board +
                '}';
    }
}
