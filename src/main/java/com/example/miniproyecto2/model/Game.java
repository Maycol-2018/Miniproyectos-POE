package com.example.miniproyecto2.model;

import com.example.miniproyecto2.controller.GameController;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class implements the IGame Interface. TThis class contains the logic of the game
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia Aguiño
 * @code 2343334
 */
public class Game implements IGame{
    /**
     * Class Instance List
     * @serialField
     */
    private List<List<Integer>>  matriz;
    /**
     * Class Instance Random
     * @serialField
     */
    private Random random = new Random();
    /**
     * Layout Instance GridPane
     * @serialField
     */
    private GridPane gridPane;
    /**
     * Class Instance GameController
     * @serialField
     */
    private GameController gameController;
    /**
     * A helper variable that executes a block of code based on its value
     * @serialField
     */
    private int lastSelectedRow = -1;
    /**
     * A helper variable that executes a block of code based on its value
     * @serialField
     */
    private int lastSelectedColumn = -1;
    /**
     * A helper variable that executes a block of code based on its value
     * @serialField
     */
    private int lastEditableBoxes = -1;
    /**
     * Stores the positions of the last few boxes by default
     * @serialField
     */
    private int boxesMatriz[][];

    /**
     * Game Class Builder
     * @param gameController Class Instance GameController
     */
    public Game(GameController gameController) {
        this.gameController = new GameController();
        matriz = Board.getMatriz();
        gridPane = gameController.getGridPane();
    }

    // Metodo para obtener el valor correcto de la matriz
    public int getMatrizValue(int row, int col) {
        return matriz.get(row).get(col);
    }


    /**
     *  Reveal the complete sudoku solution
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void showSolutionSudoku(){
        int digit;
        matriz = Board.getMatriz();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                TextField txtField = (TextField) getNodeByRowColumnIndex(row, column, gridPane);
                digit = matriz.get(row).get(column);
                txtField.setText(digit + "");
            }
        }
    }

    /**
     * Clean up the sudoku matrix with empty spaces
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void cleanMatriz(){
        matriz = Board.getMatriz();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                TextField txtField = (TextField) getNodeByRowColumnIndex(row, column, gridPane);
                txtField.setText("");
            }
        }
        System.out.println("Metodo limpiar matriz activado");
    }

    /**
     * Fill each block of the matrix with 2 numbers in a finning shape.
     * @see #editableBoxesTrue()
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void fillblocks(){
        int count=0;
        editableBoxesTrue();
        matriz = Board.getMatriz();
        boxesMatriz = new int[12][2];
        // Iterate over the 2x3 blocks in the array
        for (int bloqueFila = 0; bloqueFila < 6; bloqueFila += 2) {
            for (int bloqueColumna = 0; bloqueColumna < 6; bloqueColumna += 3) {
                List<int[]> posicionesBloque = new ArrayList<>();

                // Add all items in the current block
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        posicionesBloque.add(new int[]{bloqueFila + i, bloqueColumna + j});
                    }
                }

                // Select two different block positions
                int[] posicion1 = posicionesBloque.remove(random.nextInt(posicionesBloque.size()));
                int[] posicion2 = posicionesBloque.remove(random.nextInt(posicionesBloque.size()));

                int pos11 = posicion1[0];
                int pos12 = posicion1[1];
                TextField txtField = (TextField) getNodeByRowColumnIndex(pos11, pos12, gridPane);
                int num = matriz.get(pos11).get(pos12);
                txtField.setText(num + "");

                int pos21 = posicion2[0];
                int pos22 = posicion2[1];
                TextField txtField2 = (TextField) getNodeByRowColumnIndex(pos21, pos22, gridPane);
                int num2 = matriz.get(pos21).get(pos22);
                txtField2.setText(num2 + "");

                // Disable editing of the default cells of each block
                editableBoxesFalse(pos11, pos12, pos21, pos22);

                // The value of the variable that gives the green light to run other lines of code.
                lastEditableBoxes = 0;

                boxesMatriz[count][0] = pos11;
                boxesMatriz[count][1] = pos12;
                count++;
                boxesMatriz[count][0] = pos21;
                boxesMatriz[count][1] = pos22;
                count++;
            }
        }
    }

    public void makeEditableCellsEditable() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField txtField = (TextField) getNodeByRowColumnIndex(row, col, gridPane);
                if (txtField != null) {
                    // Verificamos si el campo de texto no contiene un número por defecto
                    if (txtField.getText().isEmpty() || txtField.getStyleClass().contains("default-cell")) {
                        txtField.setEditable(true); // Solo hacer editable si está vacío o es por defecto
                    } else {
                        txtField.setEditable(false); // Si ya tiene un número, no debe ser editable
                    }
                }
            }
        }
    }

    /**
     * Defines a background color to the labels, block, rows and columns where it was pressed.
     * @param row row
     * @param column column
     * @see #removePaintLastBox()
     * @see #removePaintBlock()
     * @see #removePaintMatriz()
     * @see #paintRows(int)
     * @see #paintColumns(int)
     * @see #paintBlock(int, int)
     * @see #paintCurrentBox(int, int)
     */
    public void paintSquares(int row, int column){
        removePaintLastBox();
        removePaintBlock();
        removePaintMatriz();

        paintRows(row);
        paintColumns(column);
        paintBlock(row, column);
        paintCurrentBox(row, column);

        // Refreshes the selected cell to clean up the background of the last selected cell
        lastSelectedRow = row;
        lastSelectedColumn = column;
    }

