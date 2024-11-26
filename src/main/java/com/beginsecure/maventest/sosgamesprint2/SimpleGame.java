package com.beginsecure.maventest.sosgamesprint2;

/**
 * Represents a simple version of the SOS game where the first player to form an SOS wins.
 * If the board is filled with no SOS formed, the game ends in a draw.
 */
public class SimpleGame extends SOSGame {

    private final GameEndListener gameEndListener;

    /**
     * Initializes a SimpleGame with a specified board size and a game end listener.
     *
     * @param boardSize the size of the board in a square dimension (e.g., 3 for a 3x3 board)
     * @param gameEndListener the listener to handle end-of-game notifications
     */
    public SimpleGame(int boardSize, GameEndListener gameEndListener) {
        super(boardSize);
        this.gameEndListener = gameEndListener;
    }

    /**
     * Places a move for the current player at the specified position and checks for SOS formation.
     * If an SOS is formed, the game ends with the current player declared the winner. If the board
     * is filled with no SOS formed, the game ends in a draw.
     *
     * @param row the row position for the move
     * @param col the column position for the move
     * @param character the character ('S' or 'O') to place
     * @return true if the move is successfully placed, false otherwise
     */
    @Override
    public boolean placeMove(int row, int col, char character) {
        boolean moveSuccess = super.placeMove(row, col, character);

        if (moveSuccess) {
            int sosCount = checkForSOSCount(row, col);

            if (sosCount > 0) {
                String winnerMessage = "Player " + (isPlayerOneTurn() ? "2" : "1") + " wins by forming an SOS!";
                gameEndListener.onGameEnd(winnerMessage);
            } else if (isBoardFull()) {
                gameEndListener.onGameEnd("The game is a draw. No SOS formed.");
            }
        }

        return moveSuccess;
    }
}
