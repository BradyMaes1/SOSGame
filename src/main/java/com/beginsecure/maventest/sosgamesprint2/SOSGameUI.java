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
 * game mode selection, player type selection, move placement, and turn tracking.
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

    // New elements for player type
    private CheckBox playerOneComputerCheckBox;
    private CheckBox playerTwoComputerCheckBox;

    // Commented out computer difficulty selection
    // private ComboBox<String> computerDifficultyComboBox;

    // Integration of LLMService
    private LLMService llmService = new LLMService();

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

        // Player type selection checkboxes
        playerOneComputerCheckBox = new CheckBox("Player 1: Computer");
        playerTwoComputerCheckBox = new CheckBox("Player 2: Computer");

        // Commented out computer difficulty selection
        // Label difficultyLabel = new Label("Computer Difficulty:");
        // computerDifficultyComboBox = new ComboBox<>();
        // computerDifficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        // computerDifficultyComboBox.setValue("Medium");

        HBox playerTypeSelection = new HBox(20, playerOneComputerCheckBox, playerTwoComputerCheckBox /*, difficultyLabel, computerDifficultyComboBox */);
        playerTypeSelection.setAlignment(Pos.CENTER);

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

        root.getChildren().addAll(titleLabel, sizeSelectionLayout, gameModeSelection, playerTypeSelection, moveSelectionLayout, turnLabel, gameModeLabel, scoreDisplayLayout);

        Scene scene = new Scene(root, 500, 500);
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
        double requiredHeight = Math.max(500, boardSize * cellSize + 450);

        stage.setWidth(requiredWidth);
        stage.setHeight(requiredHeight);

        turnLabel.setText("Player 1's Turn");
        turnLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Handles the placement of a move on the board and updates the turn label.
     * If it's the computer's turn, communicates with the LLMService to get a move.
     *
     * @param row the row where the move is placed
     * @param col the column where the move is placed
     * @param button the button representing the board cell
     */
    private void handleMove(int row, int col, Button button) {
        if (gameEnded || !button.getText().isEmpty()) {
            return;
        }

        // Human player's move logic
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
                updateTurnLabel();

                // Check if it's the AI's turn and process the AI move
                if ((game.isPlayerOneTurn() && playerOneComputerCheckBox.isSelected()) ||
                        (!game.isPlayerOneTurn() && playerTwoComputerCheckBox.isSelected())) {
                    processAIMove(); // Handle the AI's move immediately
                }
            }
        }
    }

    private void processAIMove() {
        boolean validMove = false;
        int retryCount = 0;
        final int maxRetries = 5; // Limit retries to avoid infinite loops

        while (!validMove && retryCount < maxRetries) {
            String prompt = generatePromptForLLM();
            String llmResponse = llmService.getMoveFromLLM(prompt);

            // Parse the structured response (e.g., "row,col,character")
            String[] parts = llmResponse.split(",");
            if (parts.length == 3) {
                try {
                    int aiRow = Integer.parseInt(parts[0].trim());
                    int aiCol = Integer.parseInt(parts[1].trim());
                    char aiMove = parts[2].trim().charAt(0);

                    // Validate the move
                    if (isValidAIMove(aiRow, aiCol, aiMove)) {
                        placeComputerMove(aiRow, aiCol, aiMove);
                        validMove = true; // Move was successful
                    } else {
                        System.err.println("AI attempted an invalid move at row=" + aiRow + ", col=" + aiCol);
                        retryCount++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid response format from LLM: " + llmResponse);
                    retryCount++;
                }
            } else {
                System.err.println("Invalid response format from LLM: " + llmResponse);
                retryCount++;
            }
        }

        if (!validMove) {
            System.err.println("AI failed to make a valid move after " + maxRetries + " attempts.");
            // Optionally, handle this case (e.g., force a pass or generate a random valid move)
        }
    }

    /**
     * Validates the AI's move to ensure it is within board bounds and the space is not occupied.
     *
     * @param row the row index of the move
     * @param col the column index of the move
     * @param move the character to place ('S' or 'O')
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidAIMove(int row, int col, char move) {
        // Check if the move is within board bounds
        if (row < 0 || row >= game.getBoard().length || col < 0 || col >= game.getBoard().length) {
            return false;
        }
        // Check if the space is empty
        if (game.getBoard()[row][col] != '\0') {
            return false;
        }
        // Optionally, add additional checks if necessary (e.g., valid character check)
        return move == 'S' || move == 'O'; // Assuming 'S' and 'O' are valid moves
    }

    /**
     * Generates a prompt describing the current board state, player turn, etc.
     * to send to the LLM.
     *
     * @return a string prompt for the LLM
     */
    private String generatePromptForLLM() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are playing a game of SOS on a ")
                .append(game.getBoard().length)
                .append("x")
                .append(game.getBoard().length)
                .append(" board.\n");
        prompt.append("The goal of the game is to form the sequence 'SOS' horizontally, vertically, or diagonally.\n");
        prompt.append("You are Player ")
                .append(game.isPlayerOneTurn() ? "1" : "2")
                .append(". Your objective is to either create a new 'SOS' sequence or block the opponent from forming one.\n");
        prompt.append("Remember that you cannot place your move on an already occupied space.\n");
        prompt.append("The board uses 0-based indexing, meaning the top-left cell is (0,0) and the bottom-right cell is (")
                .append(game.getBoard().length - 1)
                .append(",")
                .append(game.getBoard().length - 1)
                .append(").\n");
        prompt.append("Here is the current board state:\n");

        char[][] board = game.getBoard();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == '\0') {
                    prompt.append(". "); // Empty space
                } else {
                    prompt.append(board[r][c]).append("(").append(getPlayerLabel(r, c)).append(") ");
                }
            }
            prompt.append("\n");
        }

        prompt.append("Occupied spaces (row,col): ");
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] != '\0') {
                    prompt.append("(").append(r).append(",").append(c).append(") ");
                }
            }
        }
        prompt.append("\nConsider possible 'S' and 'O' placements that you can make with the goal of forming an 'SOS' on your turn. Provide your move in the format 'row,col,character' (e.g., '1,2,S'). The row and column values start counting from 0.");
        return prompt.toString();
    }

    private String getPlayerLabel(int row, int col) {
        // Check the playerMoves array to determine which player made the move
        String[][] playerMoves = game.getPlayerMoves();
        if (playerMoves[row][col] != null) {
            return playerMoves[row][col]; // Return "P1" or "P2" based on who placed the move
        }
        return ""; // Default case (shouldn't happen for occupied spaces)
    }

    /**
     * Places a move for the computer player based on the response from the LLM.
     *
     * @param row the row where the move is placed
     * @param col the column where the move is placed
     * @param move the character to place ('S' or 'O')
     */
    private void placeComputerMove(int row, int col, char move) {
        if (row >= 0 && row < game.getBoard().length && col >= 0 && col < game.getBoard().length) {
            Button button = (Button) grid.getChildren().get(row * game.getBoard().length + col);
            if (game.placeMove(row, col, move)) {
                button.setText(String.valueOf(move));
                button.setStyle(game.isPlayerOneTurn() ? "-fx-text-fill: red; -fx-font-weight: bold;" : "-fx-text-fill: blue; -fx-font-weight: bold;");
                updateTurnLabel(); // Ensure turn label is updated after AI move
            } else {
                System.err.println("Failed to place AI move on the board.");
            }
        } else {
            System.err.println("AI move is out of board bounds: row=" + row + ", col=" + col);
        }
    }

    /**
     * Updates the turn label based on the current player.
     */
    private void updateTurnLabel() {
        if (game.isPlayerOneTurn()) {
            turnLabel.setText("Player 1's Turn (Place S)");
            turnLabel.setStyle("-fx-text-fill: red;");
        } else {
            turnLabel.setText("Player 2's Turn (Place O)");
            turnLabel.setStyle("-fx-text-fill: blue;");
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