    /**
     * Set a fund to the current box
     * @param row row
     * @param column column
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void paintCurrentBox(int row, int column){
        TextField txtField = (TextField) getNodeByRowColumnIndex(row,column, gridPane);
        txtField.getStyleClass().add("currentBoxBackground");
    }

    /**
     * Set a background color to the rows
     * @param row row
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void paintRows(int row){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(row, i, gridPane);
            txtField.getStyleClass().add("squaresBackground");
        }
    }
    /**
     * Set a background color to the columns
     * @param column row
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void paintColumns(int column){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(i, column, gridPane);
            txtField.getStyleClass().add("squaresBackground");
        }
    }

    /**
     * Set a fund to the current Block
     * @param row row
     * @param column column
     * @see #getRowBlock(int)
     * @see #getColumnBlock(int)
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void paintBlock(int row, int column){
        int f = getRowBlock(row);
        int c = getColumnBlock(column);
        // Loop that runs through 2x3 blocks by rows and then by columns
        for(int i=0; i<2; i++){
            for(int j=0; j<3; j++){
                TextField txtField = (TextField) getNodeByRowColumnIndex(f, c, gridPane);
                txtField.getStyleClass().add("squaresBackground");
                c++;
            }
            c = getColumnBlock(column);
            f++;
        }
    }

    /**
     * Remove the background from each sudoku square
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void removePaintMatriz(){
        for(int i=0; i<6; i++){
            for(int j=0; j<6; j++){
                TextField txtField = (TextField) getNodeByRowColumnIndex(i, j, gridPane);
                txtField.getStyleClass().remove("squaresBackground");
            }
        }
    }

    /**
     * Remove the background color from the last box
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void removePaintLastBox(){
        if (lastSelectedRow != -1 && lastSelectedColumn != -1) {
            TextField txtField = (TextField) getNodeByRowColumnIndex(lastSelectedRow, lastSelectedColumn, gridPane);
            txtField.getStyleClass().remove("squaresBackground");
            txtField.getStyleClass().remove("currentBoxBackground");
        }
    }

    /**
     * Remove the background color from the block
     * @see #removePaintRows(int)
     * @see #removePaintColumns(int)
     */
    public void removePaintBlock(){
        if (lastSelectedRow != -1 && lastSelectedColumn != -1) {
            removePaintRows(lastSelectedRow);
            removePaintColumns(lastSelectedColumn);
        }
    }

    /**
     * Remove the background color from the rows
     * @param row row
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void removePaintRows(int row){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(row, i, gridPane);
            txtField.getStyleClass().remove("squaresBackground");
        }
    }

    /**
     * Remove the background color from the columns
     * @param column column
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void removePaintColumns(int column){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(i, column, gridPane);
            txtField.getStyleClass().remove("squaresBackground");
        }
    }

    /**
     * Return the row index of the block, this number varies according to each block.
     * @param row row
     * @return The index of the row
     */
    public int getRowBlock(int row){
        if(row < 2) return 0;
        else if(row < 4) return 2;
        else return 4;
    }

    /**
     * Return the column index of the block, this number varies according to each block.
     * @param column column
     * @return The index of the column
     */
    public int getColumnBlock(int column){
        if(column < 3) return 0;
        else return 3;
    }

    /**
     * Method that disables editing over the default boxes
     * @param row1 row
     * @param column1 column
     * @param row2 row
     * @param column2 column
     * @see #getNodeByRowColumnIndex(int, int, GridPane) 
     */
    public void editableBoxesFalse(int row1, int column1, int row2, int column2){
        TextField txtField = (TextField) getNodeByRowColumnIndex(row1, column1, gridPane);
        txtField.setEditable(false);
        TextField txtField2 = (TextField) getNodeByRowColumnIndex(row2, column2, gridPane);
        txtField2.setEditable(false);
    }

    /**
     * Method that enables editing on the last boxes by default, will be used when the user presses the
     * new game button.
     * @see #getNodeByRowColumnIndex(int, int, GridPane)
     */
    public void editableBoxesTrue(){
        int count =0;
        int f =0;
        int c =0;

        if(lastEditableBoxes != -1) {
            // Se recorren los 6 recuadros, y en cada uno sus 2 casillas se habilitan para ser editadas
            for (int i = 0; i < 12; i++) {
                f = boxesMatriz[count][0];
                c = boxesMatriz[count][1];
                TextField txtField = (TextField) getNodeByRowColumnIndex(f, c, gridPane);
                txtField.setEditable(true);
                count++;
            }
        }
    }

    /**
     * Obtains the TextFields that are inside the GridPane
     * @param row row
     * @param column column
     * @param gridPane instance of GridPane
     * @return An instance of the gridPane node
     */
    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }
}
