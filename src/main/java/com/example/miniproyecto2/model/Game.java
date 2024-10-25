package com.example.miniproyecto2.model;

import com.example.miniproyecto2.controller.GameController;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements IGame{
    private List<List<Integer>>  matriz;
    private Random random = new Random();
    private GridPane gridPane;
    private GameController gameController;
    private Background background;
    private int lastSelectedRow = -1;
    private int lastSelectedColumn = -1;

    // Variable para comprobar en el metodo editableBoxesTrue() si se ha editado una casilla antes.
    private int lastEditableBoxes = -1;

    // Matriz que almacenaran las posiciónes de las ultimas casillas por defecto, para que sean editables
    private int boxesMatriz[][];

    public Game(GameController gameController) {
        this.gameController = new GameController();
        matriz = Board.getMatriz();
        gridPane = gameController.getGridPane();
    }

    /**
     * Gets the value from the Sudoku matrix at the specified position.
     *
     * @param row The row index.
     * @param col The column index.
     * @return The value at the specified position.
     * @throws IndexOutOfBoundsException if the indices are out of bounds.
     *
     */
    public int getMatrizValue(int row, int col) {
        return matriz.get(row).get(col);
    }



     //Metodo que puede revelar toda la matriz, puede servir cuando el usuario haya perdido y se muestre la
     //solución
    public void showSolutionSudoku(){
        int digit;
        matriz = Board.getMatriz();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                // Se obtiene el textfield del GridPane. Correspondiente a la fila y columna actual
                TextField txtField = (TextField) getNodeByRowColumnIndex(row, column, gridPane);
                digit = matriz.get(row).get(column);
                txtField.setText(digit + "");
            }
        }
    }

    public void cleanMatriz(){
        matriz = Board.getMatriz();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                // Se obtiene el textfield del GridPane. Correspondiente a la fila y columna actual
                TextField txtField = (TextField) getNodeByRowColumnIndex(row, column, gridPane);
                txtField.setText("");
            }
        }
        System.out.println("Metodo limpiar matriz activado");
    }

    // Llena cada bloque de la matriz con 2 numeros de forma aletatoria.
    public void fillblocks(){
        int count=0;
        editableBoxesTrue();
        matriz = Board.getMatriz();
        boxesMatriz = new int[12][2];
        // Iterar sobre los bloques de 2x3 en la matriz
        for (int bloqueFila = 0; bloqueFila < 6; bloqueFila += 2) {
            for (int bloqueColumna = 0; bloqueColumna < 6; bloqueColumna += 3) {
                List<int[]> posicionesBloque = new ArrayList<>();

                // Agregar todas las posiciones del bloque actual
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        posicionesBloque.add(new int[]{bloqueFila + i, bloqueColumna + j});
                    }
                }

                // Seleccionar dos posiciones distintas del bloque
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

                // Metodo que deshabilita la posibilidad de hacer modificaciones sobre las celdas por defecto
                // de cada bloque
                editableBoxesFalse(pos11, pos12, pos21, pos22);

                // Ahora el metodo que habilita la posibilidad de modificar las celdas por defecto se puede usar
                // por que ya es ejecuto su metodo que hace el procedimiento contrario.
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

    /**
     * Makes the last default cells editable.
     * This method is used when the user starts a new game, allowing previously
     * fixed cells to become editable.
     *
     * @see #lastEditableBoxes
     * @see #boxesMatriz
     */
    public void makeEditableCellsEditable() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField txtField = (TextField) getNodeByRowColumnIndex(row, col, gridPane);
                if (txtField != null) {
                    // We check if the text field does not contain a number by default
                    if (txtField.getText().isEmpty() || txtField.getStyleClass().contains("default-cell")) {
                        txtField.setEditable(true); // Only make editable if empty or default
                    } else {
                        txtField.setEditable(false); // If it already has a number, it should not be editable
                    }
                }
            }
        }
    }

    // Metodo coloca un color de fondo a los labels, del bloque y de filas y columnas en donde se presiono.
    public void paintSquares(int row, int column){
        // Primero, limpia la celda previamente seleccionada
        removePaintLastBox();
        // Limpia el bloque de la celda previamente seleccionada
        removePaintBlock();
        // Limpia toda la matriz quitando los fondos
        removePaintMatriz();

        // Pinta filas
        paintRows(row);
        // Pinta columnas
        paintColumns(column);
        // Pinta bloque
        paintBlock(row, column);
        // Pinta casilla actual
        paintCurrentBox(row, column);

        // Actualiza la celda seleccionada: Para limpiar el fondo de la ultima celda seleccionada
        lastSelectedRow = row;
        lastSelectedColumn = column;
    }

    public void paintCurrentBox(int row, int column){
        TextField txtField = (TextField) getNodeByRowColumnIndex(row,column, gridPane);
        txtField.getStyleClass().add("currentBoxBackground");
    }

    public void paintRows(int row){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(row, i, gridPane);
            txtField.getStyleClass().add("squaresBackground");
        }
    }

    public void paintColumns(int column){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(i, column, gridPane);
            txtField.getStyleClass().add("squaresBackground");
        }
    }

    public void paintBlock(int row, int column){
        // Obtiene el primer numero de la fila del bloque
        int f = getRowBlock(row);
        // Obtiene el primer numero de la columna de bloque
        int c = getColumnBlock(column);

        // Bucle que recorre los bloques 2x3 por filas y luego por columnas
        for(int i=0; i<2; i++){
            for(int j=0; j<3; j++){
                TextField txtField = (TextField) getNodeByRowColumnIndex(f, c, gridPane);
                txtField.getStyleClass().add("squaresBackground");
                // Se recorre la columna
                c++;
            }
            // Se reinicia al valor de la columna del bloque actual
            c = getColumnBlock(column);
            // Se continua con la siguiente fila.
            f++;
        }
    }

    public void removePaintMatriz(){
        for(int i=0; i<6; i++){
            for(int j=0; j<6; j++){
                TextField txtField = (TextField) getNodeByRowColumnIndex(i, j, gridPane);
                txtField.getStyleClass().remove("squaresBackground");
            }
        }
    }

    public void removePaintLastBox(){
        // Primero, limpia la celda previamente seleccionada
        if (lastSelectedRow != -1 && lastSelectedColumn != -1) {
            TextField txtField = (TextField) getNodeByRowColumnIndex(lastSelectedRow, lastSelectedColumn, gridPane);
            txtField.getStyleClass().remove("squaresBackground");
            txtField.getStyleClass().remove("currentBoxBackground");
        }
    }

    public void removePaintBlock(){
        // Primero, limpia la celda previamente seleccionada
        if (lastSelectedRow != -1 && lastSelectedColumn != -1) {
            removePaintRows(lastSelectedRow);
            removePaintColumns(lastSelectedColumn);
        }
    }

    public void removePaintRows(int row){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(row, i, gridPane);
            txtField.getStyleClass().remove("squaresBackground");
        }
    }

    public void removePaintColumns(int column){
        for(int i= 0; i<6; i++){
            TextField txtField = (TextField) getNodeByRowColumnIndex(i, column, gridPane);
            txtField.getStyleClass().remove("squaresBackground");
        }
    }

    // Retorna la fila del bloque, este numero varía segun cada bloque.
    public int getRowBlock(int row){
        if(row < 2) return 0;
        else if(row < 4) return 2;
        else return 4;
    }

    // Retorna la primera columna del bloque, este número varia según cada bloque
    public int getColumnBlock(int column){
        if(column < 3) return 0;
        else return 3;
    }

    // Metedo que deshabilita la edición sobre las casillas por defecto
    public void editableBoxesFalse(int row1, int column1, int row2, int column2){
        TextField txtField = (TextField) getNodeByRowColumnIndex(row1, column1, gridPane);
        txtField.setEditable(false);
        TextField txtField2 = (TextField) getNodeByRowColumnIndex(row2, column2, gridPane);
        txtField2.setEditable(false);
    }

    // Metodo que habilita la edición sobre las ultimas casillas por defecto, se va a usar cuando el usuario
    // presione el boton de nuevo juego.
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
