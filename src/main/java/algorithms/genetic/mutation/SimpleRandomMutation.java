package algorithms.genetic.mutation;

import algorithms.genetic.Chromosome;
import algorithms.genetic.Mutation;
import datastructures.SolutionMatrix;
import graph.definition.GraphWrapper;

import java.util.Map;

import static algorithms.random.RandomUtils.getRandomNumber;

public class SimpleRandomMutation implements Mutation {
    private final GraphWrapper wrapper;
    private final int k;

    public SimpleRandomMutation(GraphWrapper wrapper, int k) {
        this.wrapper = wrapper;
        this.k = k;
    }

    @Override
    public Chromosome mutate(Chromosome c) {
        Map<Integer, Integer> coloring = c.getColoring();
        for(int vertex : wrapper.getVertices()) {
            for(int u : wrapper.getNeighborsOfV(vertex)) {
                if(coloring.get(u).equals(coloring.get(vertex))) {
                    coloring.put(vertex, getRandomNumber(0, k));
                }
                break;
            }
        }

        return new Chromosome(coloring, wrapper, k);
    }
}
