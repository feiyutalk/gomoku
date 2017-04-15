package edu.hitsz.cluster.server.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Neuclil on 17-4-15.
 */
public class UserManager {
    private List<UserInfo> userList;
    private int connections;
    private static UserManager userManager = new UserManager();

    private UserManager(){
        userList = new LinkedList<>();
        connections = 256*2;
    }

    public static UserManager getInstance(){
        return userManager;
    }

    public synchronized List<UserInfo> getWaitUsers(){
        List<UserInfo> waitUsers = new ArrayList<>();
        for(UserInfo userInfo : userList){
            if(userInfo.getUserState().equals(UserState.WAITING)){
                waitUsers.add(userInfo);
            }
        }
        return waitUsers;
    }

    public synchronized boolean changeUserBoard(int id,boolean white, int y, int x){
        UserInfo userInfo = getUserInfo(id);
        if(userInfo == null){
            return false;
        }
        return userInfo.changeBoard(white,y, x);
    }

    public synchronized UserInfo getUserInfo(int id){
        for (UserInfo userInfo :userList) {
            if(userInfo.getId() == id)
                return userInfo;
        }
        return null;
    }

    public synchronized void addUser(UserInfo userInfo) {
        if(this.userList.size() < connections &&
                !this.userList.contains(userInfo)) {
            this.userList.add(userInfo);
        }
    }
}
