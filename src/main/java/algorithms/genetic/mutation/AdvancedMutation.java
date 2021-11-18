package algorithms.genetic.mutation;

import algorithms.genetic.Chromosome;
import algorithms.genetic.Mutation;
import algorithms.random.RandomUtils;
import graph.definition.GraphWrapper;

import java.util.*;

import static algorithms.random.RandomUtils.getRandomNumber;

public class AdvancedMutation implements Mutation {
    private final GraphWrapper wrapper;
    private final int k;

    public AdvancedMutation(GraphWrapper wrapper, int k) {
        this.wrapper = wrapper;
        this.k = k;
    }

    @Override
    public Chromosome mutate(Chromosome c) {
        Map<Integer, Integer> coloring = c.getColoring();
        for(int vertex : wrapper.getVertices()) {
            List<Integer> otherColors = new ArrayList<>();
            for(int u : wrapper.getNeighborsOfV(vertex)) {
                if(coloring.get(u).equals(coloring.get(vertex))) {
                    otherColors.add(coloring.get(u));
                }
            }
            if(!otherColors.isEmpty()) {
                int nextColoring = RandomUtils.generateRandom(0, k, otherColors);
                coloring.put(vertex, nextColoring);
            }
        }

        return new Chromosome(coloring, wrapper, k);
    }
}
