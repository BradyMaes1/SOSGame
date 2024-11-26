package com.beginsecure.maventest.sosgamesprint2;
/**
 * Represents a general version of the SOS game where players accumulate points
 * for each SOS they form. The game ends when the board is full, and the player
 * with the most points is declared the winner. In the event of a tie, the game is a draw.
 */
public class GeneralGame extends SOSGame {
    private int playerOneScore = 0;
    private int playerTwoScore = 0;
    private final GameEndListener gameEndListener;
    /**
     * Initializes a GeneralGame with a specified board size and a game end listener.
     *
     * @param boardSize the size of the board in a square dimension (e.g., 3 for a 3x3 board)
     * @param gameEndListener the listener to handle end-of-game notifications and score updates
     */
    public GeneralGame(int boardSize, GameEndListener gameEndListener) {
        super(boardSize);
        this.gameEndListener = gameEndListener;
    }
    /**
     * Places a move for the current player at the specified position, checks for SOS formations,
     * and awards points to the player based on the number of SOSs formed. Updates the score display
     * via the game end listener. Ends the game if the board is full and declares the winner based on points.
     *
     * @param row the row position for the move
     * @param col the column position for the move
     * @param character the character ('S' or 'O') to place
     * @return true if the move is successfully placed, false otherwise
     */
    @Override
    public boolean placeMove(int row, int col, char character) {
        boolean wasPlayerOneTurn = isPlayerOneTurn();  // Capture whose turn it is before the move
        boolean moveSuccess = super.placeMove(row, col, character);
        if (moveSuccess) {
            int sosCount = checkForSOSCount(row, col);  // Get the count of SOS formations
            if (sosCount > 0) {
                // Award points based on the captured turn
                if (wasPlayerOneTurn) {
                    playerOneScore += sosCount;
                } else {
                    playerTwoScore += sosCount;
                }
                System.out.println("Player " + (wasPlayerOneTurn ? "1" : "2") + " scores " + sosCount + " point(s)!");
                // Notify listener to update the scores in UI
                gameEndListener.onScoreUpdate(playerOneScore, playerTwoScore);
            }
        }
        if (isBoardFull()) {
            String endMessage;
            if (playerOneScore > playerTwoScore) {
                endMessage = "Player 1 wins with " + playerOneScore + " points!";
                winner = "Player 1";
            } else if (playerTwoScore > playerOneScore) {
                endMessage = "Player 2 wins with " + playerTwoScore + " points!";
                winner = "Player 2";
            } else {
                endMessage = "The game is a draw. Both players have " + playerOneScore + " points.";
                winner = "Draw";
            }
            gameEndListener.onGameEnd(endMessage);
        }
        return moveSuccess;
    }
    /**
     * Retrieves Player 1's score.
     *
     * @return the current score of Player 1
     */
    public int getPlayerOneScore() {
        return playerOneScore;
    }
    /**
     * Retrieves Player 2's score.
     *
     * @return the current score of Player 2
     */
    public int getPlayerTwoScore() {
        return playerTwoScore;
    }
}