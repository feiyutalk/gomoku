package edu.hitsz.cluster.client;

import edu.hitsz.cluster.client.processor.ClientRemotingDispatcher;
import edu.hitsz.cluster.client.state.GameState;
import edu.hitsz.commons.constants.Constants;
import edu.hitsz.commons.support.GameBoot;
import edu.hitsz.commons.utils.Parser;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.request.*;
import edu.hitsz.remoting.command.body.response.ConnectResponseBody;
import edu.hitsz.remoting.command.body.response.UndoResponseBody;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private AtomicBoolean connectServer = new AtomicBoolean(false);
    private AtomicBoolean gameStart = new AtomicBoolean(false);
    private AtomicBoolean inited = new AtomicBoolean(false);
    private AtomicBoolean remotingStarted = new AtomicBoolean(false);
    public static int opponId;

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
        gameState = GameState.UNCONNECTED;
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
        private int undoTimes = 3;
        private int moveNum = 0;
        private int lastX = 0;
        private int lastY = 0;
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
        private JButton connectButton = null;
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

        /* player list */
        private JLabel waitPlayer = null;
        private JLabel waitPlayerText = null;
        private DefaultListModel listModel = null;
        private JList playerList = null;
        private JScrollPane listPane = null;
        private JButton chanllengeButton = null;
        private JButton randomPickButton = null;

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
            connectButton = new JButton();

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

            // player list
            waitPlayer = new JLabel();
            waitPlayerText = new JLabel();
            listModel = new DefaultListModel();
            playerList = new JList(listModel);
            listPane = new JScrollPane();
            chanllengeButton = new JButton();
            randomPickButton = new JButton();
        }

        public void init() {
            this.setLayout(null);
            this.setSize(2 * Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH,
                    Constants.FIRST_PANEL_HEIGHT);
            /************************* 	玩家1	*************************/
            firstPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));

            firstPlayer.setBounds(25, 5,
                    Constants.FIRST_PLAYER_LOGO_WIDTH, Constants.FIRST_PLAYER_LOGO_HEIGHT);
            firstPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            try {
                Image image = ImageIO.read(getClass().getResource(
                        "/" + config.getGender() +
                                "/" + config.getImage() + ".png"));
                firstPlayer.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                e.printStackTrace();
            }

            firstName.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstName.setBounds(25, 5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 10,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstName.setText("Name:");

            firstNameText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstNameText.setBounds(25 + Constants.FIRST_NAME_LABEL_WIDTH, 5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 10,
                    Constants.FIRST_NAME_TEXT_WIDTH, Constants.FIRST_NAME_TEXT_HEIGHT);
            firstNameText.setText(config.getName());

            firstGender.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstGender.setBounds(25, 5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + Constants.FIRST_NAME_LABEL_HEIGHT + 15,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstGender.setText("Gender:");

            firstGenderText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstGenderText.setBounds(25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + Constants.FIRST_NAME_LABEL_HEIGHT + 15,
                    Constants.FIRST_NAME_TEXT_WIDTH, Constants.FIRST_NAME_TEXT_HEIGHT);
            firstGenderText.setText(config.getGender());

            firstAge.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstAge.setBounds(25, 5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 2 * Constants.FIRST_NAME_LABEL_HEIGHT + 20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstAge.setText("Age :");

            firstAgeText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstAgeText.setBounds(25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 2 * Constants.FIRST_NAME_LABEL_HEIGHT + 20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstAgeText.setText(config.getAge() + "");

            firstFrom.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstFrom.setBounds(25, 5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 3 * Constants.FIRST_NAME_LABEL_HEIGHT + 25,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            firstFrom.setText("From:");

            firstFromText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            firstFromText.setBounds(25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 3 * Constants.FIRST_NAME_LABEL_HEIGHT + 25,
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

            messageTextField.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE - 3));
            messageTextField.setBounds(Constants.FIRST_PANEL_WIDTH, 5, Constants.MESSAGE_TEXT_WIDTH,
                    Constants.MESSAGE_TEXT_HEIGHT);
            messageTextField.setEditable(false);
            messageTextField.setText("Hello, " + config.getName() + ". Welcome to GoMoKu Game!");

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
            startButton.setBounds(Constants.FIRST_PANEL_WIDTH + 25,
                    50 + Constants.BOARD_PANEL_WIDTH + 10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            startButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            startButton.setText("start");

            undoButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            undoButton.setBounds(Constants.FIRST_PANEL_WIDTH + 175,
                    50 + Constants.BOARD_PANEL_WIDTH + 10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            undoButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            undoButton.setText("undo 3");

            restartButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            restartButton.setBounds(Constants.FIRST_PANEL_WIDTH + 325,
                    50 + Constants.BOARD_PANEL_WIDTH + 10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            restartButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            restartButton.setText("restart");

            connectButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            connectButton.setBounds(Constants.FIRST_PANEL_WIDTH + 475,
                    50 + Constants.BOARD_PANEL_WIDTH + 10,
                    Constants.BOARD_BUTTON_WIDTH, Constants.BOARD_BUTTON_HEIGHT);
            connectButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            connectButton.setText("connect");

            this.add(messageTextField);
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    this.add(buttons[i][j]);
                }
            }
            this.add(startButton);
            this.add(undoButton);
            this.add(restartButton);
            this.add(connectButton);
            /************************* 	玩家2  *************************/
            secondPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            secondPlayer.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25, 5,
                    Constants.FIRST_PLAYER_LOGO_WIDTH, Constants.FIRST_PLAYER_LOGO_HEIGHT);
            secondPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
            try {
                Image image = ImageIO.read(getClass().getResource("/not.jpg"));
                secondPlayer.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                e.printStackTrace();
            }


            secondName.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondName.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 10,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondName.setText("Name:");

            secondNameText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondNameText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 10,
                    Constants.FIRST_NAME_TEXT_WIDTH,
                    Constants.FIRST_NAME_TEXT_HEIGHT);

            secondGender.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondGender.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + Constants.FIRST_NAME_LABEL_HEIGHT + 15,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondGender.setText("Gender:");

            secondGenderText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondGenderText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + Constants.FIRST_NAME_LABEL_HEIGHT + 15,
                    Constants.FIRST_NAME_TEXT_WIDTH,
                    Constants.FIRST_NAME_TEXT_HEIGHT);

            secondAge.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondAge.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 2 * Constants.FIRST_NAME_LABEL_HEIGHT + 20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondAge.setText("Age :");

            secondAgeText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondAgeText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 2 * Constants.FIRST_NAME_LABEL_HEIGHT + 20,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);

            secondFrom.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondFrom.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 3 * Constants.FIRST_NAME_LABEL_HEIGHT + 25,
                    Constants.FIRST_NAME_LABEL_WIDTH, Constants.FIRST_NAME_LABEL_HEIGHT);
            secondFrom.setText("From:");

            secondFromText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            secondFromText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 3 * Constants.FIRST_NAME_LABEL_HEIGHT + 25,
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

            /************************* 	play list	*************************/
            waitPlayer.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            waitPlayer.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 4 * Constants.FIRST_NAME_LABEL_HEIGHT + 30,
                    Constants.FIRST_NAME_LABEL_WIDTH,Constants.FIRST_NAME_LABEL_HEIGHT);
            waitPlayer.setText("wait:");

            waitPlayerText.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            waitPlayerText.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25 + Constants.FIRST_NAME_LABEL_WIDTH,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 4 * Constants.FIRST_NAME_LABEL_HEIGHT + 30,
                    Constants.FIRST_NAME_LABEL_WIDTH,Constants.FIRST_NAME_LABEL_HEIGHT);
            waitPlayerText.setText("0");

            playerList.setFixedCellWidth(200);
            playerList.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));

            listPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            listPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            listPane.setViewportView(playerList);
            listPane.setBorder(new LineBorder(new Color(0, 0, 0)));
            listPane.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 5 * Constants.FIRST_NAME_LABEL_HEIGHT + 20,
                    Constants.FIRST_PLAYER_LOGO_WIDTH, Constants.FIRST_PLAYER_LOGO_HEIGHT);

            chanllengeButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            chanllengeButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            chanllengeButton.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 5 * Constants.FIRST_NAME_LABEL_HEIGHT + Constants.FIRST_PLAYER_LOGO_HEIGHT + 25,
                    Constants.BOARD_BUTTON_WIDTH + 60, Constants.BOARD_BUTTON_HEIGHT);
            chanllengeButton.setText("chanllenge");

            randomPickButton.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
            randomPickButton.setBorder(new LineBorder(new Color(0, 0, 0)));
            randomPickButton.setBounds(Constants.FIRST_PANEL_WIDTH + Constants.BOARD_PANEL_WIDTH + 25,
                    5 + Constants.FIRST_PLAYER_LOGO_HEIGHT + 5 * Constants.FIRST_NAME_LABEL_HEIGHT + Constants.FIRST_PLAYER_LOGO_HEIGHT + 62,
                    Constants.BOARD_BUTTON_WIDTH + 60, Constants.BOARD_BUTTON_HEIGHT);
            randomPickButton.setText("random match");

            this.add(waitPlayer);
            this.add(waitPlayerText);
            this.add(listPane);
            this.add(chanllengeButton);
            this.add(randomPickButton);
            /************************* 	Frame	*************************/
            this.setTitle("HITSZ-GoMoKu");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);

            /************************* Action	*************************/
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState == GameState.READY) {
                        LOG.debug("start game");
                        if (gameStart.compareAndSet(false, true)) {
                            RemotingCommand request = RemotingCommand.createRequestCommand(
                                    RemotingProtos.RequestCode.START.code(),
                                    new StartRequestBody(config.getId()));
                            application.getRemotingClient().invokeSync(getServerAddr(), request);
                            GameBoot.start(application);
                        }
                    }
                }
            });

            undoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState == GameState.TURNING && undoTimes > 0 && moveNum > 0) {
                        RemotingCommand request = RemotingCommand.createRequestCommand(
                                RemotingProtos.RequestCode.UNDO.code(),
                                new UndoRequstBody(config.getId(), lastY, lastX));
                        RemotingCommand response = application.getRemotingClient().invokeSync(getServerAddr(), request);
                        if (response.getCode() == RemotingProtos.ResponseCode.UNDO_SUCCESS.code()) {
                            UndoResponseBody body = (UndoResponseBody) response.getBody();
                            undo();
                            undo(body.getLastY(), body.getLastX());
                            undoTimes--;
                            undoButton.setText("undo " + undoTimes);
                        }
                    }
                }
            });

            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState == GameState.TURNING ||
                            gameState == GameState.WAITING) {
                        lose();

                        resetBoard();

                        RemotingCommand request = RemotingCommand.createRequestCommand(
                                RemotingProtos.RequestCode.RESTART.code(),
                                new RestartRequestBody(config.getId()));

                        RemotingCommand response = application.getRemotingClient().invokeSync(getServerAddr(),
                                request);
                        if (response.getCode() == RemotingProtos.ResponseCode.RESTART_SUCCESS.code()) {
                            restartInfoPrint();
                        }
                    }
                    if (gameState == GameState.END) {
                        resetBoard();
                    }
                }
            });

            connectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState == GameState.UNCONNECTED) {
                        LOG.debug("start game, finding opponents...");
                        if (connectServer.compareAndSet(false, true)) {
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
                                    getServerAddr(),
                                    request
                            );
                            config.setId(((ConnectResponseBody) response.getBody()).getId());
                            if (response.getCode() == RemotingProtos.ResponseCode.CONNECT_SUCCESS.code()) {
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
                            int y = (button.getY() - Constants.BUTTONS_HEIGHT_OFF) / button.getHeight();
                            int x = (button.getX() - Constants.BUTTONS_WIDTH_OFF) / button.getWidth();
                            lastY = y;
                            lastX = x;
                            if (gameState == GameState.TURNING) {
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
                                            getServerAddr(),
                                            request);
                                    if (response.getCode() == RemotingProtos.ResponseCode.CHANGE_BOARD_SUCCESS.code()) {
                                        gameState = GameState.WAITING;
                                        boardState[y][x] = (isWhite() == true) ? BoardState.WHITE : BoardState.BLACK;
                                        moveNum++;
                                        messageTextField.setText("your opponent's turn, please wait.");
                                        LOG.debug("Server端棋局状态改变成功!");
                                    } else {
                                        LOG.debug("Server端棋局状态改变失败!");
                                    }
                                }
                            }
                        }
                    });
                }
            }

            playerList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameState == GameState.MATCHING) {
                        int target;
                        if (e.getClickCount() == 2) {
                            if (opponId == 0) {
                                if (!playerList.isSelectionEmpty()) {
                                    String s = (String) playerList.getSelectedValue();
                                    String[] infos = s.split("-");
                                    target = Integer.parseInt(infos[0]);
                                    if (target != config.getId()) {
                                        messageTextField.setText("wait for oppoenent accept challenge, please wait.");
                                        RemotingCommand request = RemotingCommand.createRequestCommand(
                                                RemotingProtos.RequestCode.CHALLENGE.code(),
                                                new ChallengeRequestBody(config.getId(), target));
                                        application.getRemotingClient().invokeSync(getServerAddr(), request);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "can't challenge yourself!");
                                    }
                                }
                            }
                        }
                    }
                }
            });

            chanllengeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState == GameState.MATCHING) {
                        int target;
                        if (opponId == 0) {
                            if (!playerList.isSelectionEmpty()) {
                                String s = (String) playerList.getSelectedValue();
                                String[] infos = s.split("-");
                                target = Integer.parseInt(infos[0]);
                                if (target != config.getId()) {
                                    messageTextField.setText("wait for oppoenent accept challenge, please wait.");
                                    RemotingCommand request = RemotingCommand.createRequestCommand(
                                            RemotingProtos.RequestCode.CHALLENGE.code(),
                                            new ChallengeRequestBody(config.getId(), target));
                                    application.getRemotingClient().invokeSync(getServerAddr(), request);
                                } else {
                                    JOptionPane.showMessageDialog(null, "can't challenge yourself!");
                                }
                            }
                        }
                    }
                }
            });

            randomPickButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState == GameState.MATCHING) {
                        int target;
                        if (opponId == 0) {
                            RemotingCommand request = RemotingCommand.createRequestCommand(
                                    RemotingProtos.RequestCode.RANDOM_MATCH.code(),
                                    new RandomMathRequestBody(config.getId()));
                            application.getRemotingClient().invokeSync(getServerAddr(), request);
                        }
                    }
                }
            });
        }


        /************************* 	method	*************************/

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

        public void exit() {
            JOptionPane.showMessageDialog(null, "Your Opponent Leave!");
            messageTextField.setText("Your Opponent Leave! find other opponent.");
        }

        public void exitWin() {
            JOptionPane.showMessageDialog(null, "Your Opponent Leave! You Win!:)");
            gameState = GameState.MATCHING;
            messageTextField.setText("Your Opponent Leave! You Win!:)");
        }

        public void win() {
            JOptionPane.showMessageDialog(null, "You Win!:)");
            gameState = GameState.MATCHING;
            messageTextField.setText("Game Over! You Win!:)");
        }

        public void lose() {
            JOptionPane.showMessageDialog(null, "You Lose!:(");
            gameState = GameState.END;
            messageTextField.setText("Game Over! You Lose!:(");
        }

        public void lose(boolean white, int y, int x) {
            changeState(white, y, x);
            JOptionPane.showMessageDialog(null, "You Lose!:(");
            gameState = GameState.END;
            messageTextField.setText("Game Over! You Lose!:(");
        }

        private String getServerAddr() {
            return config.getServerIp() + ":" + config.getServerPort();
        }

        public void reset() {
            opponId = 0;
            resetBoard();
            resetUndo();
            resetOpponent();
        }

        public void resetOpponent() {
            try {
                Image image = ImageIO.read(getClass().getResource("/not.jpg"));
                secondPlayer.setIcon(new ImageIcon(image));
            } catch (Exception e) {
                e.printStackTrace();
            }
            secondNameText.setText("");
            secondGenderText.setText("");
            secondAgeText.setText("");
            secondFromText.setText("");
        }

        public void resetUndo() {
            undoTimes = 3;
            undoButton.setText("undo 3");
        }

        public void resetBoard() {
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons.length; j++) {
                    JButton button = buttons[i][j];
                    try {
                        Image image = ImageIO.read(getClass().getResource(
                                "/background.gif"));
                        button.setIcon(new ImageIcon(image));
                        boardState[i][j] = BoardState.NONE;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        public void restartInfoPrint() {
            try {
                Thread.sleep(1000);
                messageTextField.setText("game begin......5");
                Thread.sleep(1000);
                messageTextField.setText("game begin......4");
                Thread.sleep(1000);
                messageTextField.setText("game begin......3");
                Thread.sleep(1000);
                messageTextField.setText("game begin......2");
                Thread.sleep(1000);
                messageTextField.setText("game begin......1");
                Thread.sleep(1000);
                if (isWhite()) {
                    messageTextField.setText("you turn.");
                    gameState = GameState.TURNING;
                } else {
                    messageTextField.setText("your opponent's turn, please wait.");
                    gameState = GameState.WAITING;
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        public void undo() {
            JButton button = buttons[lastY][lastX];
            try {
                Image image = ImageIO.read(getClass().getResource(
                        "/background.gif"));
                button.setIcon(new ImageIcon(image));
                boardState[lastY][lastX] = BoardState.NONE;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        public void undo(int lastY, int lastX) {
            JButton button = buttons[lastY][lastX];
            try {
                Image image = ImageIO.read(getClass().getResource(
                        "/background.gif"));
                button.setIcon(new ImageIcon(image));
                boardState[lastY][lastX] = BoardState.NONE;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        public void addPlayer(String name) {
            listModel.addElement(name);
            playerList.repaint();
        }

        public void removePlayer(String name) {
            listModel.removeElement(name);
            playerList.repaint();
        }

        public void clearPlayerList() {
            listModel.clear();
            playerList.repaint();
        }


        /************************* 	Getter & Setter	*************************/
        public JLabel getWaitPlayer() {
            return waitPlayer;
        }

        public void setWaitPlayer(JLabel waitPlayer) {
            this.waitPlayer = waitPlayer;
        }

        public JLabel getWaitPlayerText() {
            return waitPlayerText;
        }

        public void setWaitPlayerText(JLabel waitPlayerText) {
            this.waitPlayerText = waitPlayerText;
        }

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

        public JButton getConnectButton() {
            return connectButton;
        }

        public void setConnectButton(JButton connectButton) {
            this.connectButton = connectButton;
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

        public int getUndoTimes() {
            return undoTimes;
        }

        public void setUndoTimes(int undoTimes) {
            this.undoTimes = undoTimes;
        }

        public int getMoveNum() {
            return moveNum;
        }

        public void setMoveNum(int moveNum) {
            this.moveNum = moveNum;
        }

        public int getLastX() {
            return lastX;
        }

        public void setLastX(int lastX) {
            this.lastX = lastX;
        }

        public int getLastY() {
            return lastY;
        }

        public void setLastY(int lastY) {
            this.lastY = lastY;
        }

        public DefaultListModel getListModel() {
            return listModel;
        }

        public void setListModel(DefaultListModel listModel) {
            this.listModel = listModel;
        }

        public JList getPlayerList() {
            return playerList;
        }

        public void setPlayerList(JList playerList) {
            this.playerList = playerList;
        }

        public JScrollPane getListPane() {
            return listPane;
        }

        public void setListPane(JScrollPane listPane) {
            this.listPane = listPane;
        }

        public JButton getChanllengeButton() {
            return chanllengeButton;
        }

        public void setChanllengeButton(JButton chanllengeButton) {
            this.chanllengeButton = chanllengeButton;
        }

        public JButton getRandomPickButton() {
            return randomPickButton;
        }

        public void setRandomPickButton(JButton randomPickButton) {
            this.randomPickButton = randomPickButton;
        }

    }
}