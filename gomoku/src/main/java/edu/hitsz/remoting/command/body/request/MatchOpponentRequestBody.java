package edu.hitsz.remoting.command.body.request;

import edu.hitsz.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-15.
 */
public class MatchOpponentRequestBody extends AbstractRemotingCommandBody{
    private int oppoId;
    private int image;
    private String name;
    private String gender;
    private int age;
    private String from;
    private boolean white;

    @Override
    public void checkFields() throws Exception {

    }

    /************************* 	Getter & Setter	*************************/
    public int getOppoId() {
        return oppoId;
    }

    public void setOppoId(int oppoId) {
        this.oppoId = oppoId;
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

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }
}
