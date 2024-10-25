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



public class GameController {
    private Chronometer chronometer;
    private Game game;
    private Board board;

    // label que muestra la cantidad de pistas disponibles
    @FXML
    private Label hintsLabel;
    // boton  que el usuario presiona para solicitar ayuda en una pista
    @FXML
    private Button hintButton;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label labelTime;

    @FXML
    private Label errorCountLabel; // Label para mostrar el contador de errores
    private int errorCount = 0; // Contador de errores
    private final int MAX_ERRORS = 6; // Máximo de errores permitidos
    private int score = 0;
    private boolean firstGame = false;

    @FXML
    private GridPane gridPane;
    private TextField selectedTextField; // Variable para guardar el TextField seleccionado
    private int selectedRow = -1; // Almacena la fila del TextField seleccionado
    private int selectedColumn = -1; // Almacena la columna del TextField seleccionado

    @FXML
    private Button clearButton;

    private SudokuClearAdapter clearAdapter;

    public GameController() {}

    @FXML
    public void initialize(){
        // Pasamos "this" como referencia de GameController
        this.game = new Game(this);
        board = new Board();
        chronometer = new Chronometer(this);
        // Se proporciona al cronometro una referencia directa al objeto actual de la clase (GameController)
        // Así la clase cronometro puede acceder a los elementos de GameController.
        startGame();

        // Inicializar el contador de errores
        initializeErrorCounter();

        clearAdapter = new SudokuClearAdapter(game, gridPane);

        System.out.println("Game controller inicializado");
        hintsLabel.setVisible(false); // El label está oculto inicialmente
        hintButton.setDisable(false); // El botón está habilitado desde el inicio
    }

    public void startGame(){
        chronometer.start();
        board.fillMatriz();
        generateEvents();
        resetScore();
        //fillTxtLabel();
        game.fillblocks();
    }

    @FXML
     public Label getLabelTime() {
        return labelTime;
    }

    /* MANEJO DE EVENTOS Y VALIDACIONES */

