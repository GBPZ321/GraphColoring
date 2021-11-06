package datastructures;

import algorithms.SimpleOrderedColoring;
import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.jupiter.api.Test;
import reader.DimacsReader;
import utility.GraphHelperFunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionMatrixTest {

    private static GraphDefinition getSimpleGraph() throws IOException {
        DimacsReader reader = new DimacsReader();
        String name = "simpleGraph.col";
        return reader.getGraph(GraphHelperFunctions.class.getClassLoader().getResourceAsStream(name), name);
    }

    @Test
    void shouldComputeMatrixProperly() throws IOException {
        GraphDefinition simpleGraph = getSimpleGraph();
        int numberOfColors = 3;
        Map<Integer, Integer> coloring = new HashMap<>();
        coloring.put(1, 0);
        coloring.put(2, 1);
        coloring.put(3, 0);
        SolutionMatrix matrix = new SolutionMatrix(coloring, numberOfColors, simpleGraph);

        assertEquals(1, matrix.getMatrixEntry(0,0));
        assertEquals(2, matrix.getMatrixEntry(1, 0));
        assertEquals(1, matrix.getMatrixEntry(2, 0));

        assertEquals(1, matrix.getMatrixEntry(0,1));
        assertEquals(0, matrix.getMatrixEntry(1, 1));
        assertEquals(1, matrix.getMatrixEntry(2, 1));

        assertEquals(0, matrix.getMatrixEntry(0,2));
        assertEquals(0, matrix.getMatrixEntry(1, 2));
        assertEquals(0, matrix.getMatrixEntry(2, 2));

    }
}
