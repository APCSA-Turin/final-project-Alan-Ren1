package org.example.pieces;

import org.example.DisplayPanel;

public class Pawn extends Piece {
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        if (color == DisplayPanel.white) {
            image = getImage("/pieces/w-pawn");
        } else {
            image = getImage("/pieces/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if ((isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow))) {
            if (color == 0 && preRow == 6 && preRow - targetRow == 2 && preCol == targetCol && pieceGettingHit(targetCol, targetRow) == null && !pieceIsOnStraightLine(targetCol, targetRow)) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            } else if (color == 1 && preRow == 1 && targetRow - preRow == 2 && preCol == targetCol && pieceGettingHit(targetCol, targetRow) == null && !pieceIsOnStraightLine(targetCol, targetRow)) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            } else if (color == 0 && preRow - targetRow == 1 && preCol == targetCol && pieceGettingHit(targetCol, targetRow) == null) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            } else if (color == 1 && targetRow - preRow == 1 && preCol == targetCol && pieceGettingHit(targetCol, targetRow) == null) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
            //move diagonal white
            else if (color == 0 && Math.abs(targetCol - preCol) == 1 && targetRow == preRow -1 && pieceGettingHit(targetCol, targetRow) != null && pieceGettingHit(targetCol, targetRow).color != color) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
            //move diagonal black
            else if (color == 1 && Math.abs(targetCol - preCol) == 1 && targetRow == preRow +1 && pieceGettingHit(targetCol, targetRow) != null && pieceGettingHit(targetCol, targetRow).color != color) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
