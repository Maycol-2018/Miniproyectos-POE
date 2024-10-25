package com.example.miniproyecto2.model;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Adapter class that clears all non-fixed cells on the Sudoku board.
 * This class provides a utility to reset editable cells to their original state.
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