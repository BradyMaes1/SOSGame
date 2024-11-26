package com.beginsecure.maventest.sosgamesprint2;
/**
 * Interface for handling game end and score update events in the SOS game.
 */
public interface GameEndListener {
    /**
     * Called when the game ends with a final message.
     *
     * @param message the message to display at the end of the game
     */
    void onGameEnd(String message);
    /**
     * Called to update the scores for both players during the game.
     *
     * @param playerOneScore the current score of Player 1
     * @param playerTwoScore the current score of Player 2
     */
    void onScoreUpdate(int playerOneScore, int playerTwoScore);
}