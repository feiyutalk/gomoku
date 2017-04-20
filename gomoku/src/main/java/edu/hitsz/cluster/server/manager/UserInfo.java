package edu.hitsz.cluster.server.manager;

import edu.hitsz.cluster.server.Board;
import edu.hitsz.remoting.Channel;

/**
 * Created by Neuclil on 17-4-15.
 */
public class UserInfo {
    private int id;
    private Channel channel;
    private int image;
    private String name;
    private String gender;
    private int age;
    private String from;
    private boolean white;
    private UserState userState;
    private Board board;

    public boolean changeBoard(boolean white, int y, int x){
        return board.setPosition(y, x,
                white?Board.Color.WHITE:Board.Color.BLACK);
    }

    public boolean isWin(int y, int x){
        return board.isWin(y, x);
    }

    /************************* 	Getter & Setter	*************************/
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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
