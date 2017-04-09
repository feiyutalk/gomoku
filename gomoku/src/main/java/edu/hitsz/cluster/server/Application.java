package edu.hitsz.cluster.server;

/**
 * Created by Neuclil on 17-4-9.
 */
public class Application {
    private UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
