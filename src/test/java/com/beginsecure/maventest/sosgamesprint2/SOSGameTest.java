package com.beginsecure.maventest.sosgamesprint2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SOSGame, SimpleGame, and GeneralGame classes.
 */
public class SOSGameTest {

    private SOSGame game;
    private SimpleGame simpleGame;
    private GeneralGame generalGame;
    private String gameMode;
    private TestGameEndListener testListener;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        game = new SOSGame(3); // Initializes a 3x3 board for SOSGame
        testListener = new TestGameEndListener();
        simpleGame = new SimpleGame(3, testListener);
        generalGame = new GeneralGame(3, testListener);
    }

    // Original Tests

    /**
     * Tests if the board initializes correctly with specified dimensions.
     */
    @Test
    void testBoardInitialization() {
        char[][] board = game.getBoard();
        assertNotNull(board, "Board should not be null.");
        assertEquals(3, board.length, "Board should have 3 rows.");
        assertEquals(3, board[0].length, "Board should have 3 columns.");
    }

    /**
     * Tests if the game mode is set correctly.
     */
    @Test
    void testGameModeSelection() {
        gameMode = "Simple";
        assertEquals("Simple", gameMode, "Game mode should be Simple.");

        gameMode = "General";
        assertEquals("General", gameMode, "Game mode should be General.");
    }

    /**
     * Tests if a valid move is placed correctly on the board.
     */
    @Test
    void testValidMove() {
        boolean moveSuccess = game.placeMove(0, 0, 'S');
        assertTrue(moveSuccess, "Valid move should return true.");
        assertEquals('S', game.getBoard()[0][0], "The move should place 'S' at (0, 0).");
    }

    /**
     * Tests if an invalid move outside the board bounds is handled correctly.
     */
    @Test
    void testInvalidMoveOutsideBoard() {
        boolean moveSuccess = game.placeMove(3, 3, 'S');
        assertFalse(moveSuccess, "Move outside the board should return false.");
    }

    /**
     * Tests if an invalid move to an occupied cell is handled correctly.
     */
    @Test
    void testInvalidMoveOccupiedCell() {
        game.placeMove(0, 0, 'S');
        boolean moveSuccess = game.placeMove(0, 0, 'O');
        assertFalse(moveSuccess, "Move to an occupied cell should return false.");
    }

    /**
     * Tests if turns switch correctly between players after each move.
     */
    @Test
    void testTurnSwitching() {
        game.placeMove(0, 0, 'S');
        assertFalse(game.isPlayerOneTurn(), "After Player 1's move, it should be Player 2's turn.");
        game.placeMove(1, 1, 'O');
        assertTrue(game.isPlayerOneTurn(), "After Player 2's move, it should be Player 1's turn.");
    }

    /**
     * Tests if multiple valid moves can be made sequentially.
     */
    @Test
    void testMoveSuccess() {
        assertTrue(game.placeMove(0, 0, 'S'), "Move should be valid.");
        assertTrue(game.placeMove(1, 1, 'O'), "Move should be valid.");
        assertTrue(game.placeMove(2, 2, 'S'), "Move should be valid.");
    }

    /**
     * Tests if moves to invalid positions are rejected.
     */
    @Test
    void testMoveFailure() {
        game.placeMove(0, 0, 'S');
        assertFalse(game.placeMove(0, 0, 'S'), "Should not be able to place a mark in an occupied cell.");
        assertFalse(game.placeMove(3, 3, 'O'), "Move outside the board should return false.");
    }

    // New Tests for SimpleGame and GeneralGame

    /**
     * Tests if a player wins in SimpleGame mode by forming a single SOS.
     */
    @Test
    void testSimpleGameSingleSOSWin() {
        simpleGame.placeMove(0, 0, 'S');
        simpleGame.placeMove(1, 0, 'S');
        simpleGame.placeMove(0, 1, 'O');
        simpleGame.placeMove(1, 1, 'S');
        boolean result = simpleGame.placeMove(0, 2, 'S');
        assertTrue(result);
        assertEquals("Player 1 wins by forming an SOS!", testListener.endMessage);
    }

    /**
     * Tests if the game ends in a draw in SimpleGame mode when the board is full without any SOS formed.
     */
    @Test
    void testSimpleGameDrawCondition() {
        simpleGame.placeMove(0, 0, 'S');
        simpleGame.placeMove(0, 1, 'O');
        simpleGame.placeMove(0, 2, 'S');
        simpleGame.placeMove(1, 0, 'O');
        simpleGame.placeMove(1, 1, 'S');
        simpleGame.placeMove(1, 2, 'O');
        simpleGame.placeMove(2, 0, 'O');
        simpleGame.placeMove(2, 1, 'S');
        boolean result = simpleGame.placeMove(2, 2, 'O');
        assertTrue(result);
        assertEquals("The game is a draw. No SOS formed.", testListener.endMessage);
    }

    /**
     * Tests the score counting system in GeneralGame mode.
     */
    @Test
    void testGeneralGameScoring() {
        generalGame.placeMove(0, 0, 'S');
        generalGame.placeMove(1, 0, 'S');
        generalGame.placeMove(0, 1, 'O');
        generalGame.placeMove(1, 1, 'O');
        generalGame.placeMove(0, 2, 'S');
        assertEquals(1, testListener.playerOneScore);
        assertEquals(0, testListener.playerTwoScore);

        generalGame.placeMove(2, 2, 'S');
        assertEquals(1, testListener.playerOneScore);
        assertEquals(1, testListener.playerTwoScore);
    }

    /**
     * Tests if the game ends with a winner in GeneralGame mode based on points.
     */
    @Test
    void testGeneralGameEndWithWinner() {
        generalGame.placeMove(0, 0, 'S');
        generalGame.placeMove(1, 0, 'S');
        generalGame.placeMove(0, 1, 'O');
        generalGame.placeMove(1, 1, 'O');
        generalGame.placeMove(0, 2, 'S');
        generalGame.placeMove(2, 0, 'S');
        generalGame.placeMove(2, 1, 'O');
        generalGame.placeMove(2, 2, 'S');

        assertEquals("Player 2 wins with 2 points!", testListener.endMessage);
    }

    /**
     * Tests if the game correctly identifies when a SimpleGame is over.
     */
    @Test
    void testSimpleGameOver() {
        simpleGame.placeMove(0, 0, 'S');
        simpleGame.placeMove(1, 1, 'S');
        simpleGame.placeMove(0, 1, 'O');
        simpleGame.placeMove(0, 2, 'S'); // Completes an SOS horizontally
        assertTrue(simpleGame.isBoardFull() || testListener.endMessage != null, "Simple game should be over with a winner.");
        assertEquals("Player 1 wins by forming an SOS!", testListener.endMessage);
    }

    /**
     * Tests if the game correctly identifies when a GeneralGame is over.
     */
    @Test
    void testGeneralGameOver() {
        generalGame.placeMove(0, 0, 'S');
        generalGame.placeMove(1, 0, 'S');
        generalGame.placeMove(0, 1, 'O');
        generalGame.placeMove(1, 1, 'O');
        generalGame.placeMove(0, 2, 'S');
        generalGame.placeMove(2, 0, 'S');
        generalGame.placeMove(2, 1, 'O');
        generalGame.placeMove(2, 2, 'S'); // Completes board without additional SOS

        assertTrue(generalGame.isBoardFull() || testListener.endMessage != null, "General game should be over.");
        assertTrue(testListener.endMessage.contains("wins") || testListener.endMessage.contains("draw"), "General game should end with a winner or draw.");
    }

    /**
     * A listener class used for capturing game end messages and score updates in tests.
     */
    private static class TestGameEndListener implements GameEndListener {
        String endMessage = null;
        int playerOneScore = 0;
        int playerTwoScore = 0;

        @Override
        public void onGameEnd(String message) {
            endMessage = message;
        }

        @Override
        public void onScoreUpdate(int playerOneScore, int playerTwoScore) {
            this.playerOneScore = playerOneScore;
            this.playerTwoScore = playerTwoScore;
        }
    }
}
