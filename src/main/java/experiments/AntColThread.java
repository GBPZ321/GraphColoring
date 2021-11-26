package experiments;

import algorithms.AntColHeuristic;
import datastructures.common.Shared;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

import java.util.Objects;
import java.util.concurrent.Callable;

public class AntColThread implements Callable<GraphSolution> {
    private final GraphDefinition testGraph;
    private final int threadNo;
    private final Shared shared;
    private final double evaporationRate;
    private final int numberOfAnts;

    public AntColThread(GraphDefinition definition, int threadNo, double evaporationRate, int numberOfAnts, Shared shared) {
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.evaporationRate = evaporationRate;
        this.numberOfAnts = numberOfAnts;
        this.shared = shared;
    }

    public AntColThread(GraphDefinition definition, int threadNo, double evaporationRate, int numberOfAnts) {
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.evaporationRate = evaporationRate;
        this.numberOfAnts = numberOfAnts;
        this.shared = null;
    }

    public GraphSolution call() {
        System.out.println("Starting thread " + threadNo);
        AntColHeuristic heuristic;
        if(Objects.isNull(shared)) {
            heuristic = new AntColHeuristic(testGraph, evaporationRate, numberOfAnts, false, null);
        } else {
            heuristic = new AntColHeuristic(testGraph, evaporationRate, numberOfAnts, false, shared);
        }
        System.out.println("Ending thread " + threadNo);
        return heuristic.getColoring();
    }
}
