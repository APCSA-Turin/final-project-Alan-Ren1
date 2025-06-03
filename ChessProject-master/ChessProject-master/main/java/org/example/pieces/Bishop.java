package org.example.pieces;

import org.example.DisplayPanel;

public class Bishop extends Piece {
    public Bishop(int color, int col, int row) {
        super(color, col, row);
        if (color == DisplayPanel.white) {
            image = getImage("/pieces/w-bishop");
        }
        else {
            image = getImage("/pieces/b-bishop");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(targetCol, targetRow) && !pieceIsOnADiagonal(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
