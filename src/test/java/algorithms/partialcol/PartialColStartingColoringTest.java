package algorithms.partialcol;

import graph.definition.GraphWrapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PartialColStartingColoringTest {

    @Test
    void getColoring() {
        Graph<Integer, DefaultEdge> simpleGraph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        simpleGraph.addVertex(1);
        simpleGraph.addVertex(2);
        simpleGraph.addVertex(3);
        simpleGraph.addEdge(1, 2);
        simpleGraph.addEdge(2, 3);
        simpleGraph.addEdge(1, 3);

        Map<Integer, List<Integer>> adjList = new HashMap<>();
        adjList.put(1, Arrays.asList(2, 3));
        adjList.put(2, Arrays.asList(1, 3));
        adjList.put(3, Arrays.asList(1, 2));

        GraphWrapper wrapper = new GraphWrapper(simpleGraph, adjList);

        PartialColStartingColoring starterKitAlg = new PartialColStartingColoring(wrapper, 2);
        PartialColStarterKit starterKit = starterKitAlg.getColoring();
        assertEquals(2, starterKit.getStartingColorings().size());
        assertEquals(1, starterKit.getU().size());
    }
}