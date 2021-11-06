package datastructures;

import java.util.Arrays;

public class Matrix {
    private final int[][] matrix;

    public Matrix(int rows, int columns) {
        matrix = new int[rows][columns];
        defaultMatrix();
    }

    private void defaultMatrix() {
        for(int[] vec : matrix) {
            Arrays.fill(vec, 0);
        }
    }

    public void increment(int row, int column) {
        matrix[row][column]++;
    }

    public void decrement(int row, int column) {
        matrix[row][column]--;
    }

    public void setValue(int row, int column, int value) {
        matrix[row][column] = value;
    }

    public int getValue(int row, int column) {
        return matrix[row][column];
    }
}
