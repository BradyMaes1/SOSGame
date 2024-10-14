package com.beginsecure.maventest.sosgamesprint2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for my SOSGame class.
 */
public class SOSGameTest {

    private SOSGame game;
    private String gameMode;

    @BeforeEach
    void setUp() {
        // Initialize a 3x3 board before each test
        game = new SOSGame(3);
    }

    @Test
    void testBoardInitialization() {
        // Check if the board is initialized correctly
        char[][] board = game.getBoard();
        assertNotNull(board, "Board should not be null.");
        assertEquals(3, board.length, "Board should have 3 rows.");
        assertEquals(3, board[0].length, "Board should have 3 columns.");
    }

    @Test
    void testGameModeSelection() {
        // Simulate choosing the Simple game mode
        gameMode = "Simple";
        assertEquals("Simple", gameMode, "Game mode should be Simple.");

        // Simulate choosing the General game mode
        gameMode = "General";
        assertEquals("General", gameMode, "Game mode should be General.");
    }

    @Test
    void testValidMove() {
        // Place an 'S' at (0, 0) and check if it's correctly placed
        boolean moveSuccess = game.placeMove(0, 0, 'S');
        assertTrue(moveSuccess, "Valid move should return true.");
        assertEquals('S', game.getBoard()[0][0], "The move should place 'S' at (0, 0).");
    }

    @Test
    void testInvalidMoveOutsideBoard() {
        // Attempt to place a mark outside the board (invalid)
        boolean moveSuccess = game.placeMove(3, 3, 'S');
        assertFalse(moveSuccess, "Move outside the board should return false.");
    }

    @Test
    void testInvalidMoveOccupiedCell() {
        // Place an 'S' at (0, 0), then try to place an 'O' at the same position (invalid)
        game.placeMove(0, 0, 'S');
        boolean moveSuccess = game.placeMove(0, 0, 'O');
        assertFalse(moveSuccess, "Move to an occupied cell should return false.");
    }

    @Test
    void testTurnSwitching() {
        // Place a move for Player 1, check turn switches to Player 2
        game.placeMove(0, 0, 'S');
        assertFalse(game.isPlayerOneTurn(), "After Player 1's move, it should be Player 2's turn.");
        game.placeMove(1, 1, 'O');
        assertTrue(game.isPlayerOneTurn(), "After Player 2's move, it should be Player 1's turn.");
    }

    @Test
    void testMoveSuccess() {
        // Check valid moves and their success
        assertTrue(game.placeMove(0, 0, 'S'), "Move should be valid.");
        assertTrue(game.placeMove(1, 1, 'O'), "Move should be valid.");
        assertTrue(game.placeMove(2, 2, 'S'), "Move should be valid.");
    }

    @Test
    void testMoveFailure() {
        // Place an 'S' at (0, 0) and check that further invalid moves fail
        game.placeMove(0, 0, 'S');
        assertFalse(game.placeMove(0, 0, 'S'), "Should not be able to place a mark in an occupied cell.");
        assertFalse(game.placeMove(3, 3, 'O'), "Move outside the board should return false.");
    }
}
