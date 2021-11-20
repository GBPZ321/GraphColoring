package datastructures;

import java.util.Arrays;

public class PheremoneMatrix {
    private final double[][] matrix;

    public PheremoneMatrix(int rows, int columns) {
        matrix = new double[rows][columns];
        defaultMatrix();
    }

    private void defaultMatrix() {
        for(double[] vec : matrix) {
            Arrays.fill(vec, 1.0);
        }
    }

    public void increment(int row, int column) {
        matrix[row][column]++;
    }

    public void decrement(int row, int column) {
        matrix[row][column]--;
    }

    public void setValue(int row, int column, double value) {
        matrix[row][column] = value;
    }

    public double getValue(int row, int column) {
        return matrix[row][column];
    }
}
