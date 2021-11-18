package algorithms.random;

import algorithms.interfaces.SeedingStrategy;
import graph.definition.GraphWrapper;

import java.util.HashMap;
import java.util.Map;

public class CompletelyRandomColoring implements SeedingStrategy {

    private final GraphWrapper wrapper;
    private final int max;

    public CompletelyRandomColoring(GraphWrapper wrapper, int max) {
        this.wrapper = wrapper;
        this.max = max;
    }

    @Override
    public Map<Integer, Integer> getStartingColoring() {
        Map<Integer, Integer> out = new HashMap<>();
        for(Integer vertex : wrapper.getVertices()) {
            out.put(vertex, generate(0, max - 1));
        }
        return out;
    }

    public static int generate(int min,int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
