package org.example.pieces;

import org.example.Board;
import org.example.DisplayPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;

//superclass of all of the subclasses that makes up each of the pieces
public class Piece {
    public BufferedImage image;
    public int x;
    public int y;
    public int col;
    public int row;
    public int preCol;
    public int preRow;
    public int color;
    public Piece hittingPiece;
    public boolean moved;

    public Piece (int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    /*
    this method retrieves and returns an image
    the image depends on the argument, which tells the image path
     */
    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    //get methods
    public int getX(int col) {
        return col * Board.squareSize;
    }
    public int getY(int row) {
        return row * Board.squareSize;
    }
    public int getCol(int x) {
        return (x + Board.HalfSquareSize) / Board.squareSize;
    }
    public int getRow(int y) {
        return (y + Board.HalfSquareSize) / Board.squareSize;
    }
    public int getIndex() {
        for(int i = 0; i < DisplayPanel.simPieces.size(); i++) {
            if (DisplayPanel.simPieces.get(i) == this) {
                return i;
            }
        }
        return -1;
    }

    public void updatePosition() {
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        if (targetCol == preCol && targetRow == preRow) {
            return true;
        }
        return false;
    }


    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    //checks if the piece's target position is within board and not out of bounds
    public boolean isWithinBoard (int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol < 8 && targetRow >= 0 && targetRow < 8) {
            return true;
        }
        else {
            return false;
        }
    }

    /*
    this method is a helper method to check if there's a piece between the rook and the target position
    this prevents the rook to go through pieces
    */
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        //Piece is moving to the left
        for (int c = preCol-1; c > targetCol; c--) {
            for (Piece piece : DisplayPanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        //Piece is moving to the right
        for (int c = preCol+1; c < targetCol; c++) {
            for (Piece piece : DisplayPanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        //Piece is moving up
        for (int r = preRow-1; r > targetRow; r--) {
            for (Piece piece : DisplayPanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        //Piece is moving down
        for (int r = preRow+1; r < targetRow; r++) {
            for (Piece piece : DisplayPanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        return false;

    }

    public Piece pieceGettingHit(int targetCol, int targetRow) {
        for (Piece piece : DisplayPanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingPiece = pieceGettingHit(targetCol, targetRow);
        if (hittingPiece == null) {
            return true;
        }
        else {
            if (hittingPiece.color != this.color) {
                return true;
            }
        }
        return false;
    }

    /*
    this method is a helper method to check if there's a piece between the bishop and the target position
    this prevents the bishop to go through pieces
     */
    public boolean pieceIsOnADiagonal(int targetCol, int targetRow) {
        if (targetRow < preRow) {
            //Up Left
            for (int c = preCol-1; c > targetCol; c--) {
                int d = Math.abs(c - preCol);
                for (Piece piece : DisplayPanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - d && piece != this) {
                        return true;
                    }
                }
            }
            //Up right
            for (int c = preCol+1; c < targetCol; c++) {
                int d = Math.abs(c - preCol);
                for (Piece piece : DisplayPanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - d && piece != this) {
                        return true;
                    }
                }
            }
        }
        if (targetRow > preRow) {
            //Down Left
            for (int c = preCol-1; c > targetCol; c--) {
                int d = Math.abs(c - preCol);
                for (Piece piece : DisplayPanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + d && piece != this) {
                        return true;
                    }
                }
            }
            //Down Right
            for (int c = preCol+1; c < targetCol; c++) {
                int d = Math.abs(c - preCol);
                for (Piece piece : DisplayPanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + d && piece != this) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.squareSize, Board.squareSize, null);
    }
}
