package datastructures;

import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;

import java.util.List;
import java.util.Map;

public class SolutionMatrix {
    private final Map<Integer, Integer> coloring;
    private final GraphWrapper wrapper;
    private final int n;
    private final int k;
    private final Matrix matrix;

    public SolutionMatrix(Map<Integer, Integer> coloring, int k, GraphWrapper wrapper) {
        this.coloring = coloring;
        this.wrapper = wrapper;
        this.n = wrapper.getVertexSize();
        this.k = k;
        this.matrix = new Matrix(n, k);
        initializeMatrix();
    }

    private void initializeMatrix() {
        for(int v = 0; v < n; ++v) {
            for(int z = 0; z < k; ++z) {
                int vertex = v + 1;
                List<Integer> neighbors = wrapper.getNeighborsOfV(vertex);
                int gamma = gamma(coloring, z, neighbors);
                matrix.setValue(v, z, gamma);
            }
        }
    }

    public void updateSolution(int vertex, int oldColor, int newColor) {
        List<Integer> neighborsOfV = wrapper.getNeighborsOfV(vertex);
        for(int w : neighborsOfV) {
            int wLabel = w - 1;
            matrix.getValue(wLabel, newColor);
            matrix.increment(wLabel, newColor);
            matrix.decrement(wLabel, oldColor);
        }
    }

    public int getConflictNumber() {
        int conflictNumber = 0;
        for(int v = 0; v < n; ++v) {
            Integer colorOfV = coloring.get(v + 1);
            conflictNumber += matrix.getValue(v, colorOfV);
        }
        return conflictNumber;
    }

    private static int gamma(Map<Integer, Integer> c, Integer color, List<Integer> neighbors) {
        int sum = 0;

        for(int adjacent : neighbors) {
            if(!c.containsKey(adjacent)) continue; // PARTIALCOL
            if(c.get(adjacent).equals(color)) {
                sum++;
            }
        }
        return sum;
    }

    public int getMovePerformance(Integer vertex, Integer oldColor, int newColor) {
        return -matrix.getValue(vertex - 1, newColor) + matrix.getValue(vertex - 1, oldColor);
    }

    public void updateCForPartialCol(Integer vertex, int j) {
        for(int u : wrapper.getNeighborsOfV(vertex)) {
            matrix.increment(u - 1, j);
            if(!coloring.containsKey(u)) {
                continue;
            }
            if(coloring.get(u) == j) {
                for(int w : wrapper.getNeighborsOfV(u)) {
                    matrix.decrement(w - 1, j);
                }
            }
        }
    }


    /**
     * Get RC value for mat.
     * @param vertex - vertex - 1 based
     * @param color - color
     * @return - matrix value
     */
    public int getMatrixEntry(int vertex, int color) {
        return matrix.getValue(vertex, color);
    }

    /**
     * Set RC value for mat.
     * @param vertex - vertex - 1 based
     * @param color - color
     */
    public void setMatrixEntry(int vertex, int color, int value) {
        matrix.setValue(vertex - 1, color, value);
    }
}
