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
        Map<Integer, List<Integer>> adjList = definition.getGraphWrapper().getAdjList();
        for(int v = 0; v < n; ++v) {
            for(int z = 0; z < k; ++z) {
                int gamma = gamma(coloring, v + 1, z, adjList);
                matrix.setValue(v, z, gamma);
            }
        }
    }

    public void updateSolution(int vertex, int oldColor, int newColor) {
        Map<Integer, List<Integer>> adjList = definition.getGraphWrapper().getAdjList();

        for(int w : adjList.get(vertex)) {
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

    private static int gamma(Map<Integer, Integer> c, Integer vertex, Integer color, Map<Integer, List<Integer>> adjList) {
        int sum = 0;

        for(int adjacent : adjList.get(vertex)) {
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
