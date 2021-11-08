package datastructures;

import graph.definition.GraphDefinition;

import java.util.List;
import java.util.Map;

public class SolutionMatrix {
    private final Map<Integer, Integer> coloring;
    private final GraphDefinition definition;
    private final int n;
    private final int k;
    private final Matrix matrix;

    public SolutionMatrix(Map<Integer, Integer> coloring, int k, GraphDefinition definition) {
        this.coloring = coloring;
        this.definition = definition;
        this.n = definition.getGraphWrapper().getVertexSize();
        this.k = k;
        this.matrix = new Matrix(n, k);
        initializeMatrix();
    }

    private void initializeMatrix() {
        for(int v = 0; v < n; ++v) {
            for(int z = 0; z < k; ++z) {
                int vertex = v + 1;
                List<Integer> neighbors = definition.getGraphWrapper().getNeighborsOfV(vertex);
                int gamma = gamma(coloring, z, neighbors);
                matrix.setValue(v, z, gamma);
            }
        }
    }

    public void updateSolution(int vertex, int oldColor, int newColor) {
        List<Integer> neighborsOfV = definition.getGraphWrapper().getNeighborsOfV(vertex);
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
            if(c.get(adjacent).equals(color)) {
                sum++;
            }
        }
        return sum;
    }

    public int getMovePerformance(Integer vertex, Integer oldColor, int newColor) {
        return -matrix.getValue(vertex - 1, newColor) + matrix.getValue(vertex - 1, oldColor);
    }

    public int getMatrixEntry(int row, int col) {
        return matrix.getValue(row, col);
    }
}
