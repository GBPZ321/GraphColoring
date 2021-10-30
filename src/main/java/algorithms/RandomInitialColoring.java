package algorithms;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

public class RandomInitialColoring implements VertexColoringAlgorithm<Integer> {
    private final Integer k;
    private final Graph<Integer, DefaultEdge> graph;

    public RandomInitialColoring(Graph<Integer, DefaultEdge> graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    @Override
    public Coloring<Integer> getColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        int i = 0;
        for(Integer vertex : graph.vertexSet()) {
            coloring.put(vertex, (vertex + i) % k);
        }
        return new ColoringImpl<>(coloring, k);
    }
}
