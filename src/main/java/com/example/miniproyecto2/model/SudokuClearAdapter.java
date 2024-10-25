package com.example.miniproyecto2.model;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Adapter class for clearing the Sudoku board.
 * This class provides a way to clear the Sudoku board without directly coupling
 * the clear functionality to the main game logic.
 *
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia Aguiño
 * @code 2343334
 */
public class SudokuClearAdapter {
    private Game game;
    private GridPane gridPane;

    public SudokuClearAdapter(Game game, GridPane gridPane) {
        this.game = game;
        this.gridPane = gridPane;
    }
    /**
     * Clears all editable cells on the Sudoku board.
     */
    public void clearSudokuBoard() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField textField = (TextField) game.getNodeByRowColumnIndex(row, col, gridPane);
                if (textField != null && textField.isEditable()) {
                    // Limpiar solo las celdas editables
                    textField.clear();
                }
            }
        }
    }
}