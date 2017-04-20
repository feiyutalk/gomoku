package edu.hitsz.cluster.server.manager;

import edu.hitsz.cluster.server.Board;
import edu.hitsz.remoting.Channel;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-15.
 */
public class UserInfo implements Serializable{
    private int id;
    private transient Channel channel;
    private int image;
    private String name;
    private String gender;
    private int age;
    private String from;
    private boolean white;
    private transient UserState userState;
    private transient Board board;
    private int lastY;
    private int lastX;

    public boolean changeBoard(boolean white, int y, int x){
        return board.setPosition(y, x,
                white?Board.Color.WHITE:Board.Color.BLACK);
    }

    public boolean isWin(int y, int x){
        return board.isWin(y, x);
    }

    public void reset(){
        board.reset();
    }

    public void undo(){
        board.undo(lastY, lastX);
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
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", channel=" + channel +
                ", image=" + image +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", from='" + from + '\'' +
                ", white=" + white +
                ", userState=" + userState +
                ", lastY=" + lastY +
                ", lastX=" + lastX +
                '}';
    }
}
