package com.example.miniproyecto2.controller;
import com.example.miniproyecto2.model.SudokuClearAdapter;

import com.example.miniproyecto2.model.Board;
import com.example.miniproyecto2.model.Chronometer;
import com.example.miniproyecto2.model.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;


/**
 * This class handles the events performed by the user
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia Aguiño
 * @code 2343334
 */
public class GameController {
    /**
     * Class Instance Chronometer
     * @serialField
     */
    private Chronometer chronometer;
    /**
     * Class Instance Game
     * @serialField
     */
    private Game game;
    /**
     * Class Instance Board
     * @serialField
     */
    private Board board;
    /**
     * Label Instance
     * @serialField
     */
    @FXML
    private Label hintsLabel;
    /**
     * Button Instance
     * @serialField
     */
    @FXML
    private Button hintButton;
    /**
     * Label Instance
     * @serialField
     */
    @FXML
    private Label scoreLabel;
    /**
     * Label Instance
     * @serialField
     */
    @FXML
    private Label labelTime;
    /**
     * Label Instance
     * @serialField
     */
    @FXML
    private Label errorCountLabel; // Label to display the error counter
    /**
     * Counter for player errors.
     * @serialField
     */
    private int errorCount = 0; // Error counter
    /**
     * The maximum number of errors allowed before the game ends.
     * @serialField
     */
    private final int MAX_ERRORS = 6; // Maximum allowed errors
    /**
     * Score Counter
     * @serialField
     */
    private int score = 0;
    /**
     * Auxiliary Boolean Variable
     * @serialField
     */
    private boolean firstGame = false;
    /**
     * Layout Instance GridPane
     * @serialField
     */
    @FXML
    private GridPane gridPane;
    /**
     * TextField Instance
     * @serialField
     */
    private TextField selectedTextField;
    /**
     * Its value will cause other lines of code to be executed
     * @serialField
     */
    private int selectedRow = -1;
    /**
     * Its value will cause other lines of code to be executed
     * @serialField
     */
    private int selectedColumn = -1;
    /**
     * Button Instance
     * @serialField
     */
    @FXML
    private Button clearButton;
    /**
     * Class Instance SudokuClearAdapter
     * @serialField
     */
    private SudokuClearAdapter clearAdapter;
    /**
     * GameController Class Builder
     */
    public GameController() {}

    /**
     * Defines attribute values and executes methods
     * @see #startGame()
     * @see #initializeErrorCounter()
     */
    @FXML
    public void initialize(){
        this.game = new Game(this);
        board = new Board();
        chronometer = new Chronometer(this);

        startGame();
        initializeErrorCounter();

        clearAdapter = new SudokuClearAdapter(game, gridPane);

        System.out.println("Game controller inicializado");
        hintsLabel.setVisible(false);
        hintButton.setDisable(false);
    }

    /**
     * Launch the main methods of the game
     * @see #generateEvents()
     * @see #resetScore()
     */
    public void startGame(){
        chronometer.start();
        board.fillMatriz();
        generateEvents();
        resetScore();
        game.fillblocks();
    }

    /**
     * Getter method
     * @return instance of Label
     */
    @FXML
     public Label getLabelTime() {
        return labelTime;
    }

