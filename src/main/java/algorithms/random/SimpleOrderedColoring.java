package algorithms.random;

import algorithms.interfaces.SeedingStrategy;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * Returns a graph with nodes colored by their vertex.
 * The colors range from [0...(k-1)]
 * Colors must be ordered from [1...n]
 * Does not usually produce a valid coloring unless k >= |V|
 */
public class SimpleOrderedColoring implements SeedingStrategy {
    private final Integer k;
    private final Graph<Integer, DefaultEdge> graph;

    public SimpleOrderedColoring(Graph<Integer, DefaultEdge> graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    @Override
    public Map<Integer, Integer> getStartingColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        for(Integer vertex : graph.vertexSet()) {
            coloring.put(vertex, (vertex - 1) % k);
        }
        return coloring;
    }
}
