package edu.hitsz.cluster.client;

import edu.hitsz.cluster.client.processor.ClientRemotingDispatcher;
import edu.hitsz.cluster.client.state.GameState;
import edu.hitsz.cluster.server.Board;
import edu.hitsz.cluster.server.manager.UserInfo;
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
        config = builder.ip(info.get("ip"))
                .port(Integer.valueOf(info.get("port")))
                .name(info.get("name"))
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
        private JLabel leftPlayer = null;
        private TextField leftName = null;
        private JLabel rightPlayer = null;
        private TextField rightName = null;
        private JButton secondButton = null;
        private TextField secondTextFiled = null;
        private JButton[][] buttons = null;

        public Board() throws HeadlessException {
            this.leftPlayer = new JLabel();
            this.leftName = new TextField();
            this.rightPlayer = new JLabel();
            this.rightName = new TextField();
            this.secondButton = new JButton();
            this.secondTextFiled = new TextField();
            this.buttons = new JButton[Constants.DIMENSION][Constants.DIMENSION];
        }

        public void init() {
            this.setLayout(null);
            this.setSize(Constants.BOARD_WIDTH, Constants.BOARD_HIGHT
                    + 2 * Constants.JLABLE_PLAYER_HEIGHT);
            /************************* 	Panel	*************************/
            /************************* 	First Line	*************************/
            leftPlayer.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            leftPlayer.setBounds(0, 0, Constants.JLABLE_PLAYER_WIDTH, Constants.JLABLE_PLAYER_HEIGHT);
            leftPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            leftPlayer.setText("Player1");

            leftName.setFont(new Font("Times", Font.PLAIN, Constants.FONT_SIZE));
            leftName.setBounds(Constants.JLABLE_PLAYER_WIDTH, 0, Constants.TEXTFIELD_NAME_WIDTH, Constants.TEXTFIELD_NAME_HEIGHT);
            leftName.setText(config.getName());
            leftName.setEditable(false);

            rightPlayer.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            rightPlayer.setBounds(Constants.JLABLE_PLAYER_WIDTH + Constants.TEXTFIELD_NAME_WIDTH,
                    0, Constants.JLABLE_PLAYER_WIDTH, Constants.JLABLE_PLAYER_HEIGHT);
            rightPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            rightPlayer.setText("Player2");

            rightName.setFont(new Font("Times", Font.PLAIN, Constants.FONT_SIZE));
            rightName.setBounds(2 * Constants.JLABLE_PLAYER_WIDTH + Constants.TEXTFIELD_NAME_WIDTH,
                    0, Constants.TEXTFIELD_NAME_WIDTH, Constants.TEXTFIELD_NAME_HEIGHT);
            rightName.setText("");
            rightName.setEditable(false);


            /************************* 	Second Line	 *************************/
            secondButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE - 5));
            secondButton.setBounds(0, Constants.JLABLE_PLAYER_HEIGHT, Constants.JLABLE_PLAYER_WIDTH,
                    Constants.JLABLE_PLAYER_HEIGHT);
            secondButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            secondButton.setText("start game");

            secondTextFiled.setFont(new Font("Times", Font.ITALIC, Constants.FONT_SIZE));
            secondTextFiled.setBounds(Constants.JLABLE_PLAYER_WIDTH, Constants.JLABLE_PLAYER_HEIGHT,
                    Constants.TEXTAREA_WIDTH, Constants.TEXTAREA_HEIGHT);
            secondTextFiled.setEditable(false);
            secondTextFiled.setText("  please start the game.");

            /************************* 	Buttons	*************************/
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    buttons[i][j] = new JButton();
                    JButton button = buttons[i][j];
                    button.setBounds(j * Constants.BUTTON_LENGTH,
                            Constants.BUTTON_HEIGHT_OFF + i * Constants.BUTTON_LENGTH,
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

            /************************* 	Frame	*************************/
            this.add(leftPlayer);
            this.add(leftName);
            this.add(rightPlayer);
            this.add(rightName);
            this.add(secondButton);
            this.add(secondTextFiled);
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    this.add(buttons[i][j]);
                }
            }
            this.setTitle("HITSZ-GoMoKu");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);

            /************************* Action	*************************/
            secondButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch (gameState) {
                        case READY:
                            LOG.debug("开始游戏,正在寻找对手....");
                            if (gameStart.compareAndSet(false, true)) {
                                secondButton.setText("exit");
                                secondTextFiled.setText("connecting server...please wait.");
                                gameState = GameState.CONNECTING;
                                RemoteUserInfo remoteUserInfo = new RemoteUserInfo();
                                remoteUserInfo.setName(config.getName());
                                RemotingCommand request = RemotingCommand.createRequestCommand(
                                        RemotingProtos.RequestCode.CONNECT.code(),
                                        new ConnectRequestBody(remoteUserInfo));
                                RemotingCommand response = application.getRemotingClient().invokeSync(
                                        config.getServerIp() + ":" + config.getServerPort(),
                                        request);
                                config.setId(((ConnectResponseBody) response.getBody()).getId());
                                if (response.getCode() == RemotingProtos.ResponseCode.CONNECT_SUCCESS.code()) {
                                    LOG.debug("连接成功!");
                                    secondTextFiled.setText("connect success! waiting for match");
                                    gameState = GameState.MATCHING;
                                }
                            }
                            break;
                    }
                }
            });

            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    //TODO 为什么x y的值和想象中的是反的
                    JButton button = buttons[i][j];
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int y =(button.getY()-Constants.Y_OFF)/button.getHeight();
                            int x =(button.getX()-Constants.X_OFF)/button.getWidth();
                            System.out.println("y="+y+" x="+x);
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
                                            secondTextFiled.setText("your opponent's turn, please wait.");
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
                    secondTextFiled.setText("your turn");
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
            secondButton.setText("start game");
            secondTextFiled.setText("Game Over! You Win!:)");
        }

        public void lose(boolean white, int y, int x) {
            changeState(white, y, x);
            JOptionPane.showMessageDialog(null, "You Lose!:(");
            gameState = GameState.END;
            secondButton.setText("start game");
            secondTextFiled.setText("Game Over! You Lose!:(");
        }

        /************************* 	Getter & Setter	*************************/
        public boolean isWhite() {
            return white;
        }

        public void setWhite(boolean white) {
            this.white = white;
        }

        public JLabel getLeftPlayer() {
            return leftPlayer;
        }

        public TextField getLeftName() {
            return leftName;
        }

        public JLabel getRightPlayer() {
            return rightPlayer;
        }

        public TextField getRightName() {
            return rightName;
        }

        public JButton getSecondButton() {
            return secondButton;
        }

        public TextField getSecondTextFiled() {
            return secondTextFiled;
        }

        public JButton[][] getButtons() {
            return buttons;
        }
    }
}