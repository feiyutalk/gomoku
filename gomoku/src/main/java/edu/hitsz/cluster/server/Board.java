package edu.hitsz.cluster.server;

import edu.hitsz.commons.constants.Constants;

import java.io.Serializable;


/**
 * Created by Neuclil on 17-4-15.
 */
public class Board implements Serializable{
    private Color board[][];
    private int size;

    public enum Color {
        WHITE,
        BLACK,
        NONE
    }

    public Board() {
        size = Constants.DIMENSION;
        board = new Color[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Color.NONE;
            }
        }
    }

    public void undo(int y, int x) {
        board[y][x] = Color.NONE;
    }


    public boolean setPosition(int y, int x, Color color) {
        if (board[y][x] == Color.NONE) {
            board[y][x] = color;
            return true;
        } else
            return false;
    }

    public boolean isWin(int y, int x) {


        Color c = board[y][x];
        boolean iw = false;
        if (c != Color.NONE) {
            int left = 0, right = 0, up = 0, down = 0, lup = 0, ldown = 0, rup = 0, rdown = 0;
            for (int j = x-1; j >= 0 && board[y][j] == c; --j) {
                left++;
            }
            for (int j = x+1; j < this.size && board[y][j] == c; ++j) {
                right++;
            }
            for (int i = y-1; i >= 0 && board[i][x] == c; --i) {
                down++;
            }
            for (int i = y+1; i < this.size && board[i][x] == c; ++i) {
                up++;
            }
            for (int i = y+1, j = x-1; i <this.size && j >= 0 && board[i][j] == c; ++i, --j) {
                lup++;
            }
            for (int i = y-1, j = x+1; i >= 0 && j < size && board[i][j] == c; --i, ++j) {
                rdown++;
            }
            for (int i = y+1, j = x+1; i <this.size  && j < size && board[i][j] == c; ++i, ++j) {
                rup++;
            }
            for (int i = y-1, j = x-1; i >=0  && j >= 0 && board[i][j] == c; --i, --j) {
                ldown++;
            }

            iw = ((left + right) == 4) || ((up + down) == 4) || ((lup + rdown) == 4) || ((rup + ldown) == 4);
            StringBuilder builder = new StringBuilder();
            for(int i=0; i<board.length; i++){
                for(int j=0; j<board.length; j++){
                    builder.append(board[i][j].name() + " ");
                }
                builder.append("\n");
            }
            System.out.println(builder.toString());
            System.out.println("left="+left+" right="+right+" down="+down+" up="+up);
            System.out.println("ldown="+ldown+" rup="+rup+" lup="+lup+" rdown="+rdown);
        }

        return iw;
    }

    public void reset(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Color.NONE;
            }
        }
    }

    public void resetPosition(int x, int y) {
        board[x][y] = Color.NONE;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board.length; j++){
                builder.append(board[i][j].name() + " ");
            }
           builder.append("\n");
        }
        return builder.toString();
    }
}
