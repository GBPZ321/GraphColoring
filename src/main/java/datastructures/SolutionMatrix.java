package datastructures;

import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SolutionMatrix {
    private final VertexColoringAlgorithm.Coloring<Integer> coloring;
    private final GraphDefinition definition;
    private final int n;
    private final int k;
    private final int[][] matrix;
    private Integer currConflictCount;

    public SolutionMatrix(VertexColoringAlgorithm.Coloring<Integer> coloring, GraphDefinition definition) {
        this.coloring = coloring;
        this.definition = definition;
        this.n = definition.getGraphWrapper().getVertexSize();
        this.k = coloring.getNumberColors();
        this.matrix = new int[n][k];
        defaultMatrix();
        initializeMatrix();
        computeConflictNumber();
    }

    private void computeConflictNumber() {
        currConflictCount = 0;
        Graph<Integer, DefaultEdge> graphUnderTest = definition.getGraphWrapper().getGraph();
        Set<DefaultEdge> edgeSet = graphUnderTest.edgeSet();
        Map<Integer, Integer> coloringMap = coloring.getColors();
        for(DefaultEdge edge : edgeSet) {
            Integer v1 = graphUnderTest.getEdgeSource(edge);
            Integer v2 = graphUnderTest.getEdgeTarget(edge);
            if(coloringMap.get(v1).equals(coloringMap.get(v2))) {
                currConflictCount++;
            }
        }
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
        //TODO: There is a problem here. Somewhere...
        for(int u : adjList.get(actualVertexKey)) {
            matrix[u][oldColor] = matrix[u][oldColor] - 1;
            matrix[u][newColor] = matrix[u][newColor] + 1;
        }
        this.currConflictCount = this.currConflictCount + matrix[actualVertexKey][newColor] - matrix[actualVertexKey][oldColor];
    }

    public int getMatrixEntry(int vertex, int column) {
        return matrix[vertex - 1][column];
    }

    public int getConflictNumber() {
        return currConflictCount;
    }



}
