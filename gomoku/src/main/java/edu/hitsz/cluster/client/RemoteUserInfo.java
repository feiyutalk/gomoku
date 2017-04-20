package edu.hitsz.cluster.client;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-15.
 */
public class RemoteUserInfo implements Serializable{
    private int image;
    private String name;
    private String gender;
    private int age;
    private String from;

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

    @Override
    public String toString() {
        return "RemoteUserInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
