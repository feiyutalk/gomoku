package edu.hitsz.commons.support;

import edu.hitsz.cluster.client.Client;
import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.cluster.client.state.GameState;

/**
 * Created by Neuclil on 17-4-15.
 */
public class GameBoot {
    public static void start(ClientApplication application){
        try {
            application.getBoard().getMessageTextField().setText("game begin......5");
            application.getBoard().getMessageTextField().repaint();
            Thread.sleep(1000);
            application.getBoard().getMessageTextField().setText("game begin......4");
            application.getBoard().getMessageTextField().repaint();
            Thread.sleep(1000);
            application.getBoard().getMessageTextField().setText("game begin......3");
            application.getBoard().getMessageTextField().repaint();
            Thread.sleep(1000);
            application.getBoard().getMessageTextField().setText("game begin......2");
            application.getBoard().getMessageTextField().repaint();
            Thread.sleep(1000);
            application.getBoard().getMessageTextField().setText("game begin......1");
            application.getBoard().getMessageTextField().repaint();
            Thread.sleep(1000);
            if(application.getBoard().isWhite()){
                application.getBoard().getMessageTextField().setText("your turn");
                application.getBoard().getMessageTextField().repaint();
                Client.gameState = GameState.TURNING;
            }else{
                application.getBoard().getMessageTextField().setText("your opponent's turn, please wait.");
                application.getBoard().getMessageTextField().repaint();
                Client.gameState = GameState.WAITING;
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
