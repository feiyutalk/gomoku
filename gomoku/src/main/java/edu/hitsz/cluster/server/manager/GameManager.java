package edu.hitsz.cluster.server.manager;

import edu.hitsz.cluster.server.Board;
import edu.hitsz.cluster.server.ServerApplication;
import edu.hitsz.commons.constants.Constants;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.NullRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Neuclil on 17-4-15.
 */
public class GameManager {
    private static final Logger LOG = Logger.getLogger(GameManager.class);
    private ServerApplication application;
    private Map<Integer, Integer> matchTab = new HashMap<>();

    private static GameManager cm = null;

    private GameManager(ServerApplication application) {
        this.application = application;
    }

    public synchronized void matchGame(UserInfo user1, UserInfo user2) {
        Board board = new Board();
        user1.setBoard(board);
        user2.setBoard(board);
        matchTab.put(user1.getId(), user2.getId());
        matchTab.put(user2.getId(), user1.getId());
        user1.setUserState(UserState.GAMING);
        user2.setUserState(UserState.GAMING);
    }

    public Integer getOpponentId(int id){
        return matchTab.get(id);
    }

    public void removeMatch(int id){
        Integer opponentId = getOpponentId(id);
        matchTab.remove(id);
        matchTab.remove(opponentId);
    }

    public UserInfo getOpponentInfo(int id){
        return application.getUserManager().getUserInfo(matchTab.get(id));
    }

    public synchronized static GameManager getInstance(ServerApplication application) {
        if (cm == null) {
            cm = new GameManager(application);
        }
        return cm;
    }

}
