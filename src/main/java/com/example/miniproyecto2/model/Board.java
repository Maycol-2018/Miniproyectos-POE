package com.example.miniproyecto2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the IBoard Interface. This class contains the methods for creating an array that
 * contains numbers from 1-6 such that their positions meet the constraints of sudoku.
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia Aguiño
 * @code 2343334
 */
public class Board implements IBoard{
    /**
     * Class Instance List
     * @serialField
     */
    static List<List<Integer>>  matriz = new ArrayList<>();

    /**
     * Stores the Boolean values corresponding to the rows
     * @serialField
     */
    static boolean[][] rows = new boolean[6][7];
    /**
     * Stores the Boolean values corresponding to the columns
     * @serialField
     */

    static boolean[][] columns = new boolean[6][7];

    /**
     *  Board Class Builder
     */
    public Board() {}

    /**
     * Check to see if it's safe to place the number in that position.
     * @param f row
     * @param c columns
     * @param num number
     * @return true if the number is not in the row or column or false if it is in the row or column
     */
    public boolean isSafe(int f, int c, int num) {
        return !rows[f][num] && !columns[c][num];
    }

    /**
     * Check if the number doesn't repeat in the block
     * @param i row
     * @param j column
     * @param value number
     * @return true if the number is not in the block or false if the number is in the block
     * @see #blockColumns(int)
     * @see #blockRows(int)
     */
    public boolean isBlockSafe(int i, int j, int value){
        int posI = blockRows(i);
        int posJ = blockColumns(j);

        for(int k = posI-2; k<posI; k++){
            for(int l = posJ-3; l<posJ; l++){
                if(matriz.get(k).get(l) == value){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a value from the row based on the range the parameter is in
     * @param f row
     * @return row number
     */
    public int blockRows(int f){
        if(f<=1) return 2;
        if(f<=3) return 4;
        else return 6;
    }

    /**
     * Returns a value from the column based on the range the parameter is in
     * @param c column
     * @return column number
     */
    public int blockColumns(int c){
        if(c<=2) return 3;
        else return 6;
    }

    /**
     * Inserts the number into a position in the list array
     * @param f row
     * @param c column
     * @param num number
     */
    public void insertNumber(int f, int c, int num) {
        matriz.get(f).set(c,num);
        rows[f][num] = true;
        columns[c][num] = true;
    }

    /**
     * Removes the number from an item from the list array
     * @param f row
     * @param c column
     * @param num number
     */
    public void removeNumber(int f, int c, int num) {
        matriz.get(f).set(c,0);
        rows[f][num] = false;
        columns[c][num] = false;
    }

    /**
     * This method uses backtracking to test all possible solutions and position a number in the list arrangement
     * that meets the restrictions of sudoku
     * @param f row
     * @param c column
     * @see #solveSudoku(int, int) 
     * @see #insertNumber(int, int, int) 
     * @see #removeNumber(int, int, int) 
     * @return true if the number does not repeat in the rows, columns and blocks and returns false in at least one of
     * the previous cases.
     */
    public boolean solveSudoku(int f, int c) {
        // If f = 6 the sudoku is complete
        if (f == 6) {
            return true;
        }

        // Moves to the next row
        if (c == 6) {
            return solveSudoku(f + 1, 0);
        }

        // If the cell is full, continue with the next column
        if (matriz.get(f).get(c) != 0) {
            return solveSudoku(f, c + 1);
        }

        // List with numbers from 1-6
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int num = 1; num <= 6; num++) {
            numbers.add(num);
        }
        // Shuffle List to position different numbers
        Collections.shuffle(numbers);

        // Position numbers that meet the sudoku constraint
        for (int num : numbers) {
            if (isSafe(f,c,num) && isBlockSafe(f,c,num)) {
                insertNumber(f, c, num);
                // Recursive call for the next number
                if (solveSudoku(f, c + 1)) {
                    return true;
                }
                removeNumber(f, c, num);
            }
        }
        return false;
    }

    /**
     * Initializes the list of lists with default values of "0"
     */
    public void fillMatriz() {
        for (int i = 0; i < 6; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                row.add(0);
            }
            matriz.add(row);
        }
        solveSudoku(0, 0);
    }

    /**
     * Getter method
     * @return Array Instance
     */
    public static List<List<Integer>>  getMatriz() {
        return matriz;
    }
}


