package datastructures;

import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SolutionMatrix {
    private final VertexColoringAlgorithm.Coloring<Integer> coloring;
    private final GraphDefinition definition;
    private final int n;
    private final int k;
    private final int[][] matrix;

    public SolutionMatrix(VertexColoringAlgorithm.Coloring<Integer> coloring, GraphDefinition definition) {
        this.coloring = coloring;
        this.definition = definition;
        this.n = definition.getGraphWrapper().getVertexSize();
        this.k = coloring.getNumberColors();
        this.matrix = new int[n][k];
        defaultMatrix();
        initializeMatrix();
    }

    private void defaultMatrix() {
        for(int[] vec : matrix) {
            Arrays.fill(vec, 0);
        }
    }

    private void initializeMatrix() {
        Map<Integer, List<Integer>> adjList = definition.getGraphWrapper().getAdjList();
        Map<Integer, Integer> colors = coloring.getColors();
        for(int v = 0; v < n; ++v) {
            for(int i = 0; i < k; ++i) {
                for(int adjVertex : adjList.get(v + 1)) {
                    if(colors.get(adjVertex) == i) {
                        matrix[v][i]++;
                    }
                }
            }
        }
    }

    public void updateSolution(int vertex, int oldColor, int newColor) {
        Map<Integer, List<Integer>> adjList = definition.getGraphWrapper().getAdjList();
        int actualVertexKey = vertex - 1;
        if(!adjList.containsKey(actualVertexKey)) return;
        for(int u : adjList.get(actualVertexKey)) {
            matrix[u][oldColor] = matrix[u][oldColor] - 1;
            matrix[u][newColor] = matrix[u][newColor] + 1;
        }
    }

    public void updateMatrix(int vertex, int column, int value) {
        matrix[vertex - 1][column] = value;
    }

    public int getMatrixEntry(int vertex, int column) {
        return matrix[vertex - 1][column];
    }

    public int getConflictNumber() {

        //TODO: Error is here.
        int conflictNumber = 0;
        for(int i = 0; i < n; ++i) {
            for(int k = 0; k < n; ++k) {
                conflictNumber += matrix[i][k];
            }
        }
        return conflictNumber;
    }

}
