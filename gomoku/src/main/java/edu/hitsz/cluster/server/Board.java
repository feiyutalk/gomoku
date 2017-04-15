package edu.hitsz.cluster.server;

import edu.hitsz.commons.constants.Constants;


/**
 * Created by Neuclil on 17-4-15.
 */
public class Board {
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
            for (int i = x; i > 0 && board[y][i] == c; --i) {
                left++;
            }
            for (int i = x+1; i < this.size && board[y][i] == c; ++i) {
                right++;
            }
            for (int j = y; j > 0 && board[j][x] == c; --j) {
                down++;
            }
            for (int j = y+1; j < this.size && board[j][x] == c; ++j) {
                up++;
            }
            for (int i = y, j = x; i <this.size && j > 0 && board[i][j] == c; ++i, --j) {
                lup++;
            }
            for (int i = y-1, j = x+1; i > 0 && j < size && board[i][j] == c; --i, ++j) {
                rdown++;
            }
            for (int i = y, j = x; i <this.size  && j < size && board[i][j] == c; ++i, ++j) {
                rup++;
            }
            for (int i = y-1, j = x-1; i >0  && j > 0 && board[i][j] == c; --i, --j) {
                ldown++;
            }

            iw = ((left + right) == 5) || ((up + down) == 5) || ((lup + rdown) == 5) || ((rup + ldown) == 5);
        }

        return iw;
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
