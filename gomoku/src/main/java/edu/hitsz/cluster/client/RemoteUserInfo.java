package edu.hitsz.cluster.client;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-15.
 */
public class RemoteUserInfo implements Serializable{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RemoteUserInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
