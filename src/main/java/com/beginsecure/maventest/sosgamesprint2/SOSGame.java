package com.beginsecure.maventest.sosgamesprint2;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains the core game logic for SOS. This class manages the board size, player turns, and the
 * placement of 'S' or 'O' by players.
 */
public class SOSGame {

    private Set<String> detectedSOS;
    private char[][] board;  // The game board
    private int boardSize;   // Size of the board
    protected boolean isPlayerOneTurn = true;  // Track player turns

    /**
     * Initializes the SOS game with a specified board size.
     *
     * @param boardSize the size of the board in a square dimension (e.g., 3 for a 3x3 board)
     */
    public SOSGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new char[boardSize][boardSize];
        this.detectedSOS = new HashSet<>();
    }

    /**
     * Places a character ('S' or 'O') on the board for the current player.
     *
     * @param row the row position
     * @param col the column position
     * @param character the character ('S' or 'O') to place
     * @return true if the placement is valid, false otherwise
     */
    public boolean placeMove(int row, int col, char character) {
        if (isValidMove(row, col) && (character == 'S' || character == 'O')) {
            board[row][col] = character;
            switchTurn();
            return true;
        }
        return false;
    }

    /**
     * Counts the number of new SOS patterns formed by the recent move.
     *
     * @param row the row position of the placed character
     * @param col the column position of the placed character
     * @return the count of new SOS formations detected
     */
    protected int checkForSOSCount(int row, int col) {
        int sosCount = 0;
        char currentChar = board[row][col];
        if (currentChar != 'S') {
            return sosCount;
        }

        // Horizontal checks for SOS
        if (col >= 2 && board[row][col - 2] == 'S' && board[row][col - 1] == 'O') {
            String key = sosKey(row, col - 2, row, col - 1, row, col);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (col >= 1 && col < boardSize - 1 && board[row][col - 1] == 'O' && board[row][col + 1] == 'S') {
            String key = sosKey(row, col - 1, row, col, row, col + 1);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (col < boardSize - 2 && board[row][col + 1] == 'O' && board[row][col + 2] == 'S') {
            String key = sosKey(row, col, row, col + 1, row, col + 2);
            if (detectedSOS.add(key)) sosCount++;
        }

        // Vertical checks for SOS
        if (row >= 2 && board[row - 2][col] == 'S' && board[row - 1][col] == 'O') {
            String key = sosKey(row - 2, col, row - 1, col, row, col);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (row >= 1 && row < boardSize - 1 && board[row - 1][col] == 'O' && board[row + 1][col] == 'S') {
            String key = sosKey(row - 1, col, row, col, row + 1, col);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (row < boardSize - 2 && board[row + 1][col] == 'O' && board[row + 2][col] == 'S') {
            String key = sosKey(row, col, row + 1, col, row + 2, col);
            if (detectedSOS.add(key)) sosCount++;
        }

        // Diagonal checks (top-left to bottom-right)
        if (row >= 2 && col >= 2 && board[row - 2][col - 2] == 'S' && board[row - 1][col - 1] == 'O') {
            String key = sosKey(row - 2, col - 2, row - 1, col - 1, row, col);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (row >= 1 && row < boardSize - 1 && col >= 1 && col < boardSize - 1 &&
                board[row - 1][col - 1] == 'O' && board[row + 1][col + 1] == 'S') {
            String key = sosKey(row - 1, col - 1, row, col, row + 1, col + 1);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (row < boardSize - 2 && col < boardSize - 2 && board[row + 1][col + 1] == 'O' &&
                board[row + 2][col + 2] == 'S') {
            String key = sosKey(row, col, row + 1, col + 1, row + 2, col + 2);
            if (detectedSOS.add(key)) sosCount++;
        }

        // Reverse-diagonal checks (top-right to bottom-left)
        if (row >= 2 && col < boardSize - 2 && board[row - 2][col + 2] == 'S' &&
                board[row - 1][col + 1] == 'O') {
            String key = sosKey(row - 2, col + 2, row - 1, col + 1, row, col);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (row >= 1 && row < boardSize - 1 && col < boardSize - 1 && col >= 1 &&
                board[row - 1][col + 1] == 'O' && board[row + 1][col - 1] == 'S') {
            String key = sosKey(row - 1, col + 1, row, col, row + 1, col - 1);
            if (detectedSOS.add(key)) sosCount++;
        }
        if (row < boardSize - 2 && col >= 2 && board[row + 1][col - 1] == 'O' &&
                board[row + 2][col - 2] == 'S') {
            String key = sosKey(row, col, row + 1, col - 1, row + 2, col - 2);
            if (detectedSOS.add(key)) sosCount++;
        }

        return sosCount;
    }

    /**
     * Generates a unique key for the SOS pattern based on positions.
     *
     * @param r1 row of the first 'S' in the pattern
     * @param c1 column of the first 'S'
     * @param r2 row of the 'O' in the pattern
     * @param c2 column of the 'O'
     * @param r3 row of the second 'S' in the pattern
     * @param c3 column of the second 'S'
     * @return a unique string key for identifying the SOS pattern
     */
    protected String sosKey(int r1, int c1, int r2, int c2, int r3, int c3) {
        return r1 + "," + c1 + ":" + r2 + "," + c2 + ":" + r3 + "," + c3;
    }

    /**
     * Checks if the board is full.
     *
     * @return true if there are no empty spaces on the board, false otherwise
     */
    protected boolean isBoardFull() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates if a move is within the board bounds and the position is empty.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize && board[row][col] == '\0';
    }

    /**
     * Switches the turn between Player 1 and Player 2.
     */
    protected void switchTurn() {
        isPlayerOneTurn = !isPlayerOneTurn;
    }

    /**
     * Checks if it is Player 1's turn.
     *
     * @return true if it is Player 1's turn, false otherwise
     */
    public boolean isPlayerOneTurn() {
        return isPlayerOneTurn;
    }

    /**
     * Retrieves the current state of the board.
     *
     * @return the game board
     */
    public char[][] getBoard() {
        return board;
    }
}