    public void generateEvents(){
        // Recorrer todas las filas y columnas del GridPane 6x6
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                TextField txtField = (TextField) game.getNodeByRowColumnIndex(row, column, gridPane);

                // Verificar que la celda contenga un Label
                if (txtField != null) {
                    // Asignar un evento de clic a cada Label
                    int finalRow = row;
                    int finalColumn = column;
                    // Agregar el evento de selección al TextField existente
                    txtField.setOnMouseClicked(event -> {
                        game.paintSquares(finalRow, finalColumn);
                        System.out.println("Label at [" + finalRow + "," + finalColumn + "] clicked: " + txtField.getText());
                        selectedTextField = txtField;
                        selectedRow = finalRow;
                        selectedColumn = finalColumn;
                        System.out.println("TextField seleccionado en [" + finalRow + "," + finalColumn + "]");
                    });

                    // Modificado el TextFormatter para validar solo cuando se ingresa un número 1 a 6
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

    // Metodo que se ejecuta cuando se presione el boton de nuevo juego
    @FXML
    public void newGame(ActionEvent event) {
        clearAdapter.clearSudokuBoard(); // Limpiar el tablero antes de iniciar un nuevo juego
        game.cleanMatriz();
        game.fillblocks();
        game.makeEditableCellsEditable(); // Hacer editables solo las celdas que no son por defecto
        chronometer.restart();
        resetErrorCounter();
        resetScore();

        // Metodo que reinicie el contador
        // Metodo que reinicie los errores
        // Metodo que reinicie la puntuación
        System.out.println("PRESIONADO");
    }

    public GridPane getGridPane(){
        return gridPane;
    }

    /* SISTEMA DE AYUDA/INSTRUCCIONES */

    @FXML
    void onHandleButtonHowToPlay(ActionEvent event) {
        // Crear una cuadro de diálogo de tipo "INFORMATION" que mostrará un cuadro de diálogo informativo
        Alert showMessageHowToPlay = new Alert(Alert.AlertType.INFORMATION);
        // Establece el título del cuadro de diálogo, el encabezado, el contenido principal del mensaje de la ventana emergente
        showMessageHowToPlay.setTitle("Instrucciones");
        showMessageHowToPlay.setHeaderText("Instrucciones del Juego");
        showMessageHowToPlay.setContentText("Bienvenido,\n" +
                "A continuación, te explico cómo jugar:\n" +
                "\n1. Para completar la cuadrícula de Sudoku, debes ingresar un número del 1 al 6 en cada celda.\n" +
                "\n2. Cada fila, columna y bloque de 2x3 debe contener los números del 1 al 6 sin repetir.\n" +
                "\n3. Puedes solicitar hasta seis pistas presionando el botón 'Pista'. Te mostrará un número válido para una celda vacía.\n" +
                "\n4. Debes asegurarte de no dejar ninguna celda vacía ni repetir números en filas, columnas o bloques.\n" +
                "\n¡Buena suerte y diviértete jugando!");

        // Mostrar el cuadro de diálogo en pantalla y esperar a que el usuario la cierre antes de continuar
        showMessageHowToPlay.showAndWait();
    }

    /* MANEJO DE PISTAS */

    private int availableHints = 6; // Iniciamos con 6 pistas disponibles

    // Metodo que maneja el evento del clic del boton de pistas
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

    private void showHint() {
        if (selectedTextField != null && selectedTextField.getText().isEmpty()) {
            availableHints--;

            // Obtener el valor correcto de la matriz
            int hint = game.getMatrizValue(selectedRow, selectedColumn);

            // Mostrar el valor en el TextField seleccionado
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

    // Metodo que actualiza la visualización del contador de pistas
    private void updateHintDisplay() {
        if (availableHints > 0) {
            // Muestra solo el número de pistas restantes
            hintsLabel.setText(String.valueOf(availableHints));
            hintsLabel.setVisible(true);
        } else {
            // Cuando no quedan pistas, oculta el label y deshabilita el botón
            hintsLabel.setVisible(false);
            hintButton.setDisable(true);
        }
    }

    /* SISTEMA DE CONTADOR DE ERRORES */

    // Metodo para inicializar el contador de errores
    private void initializeErrorCounter() {
        errorCountLabel.setText("0/" + MAX_ERRORS);
    }

    /* VALIDACIÓN DETALLADA DE MOVIMIENTOS PARA ERRORES */

    private void validateMove(int row, int column, int number) {
        boolean hasError = false;
        TextField currentCell = (TextField) game.getNodeByRowColumnIndex(row, column, gridPane);

        // Validar fila
        for (int c = 0; c < 6; c++) {
            if (c != column) {
                TextField cellInRow = (TextField) game.getNodeByRowColumnIndex(row, c, gridPane);
                if (cellInRow != null && !cellInRow.getText().isEmpty() &&
                        Integer.parseInt(cellInRow.getText()) == number) {
                    hasError = true;
                    highlightError(cellInRow);  // Resalta la celda con conflicto
                }
            }
        }

        // Validar columna
        for (int r = 0; r < 6; r++) {
            if (r != row) {
                TextField cellInColumn = (TextField) game.getNodeByRowColumnIndex(r, column, gridPane);
                if (cellInColumn != null && !cellInColumn.getText().isEmpty() &&
                        Integer.parseInt(cellInColumn.getText()) == number) {
                    hasError = true;
                    highlightError(cellInColumn);  // Resalta la celda con conflicto
                }
            }
        }

        // Validar bloque 2x3
        int blockStartRow = (row / 2) * 2;
        int blockStartCol = (column / 3) * 3;

        for (int r = blockStartRow; r < blockStartRow + 2; r++) {
            for (int c = blockStartCol; c < blockStartCol + 3; c++) {
                if (r != row || c != column) {
                    TextField cellInBlock = (TextField) game.getNodeByRowColumnIndex(r, c, gridPane);
                    if (cellInBlock != null && !cellInBlock.getText().isEmpty() &&
                            Integer.parseInt(cellInBlock.getText()) == number) {
                        hasError = true;
                        highlightError(cellInBlock);  // Resalta la celda con conflicto
                    }
                }
            }
        }

        // Si hay error, incrementar contador y aplicar estilos
        if (hasError) {
            errorCount++;
            if (errorCount <= MAX_ERRORS) {
                errorCountLabel.setText(errorCount + "/" + MAX_ERRORS);
                highlightError(currentCell);  // Resalta la celda actual

                // Programar la eliminación del resaltado después de 2 segundos
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

    // reiniciar contador de errores
    private void resetErrorCounter(){
        errorCount = 0;
        errorCountLabel.setText(errorCount + "/" + MAX_ERRORS);
    }

    // Reiniciar contador de puntuatción
    private void resetScore(){
        score =0;
        scoreLabel.setText(String.valueOf(score));
    }

    // Metodo para resaltar una celda con error
    private void highlightError(TextField cell) {
        if (cell != null) {
            // Combina el fondo suave con un borde rojo
            cell.setStyle(
                    "-fx-background-color: rgba(246, 156, 246, 0.8);" +  // Fondo rojo suave
                            "-fx-border-color: #ed5d5d;"                        // Color del borde rojo
            );
        }
    }
    // Metodo para resaltar una celda con error usando coordenadas
    private void highlightError(int row, int column) {
        TextField cell = (TextField) game.getNodeByRowColumnIndex(row, column, gridPane);
        highlightError(cell);
    }

    // Metodo para limpiar todos los resaltados
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

    // Metodo que revela la solución del sudoku
    @FXML
    private void showSolution(ActionEvent event) {
        game.showSolutionSudoku();
    }

    /* SISTEMA DE GAME OVER  */


    private void showGameOverAlert() {
        Alert gameOverAlert = new Alert(Alert.AlertType.ERROR);
        gameOverAlert.setTitle("Game Over");

        // Cargar la imagen de Game Over
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/example/miniproyecto2/images/gameover-image.png")));
        // Ajustar el tamaño de la imagen si es necesario
        imageView.setFitHeight(200);
        imageView.setFitWidth(380);
        imageView.setPreserveRatio(true);

        // Crear label para el header con fuente Ravie y centrado
        Label headerLabel = new Label("¡Has perdido!");
        headerLabel.setFont(Font.font("Ravie", 20));
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setMaxWidth(Double.MAX_VALUE); //Permite que el label ocupe todo el ancho disponible
        headerLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");

        Label contentLabel = new Label("Has alcanzado el máximo de errores permitidos (6). El juego ha terminado.");
        contentLabel.setFont(Font.font("Impact", 14)); // Cambiar fuente a Impact
        contentLabel.setWrapText(true); // Permitir que el texto se ajuste

        // Crear un VBox para organizar los elementos verticalmente
        VBox content = new VBox(10); // 10 es el espaciado entre elementos
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(imageView, contentLabel);

        // Establecer el contenido personalizado
        gameOverAlert.getDialogPane().setHeader(headerLabel);
        gameOverAlert.getDialogPane().setContent(content);

        // Personalizar el estilo del DialogPane (opcional)
        gameOverAlert.getDialogPane().setStyle("-fx-background-color: white;");

        // Mostrar la ventana emergente
        gameOverAlert.showAndWait();

        // Opcional: Aquí se puede  agregar código para reiniciar el juego
    }

    @FXML
    public void onHandleclearSudokuBoard(ActionEvent event) {
        clearAdapter.clearSudokuBoard(); // Llama al metodo del adaptador para limpiar el tablero
    }
}
