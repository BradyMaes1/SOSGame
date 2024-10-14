package com.beginsecure.maventest.sosgamesprint2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The JavaFX GUI for the SOS game, allowing for board size selection,
 * game mode selection, move placement, and turn tracking.
 */
public class SOSGameUI extends Application {

    private SOSGame game;
    private GridPane grid; // The game board grid
    private Label turnLabel; // Label to indicate the current player's turn
    private ComboBox<Integer> boardSizeComboBox;
    private RadioButton simpleGameButton;
    private RadioButton generalGameButton;
    private ToggleGroup gameModeGroup;
    private Label gameModeLabel; // Class-level game mode display label
    private RadioButton sButton; // Radio button for selecting "S"
    private RadioButton oButton; // Radio button for selecting "O"
    private ToggleGroup moveGroup; // Toggle group for "S" and "O" selection

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20); // Increase spacing to 20 for a cleaner layout
        root.setPadding(new Insets(20)); // Add padding around the edges
        root.setAlignment(Pos.TOP_CENTER); // Align at the top and center horizontally

        // Title Label
        Label titleLabel = new Label("Brady's SOS Game");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Board size selection
        Label boardSizeLabel = new Label("Select Board Size:");
        boardSizeComboBox = new ComboBox<>();
        for (int i = 3; i <= 9; i++) {
            boardSizeComboBox.getItems().add(i); // Add sizes 3 to 9
        }
        boardSizeComboBox.setValue(3); // Default to 3x3 board size

        // Game mode selection (Simple or General)
        Label modeLabel = new Label("Select Game Mode:");
        simpleGameButton = new RadioButton("Simple");
        generalGameButton = new RadioButton("General");
        gameModeGroup = new ToggleGroup();
        simpleGameButton.setToggleGroup(gameModeGroup);
        generalGameButton.setToggleGroup(gameModeGroup);
        simpleGameButton.setSelected(true); // Default to "Simple" game

        // Layout for game mode selection
        HBox gameModeSelection = new HBox(10, modeLabel, simpleGameButton, generalGameButton);
        gameModeSelection.setAlignment(Pos.CENTER); // Center the game mode selection layout

        // Button to start the game
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startNewGame(primaryStage)); // Pass primaryStage to adjust size

        // Turn indicator label
        turnLabel = new Label("Player 1's Turn");
        turnLabel.setStyle("-fx-text-fill: red;"); // Set Player 1's turn to red

        // Move selection for "S" or "O"
        Label moveSelectionLabel = new Label("Select Your Move:");
        sButton = new RadioButton("S");
        oButton = new RadioButton("O");
        moveGroup = new ToggleGroup();
        sButton.setToggleGroup(moveGroup); // Only one S or O radio button is allowed to be on at a time
        oButton.setToggleGroup(moveGroup);
        sButton.setSelected(true); // Default to selecting "S"

        // Layout for move selection
        HBox moveSelectionLayout = new HBox(10, moveSelectionLabel, sButton, oButton);
        moveSelectionLayout.setAlignment(Pos.CENTER); // Center the move selection layout

        // Game Mode display label
        gameModeLabel = new Label("Game Mode: Simple");

        // Layout for board size selection, move selection, and game mode
        HBox sizeSelectionLayout = new HBox(10, boardSizeLabel, boardSizeComboBox, startGameButton);
        sizeSelectionLayout.setAlignment(Pos.CENTER); // Center the board size selection layout

        root.getChildren().addAll(titleLabel, sizeSelectionLayout, gameModeSelection, moveSelectionLayout, turnLabel, gameModeLabel);

        Scene scene = new Scene(root, 500, 400); // Set the window to a rectangular shape (500x400)
        primaryStage.setScene(scene);
        primaryStage.setTitle("SOS Game");
        primaryStage.show();
    }

    /**
     * Starts a new game with the selected board size and initializes the game board.
     */
    private void startNewGame(Stage stage) {
        int boardSize = boardSizeComboBox.getValue();
        game = new SOSGame(boardSize); // Create a new game with the selected board size

        if (grid != null) {
            grid.getChildren().clear(); // Clear the grid if already present (for new game)
        } else {
            grid = new GridPane();
            ((VBox) turnLabel.getParent()).getChildren().add(grid); // Add the grid to the layout
        }

        // Ensure grid is centered and add padding so it doesn't touch the window edges
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10)); // Add 10px padding around the grid

        // Set cell size
        double cellSize = 50; // Each button/cell is 50x50

        // Create buttons for each cell in the game board
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Button cellButton = new Button("");
                cellButton.setMinSize(cellSize, cellSize);
                final int r = row;
                final int c = col;
                // Set the action for each button to place S or O
                cellButton.setOnAction(e -> handleMove(r, c, cellButton));
                grid.add(cellButton, col, row); // Add button to the grid at position (row, col)
            }
        }

        // Adjust window size to fit larger boards
        double requiredWidth = Math.max(500, boardSize * cellSize + 100); // Add 100px for padding and controls
        double requiredHeight = Math.max(400, boardSize * cellSize + 350); // Add 300px for labels, controls, and padding

        // Set the stage's width and height to accommodate the new board size
        stage.setWidth(requiredWidth);
        stage.setHeight(requiredHeight);

        turnLabel.setText("Player 1's Turn"); // Reset turn indicator
        turnLabel.setStyle("-fx-text-fill: red;"); // Set Player 1's turn to red
        // Display the selected game mode
        String gameMode = simpleGameButton.isSelected() ? "Simple" : "General";
        gameModeLabel.setText("Game Mode: " + gameMode);
    }

    /**
     * Handles the player's move on the selected cell, placing either S or O.
     *
     * @param row The row index of the selected cell.
     * @param col The column index of the selected cell.
     * @param button The button corresponding to the selected cell.
     */
    private void handleMove(int row, int col, Button button) {
        if (!button.getText().isEmpty()) {
            return; // Prevent placing a move on an already occupied cell
        }

        // Determine the player's chosen move (S or O) based on the selected radio button
        char move = sButton.isSelected() ? 'S' : 'O';

        // Color the mark based on the player's turn
        if (game.isPlayerOneTurn()) {
            button.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Player 1's mark in red
        } else {
            button.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;"); // Player 2's mark in blue
        }

        boolean moveSuccess = game.placeMove(row, col, move);

        if (moveSuccess) {
            button.setText(String.valueOf(move)); // Update button with 'S' or 'O'

            // Update the turn indicator
            if (game.isPlayerOneTurn()) {
                turnLabel.setText("Player 1's Turn (Place S)");
                turnLabel.setStyle("-fx-text-fill: red;"); // Player 1's turn in red
            } else {
                turnLabel.setText("Player 2's Turn (Place O)");
                turnLabel.setStyle("-fx-text-fill: blue;"); // Player 2's turn in blue
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
