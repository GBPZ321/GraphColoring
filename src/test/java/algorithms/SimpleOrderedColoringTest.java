package algorithms;


import algorithms.random.SimpleOrderedColoring;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleOrderedColoringTest {


    @Test
    public void shouldColorRandomlyForVerySimpleGraph() {
        for(int i = 1; i <= 10; ++i) {
            SimpleOrderedColoring initialColoring = new SimpleOrderedColoring(createGraphWithNNodes(i), i);
            VertexColoringAlgorithm.Coloring<Integer> coloring = initialColoring.getColoring();
            assertEquals(i, coloring.getNumberColors());
            assertEquals(i, coloring.getColors().size());
        }
    }

    @Test
    public void shouldColorRandomlyForSmallerGraph() {
        int reducedSize = 2;
        for(int i = 4; i <= 10; ++i) {
            SimpleOrderedColoring initialColoring = new SimpleOrderedColoring(createGraphWithNNodes(i), i - reducedSize);
            VertexColoringAlgorithm.Coloring<Integer> coloring = initialColoring.getColoring();
            assertEquals(i - reducedSize, coloring.getNumberColors());
            assertEquals(i, coloring.getColors().size());
        }
    }


    private Graph<Integer, DefaultEdge> createGraphWithNNodes(int n) {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        for(int i = 1; i <= n; ++i) {
            graph.addVertex(i);
        }
        return graph;
    }
}