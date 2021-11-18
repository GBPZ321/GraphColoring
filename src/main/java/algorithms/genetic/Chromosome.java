package algorithms.genetic;

import algorithms.interfaces.SeedingStrategy;
import datastructures.SolutionMatrix;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;
import lombok.Data;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

@Data
public class Chromosome implements Comparable<Chromosome> {
    private final Map<Integer, Integer> coloring;
    private final GraphWrapper wrapper;
    private final int k;
    private final int fitness;

    public Chromosome(Map<Integer, Integer> coloring, GraphWrapper wrapper, int k) {
        this.coloring = coloring;
        this.wrapper = wrapper;
        this.k = k;
        this.fitness = this.fitness();
    }

    public Chromosome(SeedingStrategy seedingStrategy, GraphWrapper wrapper, int k) {
        this.coloring = seedingStrategy.getStartingColoring();
        this.wrapper = wrapper;
        this.k = k;
        this.fitness = this.fitness();
    }

    private int fitness() {
        int fitness = 0;
        for(DefaultEdge edge : wrapper.getEdges()) {
            int src = wrapper.getGraph().getEdgeSource(edge);
            int dest = wrapper.getGraph().getEdgeTarget(edge);
            if(coloring.get(src).equals(coloring.get(dest))) {
                fitness++;
            }
        }
        return fitness;
    }


    @Override
    public int compareTo(Chromosome o) {
        int fitness = o.fitness();
        int myFitness = this.fitness();
        return Integer.compare(myFitness, fitness);
    }
}
