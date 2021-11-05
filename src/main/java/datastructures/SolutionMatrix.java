package datastructures;

import datastructures.pojo.Move;
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
            for(int z = 0; z < k; ++z) {
                matrix[v][z] = gamma(colors, v + 1, z, adjList);
            }
        }
    }

    public void updateSolution(int vertex, int oldColor, int newColor) {
        Map<Integer, List<Integer>> adjList = definition.getGraphWrapper().getAdjList();
        int actualVertexKey = vertex - 1;

        for(int w : adjList.get(vertex)) {
            int wLabel = w - 1;
            matrix[wLabel][newColor] = matrix[wLabel][newColor] + 1;

            int problemChild = this.matrix[wLabel][oldColor];
            if(problemChild == 0) {
                System.out.println("Why would this be going below zero? ");
            }
            this.matrix[wLabel][oldColor] = problemChild - 1;
        }
        int computedCount = this.getConflictNumber() + matrix[actualVertexKey][newColor] - matrix[actualVertexKey][oldColor];
        if(computedCount < 0) {
            System.out.println("Problem");
        }
        this.currConflictCount = computedCount;

    }


    public int getMatrixEntry(int vertex, int column) {
        return matrix[vertex - 1][column];
    }

    public int getConflictNumber() {
        return this.currConflictCount;
    }

    public static int delta(Map<Integer, Integer> coloring, int mVertex, int mColor, Map<Integer, List<Integer>> adjList) {
        return gamma(coloring, mVertex, mColor, adjList) - gamma(coloring, mVertex, coloring.get(mVertex), adjList);
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


    public int getMovePerformance(Integer vertex, Integer oldColor, int newColor, int currConflictCount) {
        int perf = matrix[vertex - 1][newColor] - matrix[vertex - 1][oldColor];
        if(perf > currConflictCount) { //Problem child scenario.
            System.out.println("Problem");
        }
        return perf;
    }
}
