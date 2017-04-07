package edu.hitsz.cluster.Client;

import edu.hitsz.commons.constants.Constants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by Neuclil on 4/7/2017.
 */
public class Board extends JFrame {

    private JPanel boardPanel = null;
    private JLabel leftPlayer = null;
    private TextField leftName = null;
    private JLabel rightPlayer = null;
    private TextField rightName = null;
    private TextArea win = null;

    public Board() throws HeadlessException {
        this.boardPanel = new JPanel();
        this.leftPlayer = new JLabel();
        this.leftName = new TextField();
        this.rightPlayer = new JLabel();
        this.rightName = new TextField();
        this.win = new TextArea();
        init();
    }

    private void init() {
        this.setLayout(null);
        this.setSize(Constants.BOARD_WIDTH, Constants.BOARD_HIGHT + 2 * Constants.JLABLE_PLAYER_HEIGHT);

        /************************* 	First Line	*************************/
        leftPlayer.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
        leftPlayer.setBounds(0,0,Constants.JLABLE_PLAYER_WIDTH,Constants.JLABLE_PLAYER_HEIGHT);
        leftPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
        leftPlayer.setText("Player1");

        leftName.setFont(new Font("Times",Font.PLAIN,Constants.FONT_SIZE));
        leftName.setBounds(Constants.JLABLE_PLAYER_WIDTH, 0, Constants.TEXTFIELD_NAME_WIDTH, Constants.TEXTFIELD_NAME_HEIGHT);
        leftName.setText("Mike");
        leftName.setEditable(false);

        rightPlayer.setFont(new Font("Times", Font.CENTER_BASELINE, Constants.FONT_SIZE));
        rightPlayer.setBounds(Constants.JLABLE_PLAYER_WIDTH+Constants.TEXTFIELD_NAME_WIDTH,
                              0, Constants.JLABLE_PLAYER_WIDTH, Constants.JLABLE_PLAYER_HEIGHT);
        rightPlayer.setBorder(new LineBorder(new Color(0, 0, 0)));
        rightPlayer.setText("Player2");

        rightName.setFont(new Font("Times",Font.PLAIN,Constants.FONT_SIZE));
        rightName.setBounds(2*Constants.JLABLE_PLAYER_WIDTH+Constants.TEXTFIELD_NAME_WIDTH,
                            0, Constants.TEXTFIELD_NAME_WIDTH,Constants.TEXTFIELD_NAME_HEIGHT);
        rightName.setText("Jack");
        rightName.setEditable(false);

        /************************* 	Second Line	*************************/


        /************************* 	Panel	*************************/
        boardPanel.setSize(Constants.BOARD_WIDTH, Constants.BOARD_HIGHT);
        boardPanel.setBounds(0, 2 * Constants.JLABLE_PLAYER_HEIGHT, Constants.BOARD_WIDTH, Constants.BOARD_HIGHT);
        boardPanel.setBorder(new LineBorder(new Color(0, 0, 0)));

        /************************* 	Frame	*************************/
        this.add(boardPanel);
        this.add(leftPlayer);
        this.add(leftName);
        this.add(rightPlayer);
        this.add(rightName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Board board = new Board();
    }
}
