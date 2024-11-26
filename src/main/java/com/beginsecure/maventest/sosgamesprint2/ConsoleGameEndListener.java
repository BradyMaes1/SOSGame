package com.beginsecure.maventest.sosgamesprint2;

/**
 * A GameEndListener implementation that prints game-ending events to the console.
 */
public class ConsoleGameEndListener implements GameEndListener {

    @Override
    public void onGameEnd(String message) {
        System.out.println(message);
    }

    @Override
    public void onScoreUpdate(int playerOneScore, int playerTwoScore) {
        System.out.println("Score Update - Player 1: " + playerOneScore + ", Player 2: " + playerTwoScore);
    }
}
