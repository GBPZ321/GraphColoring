package algorithms.random;

import algorithms.interfaces.SeedingStrategy;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

public class UnassignedNeighborColoring implements SeedingStrategy {
    private final Integer k;
    private final Graph<Integer, DefaultEdge> graph;

    public UnassignedNeighborColoring(Graph<Integer, DefaultEdge> graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    @Override
    public Map<Integer, Integer> getStartingColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        for(Integer vertex : graph.vertexSet()) {
            coloring.put(vertex, -1);
        }
        for (int i = 0; i < k ; i++ ) {
            for(Integer vertex : graph.vertexSet()) {
                if(coloring.get(vertex) == -1) {
                    for ( DefaultEdge edge : graph.edgesOf(vertex)) {
                        Integer v1 = graph.getEdgeSource(edge);
                        Integer v2 = graph.getEdgeTarget(edge);
                        // Check if edges contain current color
                        if((coloring.get(v1) == i) || (coloring.get(v2) == i) ) continue;
                        coloring.replace(vertex, i);
                    }

                }
            }
        }
        for(Integer vertex : graph.vertexSet()) {
            if(coloring.get(vertex) == -1) {
                coloring.put(vertex, RandomUtils.getRandomNumber(0, k));
            }
        }
        return coloring;
    }
}
