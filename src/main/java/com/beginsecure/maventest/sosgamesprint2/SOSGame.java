package com.beginsecure.maventest.sosgamesprint2;

/**
 * This class contains the core game logic for SOS.
 * It manages the board size, game mode, and the placement of S/O by players.
 */
public class SOSGame {

    private char[][] board; // Game board
    private int boardSize; // Size of the board
    private boolean isPlayerOneTurn = true; // Track player turns

    /**
     * Initializes the SOS game with a specified board size.
     *
     * @param boardSize the size of the board in a squared fashion (ex. 3 for a 3x3 board)
     */
    public SOSGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new char[boardSize][boardSize];
    }

    /**
     * Places a character (S or O) on the board for the current player.
     *
     * @param row the row position
     * @param col the column position
     * @param character the character ('S' or 'O')
     * @return true if the placement is valid, false otherwise
     */
    public boolean placeMove(int row, int col, char character) {
        if (isValidMove(row, col) && (character == 'S' || character == 'O')) {
            board[row][col] = character;
            switchTurn();
            return true;
        }

        else{
            System.out.println("Error: invalid position or character");
        }

        return false;
    }

    /**
     * Validates if the move is within the board and the position is empty.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize && board[row][col] == '\0';
        //board[row][col] == '\0' makes sure the space is empty, rest makes sure position is on board
    }

    /**
     * Switches the turn between player one and player two.
     */
    private void switchTurn() {
        isPlayerOneTurn = !isPlayerOneTurn;
    }

    /**
     * Checks if it is player one's turn.
     *
     * @return true if it's player one's turn, false otherwise
     */
    public boolean isPlayerOneTurn() {
        return isPlayerOneTurn;
    }

    /**
     * Returns the current state of the board.
     *
     * @return the game board
     */
    public char[][] getBoard() {
        return board;
    }
}
