package com.beginsecure.maventest.sosgamesprint2;

import java.util.HashSet;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Contains the core game logic for SOS. This class manages the board size, player turns, and the
 * placement of 'S' or 'O' by players.
 */
public class SOSGame {

    private GameEndListener gameEndListener;
    private List<String> moveHistory; // To record all moves made in the game
    private Set<String> detectedSOS;
    private char[][] board;  // The game board
    private int boardSize;   // Size of the board
    protected boolean isPlayerOneTurn = true;  // Track player turns
    protected String winner = "None";

    private boolean isPlayerOneComputer = false; // Player 1 is computer-controlled
    private boolean isPlayerTwoComputer = false;

    /**
     * Initializes the SOS game with a specified board size.
     *
     * @param boardSize the size of the board in a square dimension (e.g., 3 for a 3x3 board)
     */
    public SOSGame(int boardSize) {
        this.boardSize = boardSize;
        this.board = new char[boardSize][boardSize];
        this.detectedSOS = new HashSet<>();
        this.moveHistory = new ArrayList<>();
    }

    public void setPlayerOneComputer(boolean isComputer) {
        this.isPlayerOneComputer = isComputer;
    }

    public void setPlayerTwoComputer(boolean isComputer) {
        this.isPlayerTwoComputer = isComputer;
    }

    public boolean isPlayerOneComputer() {
        return isPlayerOneComputer;
    }

    public boolean isPlayerTwoComputer() {
        return isPlayerTwoComputer;
    }

    public String getWinner() {
        return winner;
    }

    /**
     * Loads the SOS game into a text file.
     *
     * @param filePath the name of the file the game is to be saved to
     */
    public void saveGameToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Save metadata
            writer.write("Board Size: " + boardSize);
            writer.newLine();
            writer.write("Game Mode: " + (this instanceof SimpleGame ? "Simple" : "General"));
            writer.newLine();

            // Save moves
            for (String move : moveHistory) { // Assume moveHistory stores moves as strings like "Player 1: S at (0, 0)"
                writer.write(move);
                writer.newLine();
            }

            writer.write("Winner: " + (winner != null ? winner : "Draw"));
            writer.newLine();

            System.out.println("Game saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }


    /**
     * Loads an SOS game from a text file.
     *
     * @param filePath the name of the file the game is already saved at
     */

    public void replayGameFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read metadata
            String boardSizeLine = reader.readLine();
            int boardSize = Integer.parseInt(boardSizeLine.split(": ")[1].trim());
            String gameModeLine = reader.readLine();
            String gameMode = gameModeLine.split(": ")[1].trim();

            // Initialize game with ConsoleGameEndListener
            GameEndListener consoleListener = new ConsoleGameEndListener();
            SOSGame game = gameMode.equals("Simple")
                    ? new SimpleGame(boardSize, consoleListener)
                    : new GeneralGame(boardSize, consoleListener);

            System.out.println("Replaying a " + gameMode + " game on a " + boardSize + "x" + boardSize + " board.");

            // Read and replay moves
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Winner:")) {
                    consoleListener.onGameEnd(line);
                    break;
                }

                // Parse and apply move
                String[] parts = line.split(": ");
                String player = parts[0];
                String[] moveDetails = parts[1].split(" at ");
                char mark = moveDetails[0].charAt(0);
                String[] coordinates = moveDetails[1].replace("(", "").replace(")", "").split(",");
                int row = Integer.parseInt(coordinates[0].trim());
                int col = Integer.parseInt(coordinates[1].trim());

                System.out.println(player + " plays " + mark + " at (" + row + ", " + col + ")");
                game.placeMove(row, col, mark);

