package edu.hitsz.cluster.client;

import edu.hitsz.cluster.client.processor.ClientRemotingDispatcher;
import edu.hitsz.cluster.client.state.GameState;
import edu.hitsz.commons.constants.Constants;
import edu.hitsz.commons.utils.Parser;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.ChangBoardRequestBody;
import edu.hitsz.remoting.command.body.request.ConnectRequestBody;
import edu.hitsz.remoting.command.body.response.ConnectResponseBody;
import edu.hitsz.remoting.command.protocol.RemotingProtos;
import edu.hitsz.remoting.delegate.RemotingClientDelegate;
import edu.hitsz.remoting.processor.RemotingProcessor;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Neuclil on 17-4-9.
 */
public class Client {
    private static final Logger LOG = Logger.getLogger(Client.class);
    private ClientConfig config;
    private Board board;
    private RemotingClientDelegate remotingClient;
    private ClientApplication application;
    public static GameState gameState;
    private BoardState[][] boardState;
    private AtomicBoolean gameStart = new AtomicBoolean(false);
    private AtomicBoolean inited = new AtomicBoolean(false);
    private AtomicBoolean remotingStarted = new AtomicBoolean(false);

    public Client() {
        this.application = new ClientApplication();
    }


    public void start() {
        init();
        startRemoting();
    }

    private void startRemoting() {
        try {
            if (remotingStarted.compareAndSet(false, true)) {
                LOG.debug("正在开启通信服务....");
                remotingClient.start();
                RemotingProcessor defaultProcessor = getDefaultProcessor();
                if (defaultProcessor != null) {
                    remotingClient.registerDefaultProcessor(defaultProcessor,
                            Executors.newFixedThreadPool(Constants.DEFAULT_PROCESSOR_THREAD));
                }
                LOG.debug("通信服务开启成功!");
            }
        } catch (Exception e) {
            LOG.error("Client通信服务开启失败!", e);
        }
    }

    private RemotingProcessor getDefaultProcessor() {
        return new ClientRemotingDispatcher(application);
    }

    private void init() {
        try {
            if (inited.compareAndSet(false, true)) {
                LOG.debug("Client开始初始化....");
                initConfig();

                initRemoting();

                initBoard();

                initApplication();
            }
        } catch (Exception e) {
            LOG.error("Client初始化失败!");
        }
    }

    private void initApplication() {
        this.application.setClientConfig(config);
        this.application.setRemotingClient(remotingClient);
        this.application.setBoard(board);
        LOG.debug("初始化application完成!");
    }

