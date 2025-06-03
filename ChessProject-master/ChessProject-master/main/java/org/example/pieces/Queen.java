package org.example.pieces;

import org.example.DisplayPanel;

public class Queen extends Piece {
    public Queen(int color, int col, int row) {
        super(color, col, row);
        if (color == DisplayPanel.white) {
            image = getImage("/pieces/w-queen");
        }
        else {
            image = getImage("/pieces/b-queen");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if ((isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow))) {
            if ((targetCol == preCol || targetRow == preRow)) {
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    return true;
                }
            }
            else if ((Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow))) {
                if (isValidSquare(targetCol, targetRow) && !pieceIsOnADiagonal(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }

}
