package org.example.pieces;

import org.example.DisplayPanel;

public class King extends Piece {
    public King(int color, int col, int row) {
        super(color, col, row);
        if (color == DisplayPanel.white) {
            image = getImage("/pieces/w-king");
        }
        else {
            image = getImage("/pieces/b-king");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetRow, targetCol)) {
            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
                if(isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
            if (!moved) {
                //RIGHT CASTLE
                if (targetCol == preCol + 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    for (Piece piece : DisplayPanel.simPieces) {
                        if (piece.col == preCol && piece.row == preRow && piece.moved == false) {
                            DisplayPanel.castlingPiece = piece;
                            return true;
                        }
                    }
                }
                //LEFT CASTLE
                if (targetCol == preCol - 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    Piece[] castlePieces = new Piece[2];
                    for (Piece piece : DisplayPanel.simPieces) {
                        if (piece.col == preCol - 3 && piece.row == targetRow) {
                            castlePieces[0] = piece;
                        }
                        if(piece.col == preCol - 4 && piece.row == targetRow) {
                            castlePieces[1] = piece;
                        }
                        if (castlePieces[0] == null && castlePieces[1] != null && !castlePieces[1].moved) {
                            DisplayPanel.castlingPiece = castlePieces[1];
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
