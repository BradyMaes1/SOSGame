package com.beginsecure.maventest.sosgamesprint2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * The JavaFX GUI for the SOS game, allowing for board size selection,
 * game mode selection, move placement, turn tracking, saving games, and replaying games.
 */
public class SOSGameUI extends Application implements GameEndListener {

    private SOSGame game;
    private GridPane grid;
    private Label turnLabel;
    private ComboBox<Integer> boardSizeComboBox;
    private RadioButton simpleGameButton;
    private RadioButton generalGameButton;
    private ToggleGroup gameModeGroup;
    private Label gameModeLabel;
    private RadioButton sButton;
    private RadioButton oButton;
    private ToggleGroup moveGroup;
    private boolean gameEnded = false;
    private Label playerOneScoreLabel;
    private Label playerTwoScoreLabel;
    private Button saveGameButton;
    private Button replayGameButton;
    private CheckBox playerOneComputerCheckbox;
    private CheckBox playerTwoComputerCheckbox;

    /**
     * Sets up and displays the main GUI window.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Brady's SOS Game");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label boardSizeLabel = new Label("Select Board Size:");
        boardSizeComboBox = new ComboBox<>();
        for (int i = 3; i <= 9; i++) {
            boardSizeComboBox.getItems().add(i);
        }
        boardSizeComboBox.setValue(3);

        Label modeLabel = new Label("Select Game Mode:");
        simpleGameButton = new RadioButton("Simple");
        generalGameButton = new RadioButton("General");
        gameModeGroup = new ToggleGroup();
        simpleGameButton.setToggleGroup(gameModeGroup);
        generalGameButton.setToggleGroup(gameModeGroup);
        simpleGameButton.setSelected(true);

        HBox gameModeSelection = new HBox(10, modeLabel, simpleGameButton, generalGameButton);
        gameModeSelection.setAlignment(Pos.CENTER);

        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> startNewGame(primaryStage));

        // Save and Replay Buttons
        saveGameButton = new Button("Save Game");
        saveGameButton.setOnAction(e -> saveGameToFile(primaryStage));

        replayGameButton = new Button("Replay Game");
        replayGameButton.setOnAction(e -> replayGameFromFile(primaryStage));

        HBox fileButtonsLayout = new HBox(10, saveGameButton, replayGameButton);
        fileButtonsLayout.setAlignment(Pos.CENTER);

        turnLabel = new Label("Player 1's Turn");
        turnLabel.setStyle("-fx-text-fill: red;");

        Label moveSelectionLabel = new Label("Select Your Move:");
        sButton = new RadioButton("S");
        oButton = new RadioButton("O");
        moveGroup = new ToggleGroup();
        sButton.setToggleGroup(moveGroup);
        oButton.setToggleGroup(moveGroup);
        sButton.setSelected(true);

        HBox moveSelectionLayout = new HBox(10, moveSelectionLabel, sButton, oButton);
        moveSelectionLayout.setAlignment(Pos.CENTER);

        gameModeLabel = new Label("Game Mode: Simple");

        playerOneScoreLabel = new Label("Player 1: 0");
        playerOneScoreLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        playerTwoScoreLabel = new Label("Player 2: 0");
        playerTwoScoreLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 16px;");
        HBox scoreDisplayLayout = new HBox(20, playerOneScoreLabel, playerTwoScoreLabel);
        scoreDisplayLayout.setAlignment(Pos.CENTER);

        HBox sizeSelectionLayout = new HBox(10, boardSizeLabel, boardSizeComboBox, startGameButton);
        sizeSelectionLayout.setAlignment(Pos.CENTER);

        // Initialize Computer Player Options
        playerOneComputerCheckbox = new CheckBox("Player 1: Computer");
        playerTwoComputerCheckbox = new CheckBox("Player 2: Computer");
        HBox computerPlayerOptions = new HBox(10, playerOneComputerCheckbox, playerTwoComputerCheckbox);
        computerPlayerOptions.setAlignment(Pos.CENTER);

        // Add all components to the root layout
        root.getChildren().addAll(
                titleLabel,
                sizeSelectionLayout,
                gameModeSelection,
                computerPlayerOptions,
                fileButtonsLayout,
                moveSelectionLayout,
                turnLabel,
                gameModeLabel,
                scoreDisplayLayout
        );

        // Set the scene and display the stage
        Scene scene = new Scene(root, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SOS Game");
        primaryStage.show();
    }


    /**
     * Saves the current game state to a file.
     *
     * @param stage the primary stage for displaying the file chooser
     */
    private void saveGameToFile(Stage stage) {
        if (game == null) {
            displayError("No game in progress to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            game.saveGameToFile(file.getAbsolutePath());
        }
    }

    /**
     * Replays a game from a saved file.
     *
     * @param stage the primary stage for displaying the file chooser
     */
    private void replayGameFromFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Replay Game");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            SOSGame replayGame = new SimpleGame(3, null); // Placeholder size; adjust based on game mode
            replayGame.replayGameFromFile(file.getAbsolutePath());
        }
    }

    /**
     * Displays an error message in an alert dialog.
     *
     * @param message the error message to display
     */
    private void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays the winner message when the game ends.
     *
     * @param message the game-ending message to be displayed
     */
    @Override
    public void onGameEnd(String message) {
        displayWinner(message);
    }

    /**
     * Updates the score display for each player.
     *
     * @param playerOneScore the current score of Player 1
     * @param playerTwoScore the current score of Player 2
     */
    @Override
    public void onScoreUpdate(int playerOneScore, int playerTwoScore) {
        playerOneScoreLabel.setText("Player 1: " + playerOneScore);
        playerTwoScoreLabel.setText("Player 2: " + playerTwoScore);
    }

    /**
     * Displays the final message and disables the board buttons when the game ends.
     *
     * @param message the final game message (e.g., winner or draw message)
     */
    private void displayWinner(String message) {
        turnLabel.setText(message);
        turnLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        for (int row = 0; row < game.getBoard().length; row++) {
            for (int col = 0; col < game.getBoard()[row].length; col++) {
                Button cellButton = (Button) grid.getChildren().get(row * game.getBoard().length + col);
                cellButton.setDisable(true);
            }
        }
        gameEnded = true;
    }

    /**
     * Initializes a new game based on the selected board size and game mode.
     *
     * @param stage the stage for displaying the board
     */
    private void startNewGame(Stage stage) {
        int boardSize = boardSizeComboBox.getValue();
        if (simpleGameButton.isSelected()) {
            game = new SimpleGame(boardSize, this);
            gameModeLabel.setText("Game Mode: Simple");
            playerOneScoreLabel.setVisible(false);
            playerTwoScoreLabel.setVisible(false);
        } else {
            game = new GeneralGame(boardSize, this);
            gameModeLabel.setText("Game Mode: General");
            playerOneScoreLabel.setVisible(true);
            playerTwoScoreLabel.setVisible(true);
            onScoreUpdate(0, 0); // Initialize score display at 0
        }
        gameEnded = false;

        game.setPlayerOneComputer(playerOneComputerCheckbox.isSelected());
        game.setPlayerTwoComputer(playerTwoComputerCheckbox.isSelected());

        System.out.println("Player 1 is a computer: " + game.isPlayerOneComputer());
        System.out.println("Player 2 is a computer: " + game.isPlayerTwoComputer());


        if (grid != null) {
            grid.getChildren().clear();
        } else {
            grid = new GridPane();
            ((VBox) turnLabel.getParent()).getChildren().add(grid);
        }

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        double cellSize = 50;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Button cellButton = new Button("");
                cellButton.setMinSize(cellSize, cellSize);
                final int r = row;
                final int c = col;
                cellButton.setOnAction(e -> handleMove(r, c, cellButton));
                grid.add(cellButton, col, row);
            }
        }

        double requiredWidth = Math.max(500, boardSize * cellSize + 100);
        double requiredHeight = Math.max(450, boardSize * cellSize + 450);

        stage.setWidth(requiredWidth);
        stage.setHeight(requiredHeight);

        turnLabel.setText("Player 1's Turn");
        turnLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Handles the placement of a move on the board and updates the turn label.
     *
     * @param row    the row where the move is placed
     * @param col    the column where the move is placed
     * @param button the button representing the board cell
     */

    private boolean makeComputerMove() {
        char[][] board = game.getBoard();
        int boardSize = board.length;

        // Let the game make a random move
        if (game.makeRandomMove()) {
            // Find the cell updated by the computer's move and reflect it in the UI
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (grid.getChildren().get(row * boardSize + col) instanceof Button cellButton) {
                        if (cellButton.getText().isEmpty() && board[row][col] != '\0') {
                            cellButton.setText(String.valueOf(board[row][col]));
                            //System.out.println("DEBUG: Before styling, game.isPlayerOneTurn() = " + game.isPlayerOneTurn());

                            // Style the cell for the current player
                            if (game.isPlayerOneTurn()) {
                                cellButton.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            } else {
                                cellButton.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            }

                            // Now switch the turn
                            game.switchTurn();
                            //System.out.println("DEBUG: After switchTurn, isPlayerOneTurn = " + game.isPlayerOneTurn());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    private void handleMove(int row, int col, Button button) {
        if (gameEnded || (button != null && !button.getText().isEmpty())) {
            return; // Prevent invalid or redundant moves
        }

        // Check if it's a computer's turn (this will bypass human processing)
        if (game.isPlayerOneTurn() && game.isPlayerOneComputer()) {
            //System.out.println("Player 1's turn (Computer):");
            if (makeComputerMove()) {
                //System.out.println("Player 1 (Computer) made a move.");
            } else {
                displayError("No valid moves for Player 1!");
            }
            return;
        } else if (!game.isPlayerOneTurn() && game.isPlayerTwoComputer()) {
            //System.out.println("Player 2's turn (Computer):");
            if (makeComputerMove()) {
                //System.out.println("Player 2 (Computer) made a move.");
            } else {
                displayError("No valid moves for Player 2!");
            }
            return;
        }

        // Process the human player's move
        char move = sButton.isSelected() ? 'S' : 'O';

        if (game.isPlayerOneTurn()) {
            //System.out.println("Player 1's turn (Human):");
            //System.out.println("Player 1 placed " + move + " at (" + row + ", " + col + ")");
            button.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            //System.out.println("Player 2's turn (Human):");
            //System.out.println("Player 2 placed " + move + " at (" + row + ", " + col + ")");
            button.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        }

        boolean moveSuccess = game.placeMove(row, col, move); // Note: `placeMove()` no longer switches turns

        if (moveSuccess) {
            button.setText(String.valueOf(move));
            if (!gameEnded && game.isBoardFull()) {
                onGameEnd("The game is a draw. No SOS formed.");
            } else if (!gameEnded) {
                // Switch the turn here explicitly after the move
                game.switchTurn();
                if (game.isPlayerOneTurn()) {
                    turnLabel.setText("Player 1's Turn (Place S)");
                    turnLabel.setStyle("-fx-text-fill: red;");
                } else {
                    turnLabel.setText("Player 2's Turn (Place O)");
                    turnLabel.setStyle("-fx-text-fill: blue;");
                }
            }
        }
    }





    /**
     * Main entry point to launch the JavaFX application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
