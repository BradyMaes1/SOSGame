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

        root.getChildren().addAll(titleLabel, sizeSelectionLayout, gameModeSelection, moveSelectionLayout, turnLabel, gameModeLabel, scoreDisplayLayout);

        Scene scene = new Scene(root, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SOS Game");
        primaryStage.show();
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
     * @param row the row where the move is placed
     * @param col the column where the move is placed
     * @param button the button representing the board cell
     */
    private void handleMove(int row, int col, Button button) {
        if (gameEnded || !button.getText().isEmpty()) {
            return;
        }

        char move = sButton.isSelected() ? 'S' : 'O';

        if (game.isPlayerOneTurn()) {
            button.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            button.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        }

        boolean moveSuccess = game.placeMove(row, col, move);

        if (moveSuccess) {
            button.setText(String.valueOf(move));

            if (!gameEnded && game.isBoardFull()) {
                onGameEnd("The game is a draw. No SOS formed.");
            } else if (!gameEnded) {
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