    /**
     * Respond to user-made events
     */
    public void generateEvents(){
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                TextField txtField = (TextField) game.getNodeByRowColumnIndex(row, column, gridPane);

                // Verify that the cell contains a Label
                if (txtField != null) {
                    // Asignar un evento de clic a cada Label
                    int finalRow = row;
                    int finalColumn = column;
                    // Add the selection event to the existing TextField
                    txtField.setOnMouseClicked(event -> {
                        game.paintSquares(finalRow, finalColumn);
                        System.out.println("Label at [" + finalRow + "," + finalColumn + "] clicked: " + txtField.getText());
                        selectedTextField = txtField;
                        selectedRow = finalRow;
                        selectedColumn = finalColumn;
                        System.out.println("TextField seleccionado en [" + finalRow + "," + finalColumn + "]");
                    });

                    // Modified the TextFormatter to validate only when a number 1 to 6 is entered
                    txtField.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.isEmpty()) {
                            try {
                                int number = Integer.parseInt(newValue);
                                if (number >= 1 && number <= 6) {
                                    validateMove(finalRow, finalColumn, number);
                                }
                            } catch (NumberFormatException e) {
                                txtField.setText(oldValue);
                            }
                        }
                    });

                    txtField.setTextFormatter(new TextFormatter<>(change -> {
                        if (change.getControlNewText().length() > 1) {
                            return null;
                        }
                        if (!change.getControlNewText().matches("[1-6]*")) {
                            return null;
                        }
                        return change;
                    }));
                }
            }
        }
    }

    /**
     * Runs when the new game button is pressed
     * @param event ActionEvent
     */
    @FXML
    public void newGame(ActionEvent event) {
        clearAdapter.clearSudokuBoard();
        game.cleanMatriz();
        game.fillblocks();
        game.makeEditableCellsEditable();
        chronometer.restart();
        resetErrorCounter();
        resetScore();
        System.out.println("PRESIONADO");
    }

    /**
     * Getter method
     * @return instance of GridPane
     */
    public GridPane getGridPane(){
        return gridPane;
    }


    /**
     * Handles the event when the user presses the how-to-play button.
     * This method displays an information dialog with game instructions.
     *
     * @param event The action event that triggers the how-to-play instructions.
     */
    @FXML
    void onHandleButtonHowToPlay(ActionEvent event) {
        // Create an "INFORMATION" type dialog box that will display an informative dialog
        Alert showMessageHowToPlay = new Alert(Alert.AlertType.INFORMATION);
        // Set the title of the dialog box, the header, the main content of the popup window message
        showMessageHowToPlay.setTitle("Instrucciones");
        showMessageHowToPlay.setHeaderText("Instrucciones del Juego");
        showMessageHowToPlay.setContentText("Bienvenido,\n" +
                "A continuación, te explico cómo jugar:\n" +
                "\n1. Para completar la cuadrícula de Sudoku, debes ingresar un número del 1 al 6 en cada celda.\n" +
                "\n2. Cada fila, columna y bloque de 2x3 debe contener los números del 1 al 6 sin repetir.\n" +
                "\n3. Puedes solicitar hasta seis pistas presionando el botón 'Pista'. Te mostrará un número válido para una celda vacía.\n" +
                "\n4. Debes asegurarte de no dejar ninguna celda vacía ni repetir números en filas, columnas o bloques.\n" +
                "\n¡Buena suerte y diviértete jugando!");

        // Display the dialog box on screen and wait for the user to close it before continuing
        showMessageHowToPlay.showAndWait();
    }



    /**
     * Number of hints available to the player.
     */
    private int availableHints = 6; // Iniciamos con 6 pistas disponibles

    /**
     * Handles the event when the user presses the hint button.
     * This method checks if hints are available and shows a hint if possible.
     *
     * @param event The action event that triggers the hint request.
     * @see #showHint()
     * @see #availableHints
     */
    @FXML
    void onHandleButtonHint(ActionEvent event) {
        System.out.println("BOTÓN DE PISTAS PRESIONADO");
        if (availableHints > 0) {
            showHint();
        } else {
            System.out.println("NO HAY MÁS PISTAS DISPONIBLES");
            hintButton.setDisable(true);
        }
    }

    /**
     * Displays a hint for the user.
     * This method reveals the correct number for the selected cell if it's empty.
     *
     * @see #availableHints
     * @see Game#getMatrizValue(int, int)
     */
    private void showHint() {
        if (selectedTextField != null && selectedTextField.getText().isEmpty()) {
            availableHints--;

            // Get the correct value from the matrix
            int hint = game.getMatrizValue(selectedRow, selectedColumn);

            // Show the value in the selected TextField
            selectedTextField.setText(String.valueOf(hint));

            System.out.println("Pista revelada: " + hint);
            updateHintDisplay();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Selección requerida");
            alert.setHeaderText("Por favor, seleccione una celda vacía");
            alert.showAndWait();
        }
    }

    /**
     * Updates the display of available hints counter.
     * This method is called whenever the number of available hints changes.
     *
     * @see #availableHints
     * @see #hintsLabel
     * @see #hintButton
     */
    private void updateHintDisplay() {
        if (availableHints > 0) {
            // Show only the number of remaining hints
            hintsLabel.setText(String.valueOf(availableHints));
            hintsLabel.setVisible(true);
        } else {
            // When no hints are left, hide the label and disable the button
            hintsLabel.setVisible(false);
            hintButton.setDisable(true);
        }
    }



    /**
     * Initializes the error counter in the interface.
     * This method sets up the initial state of the error counter display.
     *
     * @see #errorCountLabel
     * @see #MAX_ERRORS
     */
    private void initializeErrorCounter() {
        errorCountLabel.setText("0/" + MAX_ERRORS);
    }


    /**
     * Validates a move in the Sudoku and updates the error counter if necessary.
     * This method checks if the entered number violates Sudoku rules and updates the game state accordingly.
     *
     * @param row The row of the move.
     * @param column The column of the move.
     * @param number The entered number.
     * @see #errorCount
     * @see #MAX_ERRORS
     * @see #score
     */
    private void validateMove(int row, int column, int number) {
        boolean hasError = false;
        TextField currentCell = (TextField) game.getNodeByRowColumnIndex(row, column, gridPane);

        // Validate row
        for (int c = 0; c < 6; c++) {
            if (c != column) {
                TextField cellInRow = (TextField) game.getNodeByRowColumnIndex(row, c, gridPane);
                if (cellInRow != null && !cellInRow.getText().isEmpty() &&
                        Integer.parseInt(cellInRow.getText()) == number) {
                    hasError = true;
                    highlightError(cellInRow);   // Highlight the conflicting cell
                }
            }
        }

        // Validate column

        for (int r = 0; r < 6; r++) {
            if (r != row) {
                TextField cellInColumn = (TextField) game.getNodeByRowColumnIndex(r, column, gridPane);
                if (cellInColumn != null && !cellInColumn.getText().isEmpty() &&
                        Integer.parseInt(cellInColumn.getText()) == number) {
                    hasError = true;
                    highlightError(cellInColumn);  // Highlight the conflicting cell
                }
            }
        }

        // Validate 2x3 block
        int blockStartRow = (row / 2) * 2;
        int blockStartCol = (column / 3) * 3;

        for (int r = blockStartRow; r < blockStartRow + 2; r++) {
            for (int c = blockStartCol; c < blockStartCol + 3; c++) {
                if (r != row || c != column) {
                    TextField cellInBlock = (TextField) game.getNodeByRowColumnIndex(r, c, gridPane);
                    if (cellInBlock != null && !cellInBlock.getText().isEmpty() &&
                            Integer.parseInt(cellInBlock.getText()) == number) {
                        hasError = true;
                        highlightError(cellInBlock);  // Highlight the conflicting cell
                    }
                }
            }
        }

        // If there's an error, increment counter and apply styles
        if (hasError) {
            errorCount++;
            if (errorCount <= MAX_ERRORS) {
                errorCountLabel.setText(errorCount + "/" + MAX_ERRORS);
                highlightError(currentCell);  // Highlight the current cell


                // Schedule removal of highlighting after 2 seconds
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.seconds(2),
                        evt -> clearHighlights()
                ));
                timeline.play();
            }

            if (errorCount >= MAX_ERRORS) {
                showGameOverAlert();
            }
        }
        else{
            if(firstGame){
                score += 5;
                scoreLabel.setText(String.valueOf(score));
            }
            else{
                firstGame = true;
                score -= 60;
                score += 5;
                scoreLabel.setText(String.valueOf(score));
            }
        }
    }

    /**
     * Reset Error Counter
     */
    private void resetErrorCounter(){
        errorCount = 0;
        errorCountLabel.setText(errorCount + "/" + MAX_ERRORS);
    }

    /**
     * Reset Error Counter
     */
    private void resetScore(){
        score =0;
        scoreLabel.setText(String.valueOf(score));
    }

    /**
     * Highlights a cell to indicate an error.
     * This method applies a visual style to the cell to show it contains an error.
     *
     * @param cell The TextField cell to highlight.
     * @see #clearHighlights()
     */
    private void highlightError(TextField cell) {
        if (cell != null) {
            // Combines soft background with a red border
            cell.setStyle(
                    "-fx-background-color: rgba(246, 156, 246, 0.8);" +  // Fondo rojo suave
                            "-fx-border-color: #ed5d5d;"                        // Color del borde rojo
            );
        }
    }

    /**
     * Removes all error highlights from the board.
     * This method is called after a certain time period to clear temporary error highlights.
     *
     * @see #highlightError(TextField)
     */
    private void clearHighlights() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                TextField cell = (TextField) game.getNodeByRowColumnIndex(r, c, gridPane);
                if (cell != null) {
                    cell.setStyle("");
                }
            }
        }
    }

    /**
     * Method that reveals the solution of sudoku
     * @param event ActionEvent
     */
    @FXML
    private void showSolution(ActionEvent event) {
        game.showSolutionSudoku();
    }



    /**
     * Displays an alert when the game is over due to too many errors.
     * This method creates and shows a custom alert with a game over image and message.
     *
     * @see #MAX_ERRORS
     * @see #errorCount
     */
    private void showGameOverAlert() {
        Alert gameOverAlert = new Alert(Alert.AlertType.ERROR);
        gameOverAlert.setTitle("Game Over");

        // Load the Game Over image
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/miniproyecto2/images/gameover-image.png")));
        // Adjust the image size if necessary
        imageView.setFitHeight(200);
        imageView.setFitWidth(380);
        imageView.setPreserveRatio(true);

        // Create label for the header with Ravie font and centered
        Label headerLabel = new Label("¡Has perdido!");
        headerLabel.setFont(Font.font("Ravie", 20));
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setMaxWidth(Double.MAX_VALUE); // Allows the label to occupy all available width
        headerLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");

        Label contentLabel = new Label("Has alcanzado el máximo de errores permitidos (6). El juego ha terminado.");
        contentLabel.setFont(Font.font("Impact", 14));
        contentLabel.setWrapText(true);

        // Create a VBox to organize elements vertically
        VBox content = new VBox(10); // 10 is the spacing between elements
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(imageView, contentLabel);

        // Set custom content
        gameOverAlert.getDialogPane().setHeader(headerLabel);
        gameOverAlert.getDialogPane().setContent(content);

        // Customize DialogPane style
        gameOverAlert.getDialogPane().setStyle("-fx-background-color: white;");

        // Show the popup window
        gameOverAlert.showAndWait();

    }

    /**
     * Handles the event to clear the Sudoku board.
     * This method is called when the user requests to clear the board.
     *
     * @param event The action event that triggers the clearing.
     * @see Game#cleanMatriz()
     */
    @FXML
    public void onHandleclearSudokuBoard(ActionEvent event) {
        clearAdapter.clearSudokuBoard(); // Call the adapter method to clear the board
    }
}
