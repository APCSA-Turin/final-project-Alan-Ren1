package org.example;
import java.awt.Graphics2D;
import java.awt.Color;

public class Board {
    final int maxRow = 8;
    final int maxCol = 8;
    public static final int squareSize = 100;
    public static final int HalfSquareSize = squareSize / 2;

    private boolean isFlipped = false;

    //method to set the board orientation
            public void setFlipped(boolean flipped) {
        this.isFlipped = flipped;
    }

    //method to get flipped rows
    public int getFlippedRow(int row) {
        if (isFlipped) {
            return maxRow - 1 - row;
        }
        return row;
    }
    //method to get flipped columns
    public int getFlippedCol(int col) {
        if (isFlipped) {
            return maxCol - 1 - col;
        }
        return col;
    }

    /*
    This method draws the board
     */
    public void draw(Graphics2D g2) {
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                //get the actual board position (flipped if needed)
                int boardRow = getFlippedRow(row);
                int boardCol = getFlippedCol(col);

                //determine color based on actual board position, rotates square color based on position
                if ((boardRow + boardCol) % 2 == 0) {
                    g2.setColor(new Color(210, 165, 125)); // Light squares
                } else {
                    g2.setColor(new Color(175, 115, 70));  // Dark squares
                }

                g2.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
            }
        }
    }

    // Helper methods for coordinate conversion
    public int getScreenX(int boardCol) {
        if (isFlipped) {
            return (maxCol - 1 - boardCol) * squareSize;
        }
        else {
            return boardCol * squareSize;
        }
    }

    public int getScreenY(int boardRow) {
        if (isFlipped) {
            return (maxRow - 1 - boardRow) * squareSize;
        }
        else {
            return boardRow * squareSize;
        }
    }

    public int getBoardCol(int screenX) {
        int col = screenX / squareSize;
        if (isFlipped) {
            return maxCol - 1 - col;
        }
        else {
            return col;
        }
    }

    public int getBoardRow(int screenY) {
        int row = screenY / squareSize;
        if (isFlipped) {
            return maxRow - 1 - row;
        }
        else {
            return row;
        }
    }
}