    private void initBoard() {
        boardState = new BoardState[Constants.DIMENSION][Constants.DIMENSION];
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                boardState[i][j] = BoardState.NONE;
            }
        }
        this.board = new Board();
        board.init();
        gameState = GameState.READY;
        LOG.debug("棋盘初始化成功!");
    }

    private void initRemoting() {
        this.remotingClient = new RemotingClientDelegate();
        LOG.debug("初始化通信服务完成!");
    }

    private void initConfig() {
        Map<String, String> info =
                Parser.parseClientConfig("clientconfig.xml");
        ClientConfig.Builder builder = new ClientConfig.Builder();
        config = builder
                .name(info.get("name"))
                .gender(info.get("gender"))
                .age(Integer.valueOf(info.get("age")))
                .from(info.get("from"))
                .image(Integer.valueOf(info.get("image")))
                .ip(info.get("ip"))
                .port(Integer.valueOf(info.get("port")))
                .serverIp(info.get("serverIp"))
                .serverPort(Integer.valueOf(info.get("serverPort")))
                .build();
        LOG.debug("Client初始化配置信息成功! " + config);
    }

    enum BoardState {
        WHITE,
        BLACK,
        NONE;
    }

    public class Board extends JFrame {
        private boolean white;
        /* 玩家1 */
        private JLabel firstPlayer = null;
        private JLabel firstName = null;
        private JLabel firstNameText = null;
        private JLabel firstGender = null;
        private JLabel firstGenderText = null;
        private JLabel firstAge = null;
        private JLabel firstAgeText = null;
        private JLabel firstFrom = null;
        private JLabel firstFromText = null;


        /* 棋局 */
        private TextField messageTextField = null;
        private JButton startButton = null;
        private JButton undoButton = null;
        private JButton restartButton = null;
        private JButton exitButton = null;
        private JButton[][] buttons = null;

        /* 玩家2 */
        private JLabel secondPlayer = null;
        private JLabel secondName = null;
        private JLabel secondNameText = null;
        private JLabel secondGender = null;
        private JLabel secondGenderText = null;
        private JLabel secondAge = null;
        private JLabel secondAgeText = null;
        private JLabel secondFrom = null;
        private JLabel secondFromText = null;

        public Board() throws HeadlessException {
            //玩家1
            firstPlayer = new JLabel();
            firstName = new JLabel();
            firstNameText = new JLabel();
            firstGender = new JLabel();
            firstGenderText = new JLabel();
            firstAge = new JLabel();
            firstAgeText = new JLabel();
            firstFrom = new JLabel();
            firstFromText = new JLabel();

            //棋局
            messageTextField = new TextField();
            buttons = new JButton[Constants.DIMENSION][Constants.DIMENSION];
            startButton = new JButton();
            undoButton = new JButton();
            restartButton = new JButton();
            exitButton = new JButton();

            //玩家2
            secondPlayer = new JLabel();
            secondName = new JLabel();
            secondNameText = new JLabel();
            secondGender = new JLabel();
            secondGenderText = new JLabel();
            secondAge = new JLabel();
            secondAgeText = new JLabel();
            secondFrom = new JLabel();
            secondFromText = new JLabel();
        }

        public void init() {
            this.setLayout(null);
            this.setSize(2*Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH,
                    Constants.FIRST_PANEL_HEIGHT);
            /************************* 	玩家1	*************************/
            firstPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));

            firstPlayer.setBounds(25,5,
                    Constants.FIRST_PLAYER_LOGO_WIDTH,Constants.FIRST_PLAYER_LOGO_HEIGHT);
            firstPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            try {
                Image image = ImageIO.read(getClass().getResource(
                        "/" + config.getGender() +
                              "/" + config.getImage()+".png"));
                firstPlayer.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                e.printStackTrace();
            }

            firstName.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstName.setBounds(25, 5+Constants.FIRST_PLAYER_LOGO_HEIGHT+10,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstName.setText("Name:");

            firstNameText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstNameText.setBounds(25+Constants.FIRST_NAME_LABEL_WIDTH, 5+Constants.FIRST_PLAYER_LOGO_HEIGHT+10,
                    Constants.FIRST_NAME_TEXT_WIDTH, Constants.FIRST_NAME_TEXT_HEIGHT);
            firstNameText.setText(config.getName());

            firstGender.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstGender.setBounds(25, 5+Constants.FIRST_PLAYER_LOGO_HEIGHT+Constants.FIRST_NAME_LABEL_HEIGHT+15,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstGender.setText("Gender:");

            firstGenderText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstGenderText.setBounds(25+Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+Constants.FIRST_NAME_LABEL_HEIGHT+15,
                    Constants.FIRST_NAME_TEXT_WIDTH, Constants.FIRST_NAME_TEXT_HEIGHT);
            firstGenderText.setText(config.getGender());

            firstAge.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstAge.setBounds(25, 5+Constants.FIRST_PLAYER_LOGO_HEIGHT+2*Constants.FIRST_NAME_LABEL_HEIGHT+20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstAge.setText("Age :");

            firstAgeText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstAgeText.setBounds(25+Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+2*Constants.FIRST_NAME_LABEL_HEIGHT+20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstAgeText.setText(config.getAge()+"");

            firstFrom.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstFrom.setBounds(25, 5+Constants.FIRST_PLAYER_LOGO_HEIGHT+3*Constants.FIRST_NAME_LABEL_HEIGHT+25,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstFrom.setText("From:");

            firstFromText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstFromText.setBounds(25+Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+3*Constants.FIRST_NAME_LABEL_HEIGHT+25,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstFromText.setText(config.getFrom());

            this.add(firstPlayer);
            this.add(firstName);
            this.add(firstNameText);
            this.add(firstGender);
            this.add(firstGenderText);
            this.add(firstAge);
            this.add(firstAgeText);
            this.add(firstFrom);
            this.add(firstFromText);

            /************************* 	棋局  *************************/

            messageTextField.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE-3));
            messageTextField.setBounds(Constants.FIRST_PANEL_WIDTH,5,Constants.MESSAGE_TEXT_WIDTH,
                    Constants.MESSAGE_TEXT_HEIGHT);
            messageTextField.setEditable(false);
            messageTextField.setText("Hello, "+config.getName() +". Welcome to GoMoKu Game!");

            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    buttons[i][j] = new JButton();
                    JButton button = buttons[i][j];
                    button.setBounds(Constants.BUTTONS_WIDTH_OFF + j * Constants.BUTTON_LENGTH,
                            Constants.BUTTONS_HEIGHT_OFF + i * Constants.BUTTON_LENGTH,
                            Constants.BUTTON_LENGTH,
                            Constants.BUTTON_LENGTH);
                    button.setBorder(new LineBorder(new Color(0, 0, 0)));
                    try {
                        Image image = ImageIO.read(getClass().getResource(
                                "/background.gif"));
                        button.setIcon(new ImageIcon(image));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            startButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            startButton.setBounds(Constants.FIRST_PANEL_WIDTH+25,
                    50 + Constants.BOARD_PANEL_WIDTH+10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            startButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            startButton.setText("start");

            undoButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            undoButton.setBounds(Constants.FIRST_PANEL_WIDTH+175,
                    50 + Constants.BOARD_PANEL_WIDTH+10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            undoButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            undoButton.setText("undo");

            restartButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            restartButton.setBounds(Constants.FIRST_PANEL_WIDTH + 325,
                    50 + Constants.BOARD_PANEL_WIDTH+10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            restartButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            restartButton.setText("restart");

            exitButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            exitButton.setBounds(Constants.FIRST_PANEL_WIDTH+475,
                    50 + Constants.BOARD_PANEL_WIDTH + 10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            exitButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            exitButton.setText("exit");

            this.add(messageTextField);
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    this.add(buttons[i][j]);
                }
            }
            this.add(startButton);
            this.add(undoButton);
            this.add(restartButton);
            this.add(exitButton);
            /************************* 	玩家2  *************************/
            secondPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            secondPlayer.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25,5,
                    Constants.FIRST_PLAYER_LOGO_WIDTH,Constants.FIRST_PLAYER_LOGO_HEIGHT);
            secondPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            try {
                Image image = ImageIO.read(getClass().getResource("/not.jpg"));
                secondPlayer.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                e.printStackTrace();
            }


            secondName.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondName.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+10,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondName.setText("Name:");

            secondNameText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondNameText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+10,
                    Constants.FIRST_NAME_TEXT_WIDTH,
                    Constants.FIRST_NAME_TEXT_HEIGHT);

            secondGender.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondGender.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+Constants.FIRST_NAME_LABEL_HEIGHT+15,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondGender.setText("Gender:");

            secondGenderText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondGenderText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+Constants.FIRST_NAME_LABEL_HEIGHT+15,
                    Constants.FIRST_NAME_TEXT_WIDTH,
                    Constants.FIRST_NAME_TEXT_HEIGHT);

            secondAge.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondAge.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+2*Constants.FIRST_NAME_LABEL_HEIGHT+20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondAge.setText("Age :");

            secondAgeText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondAgeText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+2*Constants.FIRST_NAME_LABEL_HEIGHT+20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);

            secondFrom.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondFrom.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+3*Constants.FIRST_NAME_LABEL_HEIGHT+25,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondFrom.setText("From:");

            secondFromText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondFromText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH+25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5+Constants.FIRST_PLAYER_LOGO_HEIGHT+3*Constants.FIRST_NAME_LABEL_HEIGHT+25,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);

            this.add(secondPlayer);
            this.add(secondName);
            this.add(secondNameText);
            this.add(secondGender);
            this.add(secondGenderText);
            this.add(secondAge);
            this.add(secondAgeText);
            this.add(secondFrom);
            this.add(secondFromText);

            /************************* 	Frame	*************************/
            this.setTitle("HITSZ-GoMoKu");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);

            /************************* Action	*************************/
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch(gameState){
                        case READY:
                            LOG.debug("start game, finding opponents...");
                            if(gameStart.compareAndSet(false, true)){
                                messageTextField.setText("connecting server...please wait.");
                                gameState = GameState.CONNECTING;
                                RemoteUserInfo remoteUserInfo = new RemoteUserInfo();
                                remoteUserInfo.setImage(config.getImage());
                                remoteUserInfo.setName(config.getName());
                                remoteUserInfo.setGender(config.getGender());
                                remoteUserInfo.setAge(config.getAge());
                                remoteUserInfo.setFrom(config.getFrom());
                                RemotingCommand request = RemotingCommand.createRequestCommand(
                                        RemotingProtos.RequestCode.CONNECT.code(),
                                        new ConnectRequestBody(remoteUserInfo)
                                );
                                RemotingCommand response = application.getRemotingClient().invokeSync(
                                        config.getServerIp() + ":" + config.getServerPort(),
                                        request
                                );
                                config.setId(((ConnectResponseBody)response.getBody()).getId());
                                if(response.getCode() == RemotingProtos.ResponseCode.CONNECT_SUCCESS.code()){
                                    LOG.debug("Connect Success!");
                                    messageTextField.setText("connect success! waiting for match.");
                                    gameState = GameState.MATCHING;
                                }
                            }
                    }
                }
            });

            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    JButton button = buttons[i][j];
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int y =(button.getY()-Constants.BUTTONS_HEIGHT_OFF)/button.getHeight();
                            int x =(button.getX()-Constants.BUTTONS_WIDTH_OFF)/button.getWidth();
                            switch (gameState) {
                                case TURNING:
                                    if (boardState[y][x] == BoardState.NONE) {
                                        try {
                                            String imageFile = (isWhite() == true)
                                                    ? "/whiteStone.gif" : "/blackStone.gif";
                                            Image image = ImageIO.read(getClass().getResource(
                                                    imageFile));
                                            button.setIcon(new ImageIcon(image));
                                            button.repaint();

                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                        RemotingCommand request = RemotingCommand.createRequestCommand(
                                                RemotingProtos.RequestCode.CHANGE_BOARD.code(),
                                                new ChangBoardRequestBody(config.getId(), isWhite(), y, x));
                                        RemotingCommand response = application.getRemotingClient().invokeSync(
                                                config.getServerIp() + ":" + config.getServerPort(),
                                                request);
                                        if (response.getCode() == RemotingProtos.ResponseCode.CHANGE_BOARD_SUCCESS.code()) {
                                            gameState = GameState.WAITING;
                                            boardState[y][x] = (isWhite() == true) ? BoardState.WHITE : BoardState.BLACK;
                                            messageTextField.setText("your opponent's turn, please wait.");
                                            LOG.debug("Server端棋局状态改变成功!");
                                        } else {
                                            //TODO
                                            LOG.debug("Server端棋局状态改变失败!");
                                        }
                                    }
                                    break;
                            }
                        }
                    });
                }
            }
        }

        public boolean changeState(boolean white, int y, int x) {
            boolean success = false;
            if (gameState == GameState.WAITING &&
                    boardState[y][x] == BoardState.NONE) {
                Image image = null;
                try {
                    JButton button = buttons[y][x];
                    String imageFile = (white == true) ?
                            "/whiteStone.gif" : "/blackStone.gif";
                    image = ImageIO.read(getClass().getResource(
                            imageFile));
                    button.setIcon(new ImageIcon(image));
                    button.repaint();
                    success = true;
                    gameState = GameState.TURNING;
                    boardState[y][x] = white ? BoardState.WHITE : BoardState.BLACK;
                    messageTextField.setText("your turn");
                } catch (IOException e) {
                    LOG.debug("对方下棋后,改变棋局状态失败!", e);
                    return success;
                }
            }
            return success;
        }

        public void win() {
            JOptionPane.showMessageDialog(null, "You Win!:)");
            gameState = GameState.END;
            messageTextField.setText("Game Over! You Win!:)");
        }

        public void lose(boolean white, int y, int x) {
            changeState(white, y, x);
            JOptionPane.showMessageDialog(null, "You Lose!:(");
            gameState = GameState.END;
            messageTextField.setText("Game Over! You Lose!:(");
        }

        /************************* 	Getter & Setter	*************************/
        public boolean isWhite() {
            return white;
        }

        public void setWhite(boolean white) {
            this.white = white;
        }

        public TextField getMessageTextField() {
            return messageTextField;
        }

        public JButton[][] getButtons() {
            return buttons;
        }

        public JLabel getFirstPlayer() {
            return firstPlayer;
        }

        public void setFirstPlayer(JLabel firstPlayer) {
            this.firstPlayer = firstPlayer;
        }

        public JLabel getFirstName() {
            return firstName;
        }

        public void setFirstName(JLabel firstName) {
            this.firstName = firstName;
        }

        public JLabel getFirstNameText() {
            return firstNameText;
        }

        public void setFirstNameText(JLabel firstNameText) {
            this.firstNameText = firstNameText;
        }

        public JLabel getFirstGender() {
            return firstGender;
        }

        public void setFirstGender(JLabel firstGender) {
            this.firstGender = firstGender;
        }

        public JLabel getFirstGenderText() {
            return firstGenderText;
        }

        public void setFirstGenderText(JLabel firstGenderText) {
            this.firstGenderText = firstGenderText;
        }

        public JLabel getFirstAge() {
            return firstAge;
        }

        public void setFirstAge(JLabel firstAge) {
            this.firstAge = firstAge;
        }

        public JLabel getFirstAgeText() {
            return firstAgeText;
        }

        public void setFirstAgeText(JLabel firstAgeText) {
            this.firstAgeText = firstAgeText;
        }

        public JLabel getFirstFrom() {
            return firstFrom;
        }

        public void setFirstFrom(JLabel firstFrom) {
            this.firstFrom = firstFrom;
        }

        public JLabel getFirstFromText() {
            return firstFromText;
        }

        public void setFirstFromText(JLabel firstFromText) {
            this.firstFromText = firstFromText;
        }

        public void setMessageTextField(TextField messageTextField) {
            this.messageTextField = messageTextField;
        }

        public JButton getStartButton() {
            return startButton;
        }

        public void setStartButton(JButton startButton) {
            this.startButton = startButton;
        }

        public JButton getUndoButton() {
            return undoButton;
        }

        public void setUndoButton(JButton undoButton) {
            this.undoButton = undoButton;
        }

        public JButton getRestartButton() {
            return restartButton;
        }

        public void setRestartButton(JButton restartButton) {
            this.restartButton = restartButton;
        }

        public JButton getExitButton() {
            return exitButton;
        }

        public void setExitButton(JButton exitButton) {
            this.exitButton = exitButton;
        }

        public void setButtons(JButton[][] buttons) {
            this.buttons = buttons;
        }

        public JLabel getSecondPlayer() {
            return secondPlayer;
        }

        public void setSecondPlayer(JLabel secondPlayer) {
            this.secondPlayer = secondPlayer;
        }

        public JLabel getSecondName() {
            return secondName;
        }

        public void setSecondName(JLabel secondName) {
            this.secondName = secondName;
        }

        public JLabel getSecondNameText() {
            return secondNameText;
        }

        public void setSecondNameText(JLabel secondNameText) {
            this.secondNameText = secondNameText;
        }

        public JLabel getSecondGender() {
            return secondGender;
        }

        public void setSecondGender(JLabel secondGender) {
            this.secondGender = secondGender;
        }

        public JLabel getSecondGenderText() {
            return secondGenderText;
        }

        public void setSecondGenderText(JLabel secondGenderText) {
            this.secondGenderText = secondGenderText;
        }

        public JLabel getSecondAge() {
            return secondAge;
        }

        public void setSecondAge(JLabel secondAge) {
            this.secondAge = secondAge;
        }

        public JLabel getSecondAgeText() {
            return secondAgeText;
        }

        public void setSecondAgeText(JLabel secondAgeText) {
            this.secondAgeText = secondAgeText;
        }

        public JLabel getSecondFrom() {
            return secondFrom;
        }

        public void setSecondFrom(JLabel secondFrom) {
            this.secondFrom = secondFrom;
        }

        public JLabel getSecondFromText() {
            return secondFromText;
        }

        public void setSecondFromText(JLabel secondFromText) {
            this.secondFromText = secondFromText;
        }
    }
}