package algorithms;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import utility.RandomUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomInitialColoring implements VertexColoringAlgorithm<Integer> {
    private final Graph<Integer, DefaultEdge> graph;
    private final Integer k;
    private Random random;
    private static final Integer LOWER_BOUND_COLORS = 1;

    public RandomInitialColoring(Graph<Integer, DefaultEdge> graph, Integer k) {
        this.k = k;
        this.graph = graph;
        this.random = new Random();
    }

    @Override
    public Coloring<Integer> getColoring() {
        int vertexCount = graph.vertexSet().size();
        Map<Integer, Integer> coloring = new HashMap<>();
        for(int i = 1; i <= vertexCount; ++i) {
            coloring.put(i, RandomUtility.getRandomNumberInRangeInclusive(random, LOWER_BOUND_COLORS, k));
        }
        return new ColoringImpl<>(coloring, k);
    }
}