                // Optional: Add a delay for better visualization
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error replaying game: " + e.getMessage());
        }
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
        //System.out.println("Attempting to place " + character + " at (" + row + ", " + col + ")");
        if (isValidMove(row, col) && (character == 'S' || character == 'O')) {
            board[row][col] = character;

            // Record the move
            String player = isPlayerOneTurn ? "Player 1" : "Player 2";
            moveHistory.add(player + ": " + character + " at (" + row + ", " + col + ")");
            //System.out.println(player + " successfully placed " + character + " at (" + row + ", " + col + ")");

            // Turn switching is now handled explicitly outside
            return true;
        }
        System.out.println("Move invalid at (" + row + ", " + col + ")");
        return false;
    }


    /**
     * Counts the number of new SOS patterns formed by the recent move.
     *
     * @param row the row position of the placed character
     * @param col the column position of the placed character
     * @return the count of new SOS formations detected
     */


    public boolean makeRandomMove() {
        Random random = new Random();
        char character = random.nextBoolean() ? 'S' : 'O'; // Randomly choose S or O

        // Find a random unoccupied position on the board
        for (int attempts = 0; attempts < boardSize * boardSize; attempts++) {
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            if (isValidMove(row, col)) {
                placeMove(row, col, character);
                System.out.println("Computer placed " + character + " at (" + row + ", " + col + ")");
                return true;
            }
        }

        // If no move was made (e.g., board is full), return false
        return false;
    }

    protected int checkForSOSCount(int row, int col) {
        int sosCount = 0;
        char currentChar = board[row][col];

        // Horizontal checks
        if (currentChar == 'S' || currentChar == 'O') {
            // Check for SOS with the current character as the starting or ending 'S'
            if (currentChar == 'S') {
                if (col >= 2 && board[row][col - 2] == 'S' && board[row][col - 1] == 'O') {
                    if (detectedSOS.add(sosKey(row, col - 2, row, col - 1, row, col))) sosCount++;
                }
                if (col >= 1 && col < boardSize - 1 && board[row][col - 1] == 'O' && board[row][col + 1] == 'S') {
                    if (detectedSOS.add(sosKey(row, col - 1, row, col, row, col + 1))) sosCount++;
                }
                if (col < boardSize - 2 && board[row][col + 1] == 'O' && board[row][col + 2] == 'S') {
                    if (detectedSOS.add(sosKey(row, col, row, col + 1, row, col + 2))) sosCount++;
                }
            }

            // Check for SOS with the current character as the middle 'O'
            if (currentChar == 'O') {
                if (col >= 1 && col < boardSize - 1 && board[row][col - 1] == 'S' && board[row][col + 1] == 'S') {
                    if (detectedSOS.add(sosKey(row, col - 1, row, col, row, col + 1))) sosCount++;
                }
            }
        }

        // Vertical checks
        if (currentChar == 'S' || currentChar == 'O') {
            // Check for SOS with the current character as the starting or ending 'S'
            if (currentChar == 'S') {
                if (row >= 2 && board[row - 2][col] == 'S' && board[row - 1][col] == 'O') {
                    if (detectedSOS.add(sosKey(row - 2, col, row - 1, col, row, col))) sosCount++;
                }
                if (row >= 1 && row < boardSize - 1 && board[row - 1][col] == 'O' && board[row + 1][col] == 'S') {
                    if (detectedSOS.add(sosKey(row - 1, col, row, col, row + 1, col))) sosCount++;
                }
                if (row < boardSize - 2 && board[row + 1][col] == 'O' && board[row + 2][col] == 'S') {
                    if (detectedSOS.add(sosKey(row, col, row + 1, col, row + 2, col))) sosCount++;
                }
            }

            // Check for SOS with the current character as the middle 'O'
            if (currentChar == 'O') {
                if (row >= 1 && row < boardSize - 1 && board[row - 1][col] == 'S' && board[row + 1][col] == 'S') {
                    if (detectedSOS.add(sosKey(row - 1, col, row, col, row + 1, col))) sosCount++;
                }
            }
        }

        // Diagonal checks (top-left to bottom-right)
        if (currentChar == 'S' || currentChar == 'O') {
            if (currentChar == 'S') {
                if (row >= 2 && col >= 2 && board[row - 2][col - 2] == 'S' && board[row - 1][col - 1] == 'O') {
                    if (detectedSOS.add(sosKey(row - 2, col - 2, row - 1, col - 1, row, col))) sosCount++;
                }
                if (row >= 1 && row < boardSize - 1 && col >= 1 && col < boardSize - 1 &&
                        board[row - 1][col - 1] == 'O' && board[row + 1][col + 1] == 'S') {
                    if (detectedSOS.add(sosKey(row - 1, col - 1, row, col, row + 1, col + 1))) sosCount++;
                }
                if (row < boardSize - 2 && col < boardSize - 2 && board[row + 1][col + 1] == 'O' &&
                        board[row + 2][col + 2] == 'S') {
                    if (detectedSOS.add(sosKey(row, col, row + 1, col + 1, row + 2, col + 2))) sosCount++;
                }
            }

            if (currentChar == 'O') {
                if (row >= 1 && row < boardSize - 1 && col >= 1 && col < boardSize - 1 &&
                        board[row - 1][col - 1] == 'S' && board[row + 1][col + 1] == 'S') {
                    if (detectedSOS.add(sosKey(row - 1, col - 1, row, col, row + 1, col + 1))) sosCount++;
                }
            }
        }

        // Reverse-diagonal checks (top-right to bottom-left)
        if (currentChar == 'S' || currentChar == 'O') {
            if (currentChar == 'S') {
                if (row >= 2 && col < boardSize - 2 && board[row - 2][col + 2] == 'S' &&
                        board[row - 1][col + 1] == 'O') {
                    if (detectedSOS.add(sosKey(row - 2, col + 2, row - 1, col + 1, row, col))) sosCount++;
                }
                if (row >= 1 && row < boardSize - 1 && col < boardSize - 1 && col >= 1 &&
                        board[row - 1][col + 1] == 'O' && board[row + 1][col - 1] == 'S') {
                    if (detectedSOS.add(sosKey(row - 1, col + 1, row, col, row + 1, col - 1))) sosCount++;
                }
                if (row < boardSize - 2 && col >= 2 && board[row + 1][col - 1] == 'O' &&
                        board[row + 2][col - 2] == 'S') {
                    if (detectedSOS.add(sosKey(row, col, row + 1, col - 1, row + 2, col - 2))) sosCount++;
                }
            }

            if (currentChar == 'O') {
                if (row >= 1 && row < boardSize - 1 && col < boardSize - 1 && col >= 1 &&
                        board[row - 1][col + 1] == 'S' && board[row + 1][col - 1] == 'S') {
                    if (detectedSOS.add(sosKey(row - 1, col + 1, row, col, row + 1, col - 1))) sosCount++;
                }
            }
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
        //System.out.println("Turn switched. It is now " + (isPlayerOneTurn ? "Player 1's turn" : "Player 2's turn"));
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