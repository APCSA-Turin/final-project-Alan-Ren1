package org.example.pieces;

import org.example.DisplayPanel;

public class Knight extends Piece {
    public Knight(int color, int col, int row) {
        super(color, col, row);
        if (color == DisplayPanel.white) {
            image = getImage("/pieces/w-knight");
        }
        else {
            image = getImage("/pieces/b-knight");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
                if(isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
