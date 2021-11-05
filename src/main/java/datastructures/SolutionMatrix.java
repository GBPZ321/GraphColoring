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
            List<Integer> adjacentVertices = adjList.get(v + 1);
            for(Integer u : adjacentVertices) {
                matrix[v][colors.get(u)]++;
            }
        }
    }

    public void updateSolution(int vertex, int oldColor, int newColor) {
        Map<Integer, List<Integer>> adjList = definition.getGraphWrapper().getAdjList();
        int actualVertexKey = vertex - 1;

        for(int u : adjList.get(vertex)) {
            matrix[u - 1][oldColor] = matrix[u - 1][oldColor] - 1;
            matrix[u - 1][newColor] = matrix[u - 1][newColor] + 1;
        }
        int computedCount = this.currConflictCount + matrix[actualVertexKey][newColor] - matrix[actualVertexKey][oldColor];
        if(computedCount < 0) {
            System.out.println("Problem");
        }
        this.currConflictCount = getConflictNumber();

    }

    public int getMatrixEntry(int vertex, int column) {
        return matrix[vertex - 1][column];
    }

    public int getConflictNumber() {
        int sum = 0;
        for(int i = 0; i < n; ++i) {
            sum += matrix[i][coloring.getColors().get(i + 1)];
        }
        return sum;
    }



}
