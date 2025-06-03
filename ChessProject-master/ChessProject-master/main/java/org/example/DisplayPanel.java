package org.example;
import org.example.pieces.*;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

public class DisplayPanel extends JPanel implements Runnable {
    public static final int width = 1550;
    public static final int height = 900;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //pieces
    public static ArrayList<Piece> pieces = new ArrayList<Piece>();
    public static ArrayList<Piece> simPieces = new ArrayList<Piece>();
    Piece activePiece;
    public static Piece castlingPiece;
    ArrayList<Piece> promotionPieces = new ArrayList<>();
    Piece checkingPiece;
    public String winner;
    public boolean checkCastlingPieceMoved = false;

    //color
    public static final int white = 0;
    public static final int black = 1;
    int currentColor = white;

    //booleans
    boolean canMove = false;
    boolean validSquare = false;
    boolean promotion = false;
    boolean gameover = false;


    private String playerName = "";
    private int playerRating = 0;
    private JTextField guessField;
    private JButton guessButton;
    private JButton nextButton;
    private JLabel nameLabel;
    private JLabel resultLabel;
    private boolean hasGuessed = false;
    private int averageDifference = 0;
    private int numGuesses = 0;

    /*
    Display panel method that creates a new window
    This sets up everything in the window so it is ready to be used
     */
    public DisplayPanel() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(width, height)); //sets the size of the window
        setLayout(null); //set the positioning of the window to the center

        setPieces(); //set pieces in its order and positions
        copyPieces(pieces, simPieces);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setupGuessUI();
        loadPlayer();
    }
    /*
    This method sets up the guess the rating UI
     */
    private void setupGuessUI() {
        // Player name
        nameLabel = new JLabel("Loading...");
        nameLabel.setBounds(1200, 320, 400, 40);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        add(nameLabel);

        // Guess field
        guessField = new JTextField();
        guessField.setBounds(1120, 400, 150, 40);
        guessField.setFont(new Font("Montserrat", Font.PLAIN, 18));
        add(guessField);

        // Guess button
        guessButton = new JButton("Guess!");
        guessButton.setBounds(1320, 400, 100, 40);
        guessButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        guessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                makeGuess();
            }
        });
        add(guessButton);

        // Result display
        resultLabel = new JLabel("");
        resultLabel.setBounds(815, 600, 800, 80);
        resultLabel.setForeground(Color.YELLOW);
        resultLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        add(resultLabel);

        // Next player button
        nextButton = new JButton("Next Player");
        nextButton.setBounds(1250, 480, 150, 40);
        nextButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadPlayer();
            }
        });
        add(nextButton);

        // Enter key support
        guessField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!hasGuessed) {
                    makeGuess();
                }
            }
        });
    }

    /*
    this method loads the player name from the API, parses it, and displays it on the UI
    it also sets playerRating to their rating so that it can be used to check the guess
     */
    private void loadPlayer() {
        resetGuessState();
        try {
            String data = API.getPlayerData(API.getRandomPlayerName("Players"));
            JSONObject playerData = new JSONObject(data);
            playerName = playerData.getString("username");

            JSONObject perfs = playerData.getJSONObject("perfs");
            JSONObject rapid = perfs.getJSONObject("rapid");
            playerRating = rapid.getInt("rating");

            nameLabel.setText("Player: " + playerName);

        } catch (Exception e) {
            nameLabel.setText("Error loading player");
        }
    }

    /*
    this method resets the guess state so that the UI is ready to be used again
     */
    private void resetGuessState() {
        nameLabel.setText("Loading...");
        resultLabel.setText("");
        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        nextButton.setEnabled(false);
        hasGuessed = false;
    }

    /*
    this method stimulates the guessing, compares the guess with the actual rating
    displays actual rating, user's guess, difference, and average difference
    it takes the difference in score, and checks whether the guess is a good guess, not bad guess, or way off
    catches an exception if the user does not enter a number
     */
    private void makeGuess() {
        if (hasGuessed || playerName.isEmpty()) {
            return;
        }

        try {
            int guess = Integer.parseInt(guessField.getText().trim());
            int diff = Math.abs(guess - playerRating);
            averageDifference = averageDifference * numGuesses;
            numGuesses++;
            averageDifference = averageDifference + diff;
            averageDifference = averageDifference / numGuesses;


            resultLabel.setPreferredSize(new Dimension(800, 40));

            String result = "Actual: " + playerRating + " | Your guess: " + guess + " | Difference: " + diff + " | Average Difference: " + averageDifference ;

            if (diff <= 20) {
                result += " - Great guess!";
                resultLabel.setForeground(Color.GREEN);
            } else if (diff <= 40) {
                result += " - Not bad!";
                resultLabel.setForeground(Color.YELLOW);
            } else {
                result += " - Try again!";
                resultLabel.setForeground(Color.RED);
            }

            resultLabel.setText(result);

            guessField.setEnabled(false);
            guessButton.setEnabled(false);
            nextButton.setEnabled(true);
            hasGuessed = true;

        } catch (NumberFormatException e) {
            resultLabel.setText("Enter a number!");
            resultLabel.setForeground(Color.RED);
        }
    }


    //launches the game
    public void launchGame() {
        gameThread = new Thread(this); // pass this as Runnable
        gameThread.start();
    }

    //adds pieces to the ArrayList by creating new objects
    public void setPieces() {
        //White Team
        pieces.add(new Pawn(white, 0, 6));
        pieces.add(new Pawn(white, 1, 6));
        pieces.add(new Pawn(white, 2, 6));
        pieces.add(new Pawn(white, 3, 6));
        pieces.add(new Pawn(white, 4, 6));
        pieces.add(new Pawn(white, 5, 6));
        pieces.add(new Pawn(white, 6, 6));
        pieces.add(new Pawn(white, 7, 6));
        pieces.add(new Rook(white, 0, 7));
        pieces.add(new Rook(white, 7, 7));
        pieces.add(new Knight(white, 1, 7));
        pieces.add(new Knight(white, 6, 7));
        pieces.add(new Bishop(white, 2, 7));
        pieces.add(new Bishop(white, 5, 7));
        pieces.add(new Queen(white, 3, 7));
        pieces.add(new King(white, 4, 7));

        //Black Team
        pieces.add(new Pawn(black, 0, 1));
        pieces.add(new Pawn(black, 1, 1));
        pieces.add(new Pawn(black, 2, 1));
        pieces.add(new Pawn(black, 3, 1));
        pieces.add(new Pawn(black, 4, 1));
        pieces.add(new Pawn(black, 5, 1));
        pieces.add(new Pawn(black, 6, 1));
        pieces.add(new Pawn(black, 7, 1));
        pieces.add(new Rook(black, 0, 0));
        pieces.add(new Rook(black, 7, 0));
        pieces.add(new Knight(black, 1, 0));
        pieces.add(new Knight(black, 6, 0));
        pieces.add(new Bishop(black, 2, 0));
        pieces.add(new Bishop(black, 5, 0));
        pieces.add(new Queen(black, 3, 0));
        pieces.add(new King(black, 4, 0));
    }

    //this method copies the arraylist from the soure to the target in the case of needing to revert an action
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    //runs the game and loop it
    public void run() {
        //GAME LOOP
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    /*
    checks whether the position is illegal for the king by
    checking if any of the pieces on the other team, except for the king, can move to the position in the king is on
     */
    private boolean isIllegal(Piece king) {
        if (king instanceof King) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void update() {
        if (promotion) {
            promoting();
        } else if (gameover == false && isStalemate() == false) {
            if (mouse.pressed) {
                if (activePiece == null) {
                    for (Piece piece : simPieces) {
                        //convert screen coordinates to board coordinates
                        int boardCol = board.getBoardCol(mouse.x);
                        int boardRow = board.getBoardRow(mouse.y);

                        if (piece.color == currentColor && piece.col == boardCol && piece.row == boardRow) {
                            activePiece = piece;
                        }
                    }
                } else {
                    //if the player is holding a piece, then simulate the move
                    gameSimulation();
                }
            }
            // Mouse Button Released
            if (!mouse.pressed) {
                if (activePiece != null) {
                    if (validSquare) {
                        //move confirmed
                        //update the piece list if a piece gets captured and removed during the simulation
                        copyPieces(simPieces, pieces);
                        activePiece.updatePosition();
                        if (castlingPiece != null) {
                            castlingPiece.updatePosition();
                        }
                        if (isKingInCheck() && isCheckmate()) { //checks whether the game is over due to checkmate
                            gameover = true;
                        } else if (isStalemate()) {
                            gameover = true;
                        } else { //the game is still running
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }
                    } else {
                        //if the move is not valid then reset everything
                        copyPieces(pieces, simPieces);
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }
    }

    private void gameSimulation() {
        canMove = false;
        validSquare = false;
        copyPieces(pieces, simPieces);

        if (castlingPiece != null) {
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }

        //converts mouse coordinates to board coordinates
        int targetBoardCol = board.getBoardCol(mouse.x);
        int targetBoardRow = board.getBoardRow(mouse.y);

        //updates the active piece position
        activePiece.x = mouse.x - Board.HalfSquareSize;
        activePiece.y = mouse.y - Board.HalfSquareSize;
        activePiece.col = targetBoardCol;
        activePiece.row = targetBoardRow;

        if (activePiece.canMove(activePiece.col, activePiece.row)) {
            canMove = true;

            if (activePiece.hittingPiece != null) {
                simPieces.remove(activePiece.hittingPiece.getIndex());
            }
            checkCastling();
            if (!isIllegal(activePiece) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        }
    }

    //this method changes the player, sets current color to the opposite color, and flip the board
    private void changePlayer() {
        if (currentColor == white) {
            currentColor = black;
            board.setFlipped(true);  // Flip board for black's turn
        } else {
            currentColor = white;
            board.setFlipped(false); // Normal orientation for white's turn
        }
        activePiece = null;
    }

    /*
    checks if the
     */
    private void checkCastling() {
        if (castlingPiece != null) {
            if (castlingPiece.col == 0) {
                castlingPiece.col += 3;
            } else if (castlingPiece.col == 7) {
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }
    //check if the opponent can capture the king, returns true or false
    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false);
        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    /*
    this method checks if the player can promote their pawn to another piece
    if they can, clear the arraylist called "promotionPieces"
    and add all the pieces that a pawn can promote to into the arraylist with their position all set to the position of the pawn
    returns true if the user can promote their pawn
    otherwise returns false
     */
    private boolean canPromote() {
        if (activePiece instanceof Pawn) {
            if (activePiece.color == white && activePiece.row == 0 || activePiece.color == black && activePiece.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Queen(activePiece.color, activePiece.col, activePiece.row));
                promotionPieces.add(new Knight(activePiece.color, activePiece.col, activePiece.row));
                promotionPieces.add(new Bishop(activePiece.color, activePiece.col, activePiece.row));
                promotionPieces.add(new Rook(activePiece.color, activePiece.col, activePiece.row));
                return true;
            }
        }
        return false;
    }

    //if the king is the only piece left and it can't move, its a stalemate
    private boolean isStalemate() {
        int count = 0;
        //count the number of pieces
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }

        if (count == 1) {
            if (kingCanMove(getKing(true)) == false) {
                return true;
            }
        }
        return false;
    }

    /*
    this method checks for checkmate by
    checking if the king can move
    then checks where the piece is attacking from
    then checks to see if there is any piece that can protect the king
     */
    private boolean isCheckmate() {
        Piece king = getKing(true);
        if (kingCanMove(king)) {
            return false;
        } else {
            //check the position of the checking piece and the king in check
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if (colDiff == 0) {
                //The checking piece is attacking vertically
                if (checkingPiece.row < king.row) {
                    //The checking piece is above the king
                    for (int row = checkingPiece.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.row > king.row) {
                    //The checking piece is below the king
                    for (int row = checkingPiece.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
                                return false;
                            }
                        }
                    }
                }

            } else if (rowDiff == 0) {
                //the checking piece is attacking horizontally
                if (checkingPiece.col < king.col) {
                    for (int col = checkingPiece.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.col > king.col) {
                    for (int col = checkingPiece.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                //The checking piece is attacking diagonally
                if (checkingPiece.row < king.row) {
                    //the checking piece is above the king
                    if (checkingPiece.col < king.col) {
                        //the checking piece is in the upper left
                        int c = checkingPiece.col;
                        int r = checkingPiece.row;

                        while (c < king.col && r < king.row) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(c, r)) {
                                    return false;
                                }
                            }
                            c++;
                            r++;
                        }
                    }
                    if (checkingPiece.col > king.col) {
                        //The checking piece is in the upper right
                        int c = checkingPiece.col;
                        int r = checkingPiece.row;

                        while (c < king.col && r < king.row) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(c, r)) {
                                    return false;
                                }
                            }
                            c++;
                            r++;
                        }
                    }

                }
                if (checkingPiece.row > king.row) {
                    //The checking piece is below the king
                    if (checkingPiece.col < king.col) {
                        //The checking piece is in the lower left
                        int c = checkingPiece.col;
                        int r = checkingPiece.row;

                        while (c < king.col && r < king.row) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(c, r)) {
                                    return false;
                                }
                            }
                            c++;
                            r--;
                        }
                    }
                    if (checkingPiece.col > king.col) {
                        //The checking piece is in the lower right
                        int c = checkingPiece.col;
                        int r = checkingPiece.row;

                        while (c > king.col && r < king.row) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(c, r)) {
                                    return false;
                                }
                            }
                            c--;
                            r--;
                        }
                    }
                }
            }
        }
        return true;
    }

    //checks if the king can move anywhere and not be in check
    private boolean kingCanMove(Piece king) {
        //simulate anywhere the king can go
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        return false;
    }

    /*
    this method checks if the king is able to move to the target position by
    checking if the target position is illegal for the king
     */
    private boolean isValidMove(Piece king, int targetCol, int targetRow) {
        boolean validMove = false;
        int newCol = king.col + targetCol;
        int newRow = king.row + targetRow;

        if (king.canMove(newCol, newRow)) {
            if (king.hittingPiece != null) {
                simPieces.remove(king.hittingPiece.getIndex());
            }
            if (!isIllegal(king)) {
                validMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return validMove;
    }
    //this method checks if the king is in check by checking if any opponent piece can move to the king's position
    private boolean isKingInCheck() {
        Piece king = getKing(true);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                checkingPiece = piece;
                return true;
            }
        }
        checkingPiece = null;
        return false;
    }

    /*
    this method gets the king piece based on the boolean parameter
    if true : returns the current player's opponent's king
    if false : returns the current player's king
     */
    private Piece getKing(boolean opponent) {
        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece instanceof King && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece instanceof King && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    /*
    this method simulates the promotion of a pawn
    changes the pawn to whatever the user clicked based on the mouse position
    by removing the pawn and adding the piece that was clicked
    then change player
     */
    private void promoting() {
        if (mouse.pressed) {
            int promotionX = 900; //the x-axis of the promotional pieces
            int[] promotionYPositions = {190, 290, 390, 490}; //array of different Y positions

            //check if click is within any of the promotion piece areas
            for (int i = 0; i < promotionYPositions.length; i++) {
                int pieceX = promotionX;
                int pieceY = promotionYPositions[i];

                //check if mouse click is within this piece's area
                if (mouse.x >= pieceX && mouse.x <= pieceX + Board.squareSize &&
                        mouse.y >= pieceY && mouse.y <= pieceY + Board.squareSize) {

                    //create the appropriate piece based on the clicked position
                    if (i == 0) { // Queen
                        simPieces.add(new Queen(activePiece.color, activePiece.col, activePiece.row));
                    } else if (i == 1) { // Knight
                        simPieces.add(new Knight(activePiece.color, activePiece.col, activePiece.row));
                    } else if (i == 2) { // Bishop
                        simPieces.add(new Bishop(activePiece.color, activePiece.col, activePiece.row));
                    } else if (i == 3) { // Rook
                        simPieces.add(new Rook(activePiece.color, activePiece.col, activePiece.row));
                    }

                    // Remove the original pawn and finalize the promotion
                    simPieces.remove(activePiece.getIndex());
                    copyPieces(simPieces, pieces);
                    activePiece = null;
                    promotion = false;
                    changePlayer();
                    break;
                }
            }
        }
    }

    /*
    this method is for all of the coloring and displaying of the chess game
    draws the chessboard and all the pieces (except the one being dragged)
    highlights the square under the dragged piece with red if the move is illegal, or white if its valid
    draws the dragged piece at the mouse location so it follows the cursor
    displays white to move, black to move, and promotion options
    shows messages like check, checkmate, or stalemate based on the game state.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        board.draw(g2);

        //draw all pieces except the one being dragged (activePiece)
        for (Piece piece : simPieces) {
            // Only draw if this is not the activePiece
            if (piece != activePiece) {
                piece.x = board.getScreenX(piece.col);
                piece.y = board.getScreenY(piece.row);
                piece.draw(g2);
            }
        }

        // Highlighting and drawing the activePiece
        if (activePiece != null) {
            if (canMove) {
                int screenCol = mouse.x / Board.squareSize;
                int screenRow = mouse.y / Board.squareSize;

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

                if (isIllegal(activePiece) || opponentCanCaptureKing()) {
                    g2.setColor(Color.red);
                } else {
                    g2.setColor(Color.white);
                }

                g2.fillRect(screenCol * Board.squareSize, screenRow * Board.squareSize, Board.squareSize, Board.squareSize);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            //draw activePiece last, at mouse position
            activePiece.draw(g2);
        }

        //render UI elements
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.setFont(new Font("Montserrat", Font.PLAIN, 40));

        if (promotion) {
            g2.drawString("Promote to:", 840, 150);
            g2.drawImage(promotionPieces.get(0).image, 900, 190, Board.squareSize, Board.squareSize, null);
            g2.drawImage(promotionPieces.get(1).image, 900, 290, Board.squareSize, Board.squareSize, null);
            g2.drawImage(promotionPieces.get(2).image, 900, 390, Board.squareSize, Board.squareSize, null);
            g2.drawImage(promotionPieces.get(3).image, 900, 490, Board.squareSize, Board.squareSize, null);
        } else {
            if (currentColor == white) {
                g2.drawString("WHITE TO MOVE", 850, 550);
                if (checkingPiece != null && checkingPiece.color == black) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            } else {
                g2.drawString("BLACK TO MOVE", 850, 300);
                if (checkingPiece != null && checkingPiece.color == white) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 100);
                    g2.drawString("is in check!", 840, 150);
                }
            }

            if (gameover) {
                String winner = (currentColor == white) ? "White Wins!" : "Black Wins!";
                g2.setFont(new Font("Montserrat", Font.PLAIN, 90));
                g2.setColor(Color.green);
                g2.drawString(winner, 200, 420);
            }

            if (isStalemate()) {
                g2.setFont(new Font("Montserrat", Font.PLAIN, 90));
                g2.setColor(Color.lightGray);
                g2.drawString("Stalemate!", 200, 420);
            }
        }
    }
}