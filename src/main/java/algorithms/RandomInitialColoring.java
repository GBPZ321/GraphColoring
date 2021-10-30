package algorithms;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

public class RandomInitialColoring implements VertexColoringAlgorithm<Integer> {
    private final Integer k;


    public RandomInitialColoring(Graph<Integer, DefaultEdge> graph) {
        this.k = graph.vertexSet().size();
    }

    @Override
    public Coloring<Integer> getColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        for(int i = 1; i <= k; ++i) {
            coloring.put(i, i);
        }
        return new ColoringImpl<>(coloring, k);
    }
}
