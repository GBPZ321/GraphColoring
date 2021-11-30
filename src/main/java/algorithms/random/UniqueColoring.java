package algorithms.random;

import algorithms.interfaces.SeedingStrategy;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

public class UniqueColoring implements SeedingStrategy {
    private final Graph<Integer, DefaultEdge> graph;

    public UniqueColoring(Graph<Integer, DefaultEdge> graph) {
        this.graph = graph;
    }

    @Override
    public Map<Integer, Integer> getStartingColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        for(Integer vertex : graph.vertexSet()) {
            coloring.put(vertex, (vertex - 1));
        }
        return coloring;
    }
}
