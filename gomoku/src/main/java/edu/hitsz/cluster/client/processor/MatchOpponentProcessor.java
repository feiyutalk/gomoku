package edu.hitsz.cluster.client.processor;

import edu.hitsz.cluster.client.Client;
import edu.hitsz.cluster.client.ClientApplication;
import edu.hitsz.cluster.client.state.GameState;
import edu.hitsz.commons.support.GameBoot;
import edu.hitsz.remoting.Channel;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.MatchOpponentRequestBody;
import edu.hitsz.remoting.command.body.response.NullResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.exception.RemotingCommandException;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Neuclil on 17-4-15.
 */
public class MatchOpponentProcessor implements RemotingProcessor{
    private static final Logger LOG = Logger.getLogger(MatchOpponentProcessor.class);
    private ClientApplication application;

    public MatchOpponentProcessor(ClientApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOG.debug("MatchOpponentProcessor正在处理请求....");
        MatchOpponentRequestBody body = (MatchOpponentRequestBody)request.getBody();
        int oppoId = body.getOppoId();
        int imageSequnce = body.getImage();
        String name = body.getName();
        String gender = body.getGender();
        int age = body.getAge();
        String from = body.getFrom();
        boolean white = body.isWhite();
        System.out.println(File.separator + gender +
                File.separator + imageSequnce + ".png");
        try {
            Image image = ImageIO.read(getClass().getResource(
                      "/" + gender +
                            "/" + imageSequnce + ".png"));
            application.getBoard().getSecondPlayer().setIcon(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Client.opponId = oppoId;
        application.getBoard().getSecondNameText().setText(name);
        application.getBoard().getSecondGenderText().setText(gender);
        application.getBoard().getSecondAgeText().setText(age+"");
        application.getBoard().getSecondFromText().setText(from);
        application.getBoard().setWhite(white);
        application.getBoard().getMessageTextField().setText("match opponent success!");
        Client.gameState = GameState.READY;

        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.MATCH_SUCCESS.code(),
                new NullResponseBody());
        LOG.debug("MatchOpponentProcessor处理请求完成!");
        return response;
    }
}
