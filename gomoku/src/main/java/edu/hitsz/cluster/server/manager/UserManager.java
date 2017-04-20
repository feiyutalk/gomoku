package edu.hitsz.cluster.server.manager;

import edu.hitsz.remoting.Channel;

import java.util.*;

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

    public synchronized List<UserInfo> getAll(){
        return Collections.unmodifiableList(userList);
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

    public synchronized UserInfo getRandomUserInfo(int id){
        if(userList.size()<=1){
            return null;
        }
        Random random = new Random();
        int num = random.nextInt(userList.size());
        while(num==id){
            num = random.nextInt(userList.size());
        }
        return userList.get(num);
    }

    public synchronized void reset(int id){
        for(UserInfo userInfo : userList){
            if(userInfo.getId() == id){
                userInfo.reset();
            }
        }
    }

    public synchronized void addUser(UserInfo userInfo) {
        if(this.userList.size() < connections &&
                !this.userList.contains(userInfo)) {
            this.userList.add(userInfo);
        }
    }

    public UserInfo remove(Channel channel) {
        for(int i=0; i<userList.size(); i++){
            UserInfo userInfo = userList.get(i);
            if(userInfo.getChannel().equals(channel)){
                userList.remove(i);
                return userInfo;
            }
        }
        return null;
    }
}
