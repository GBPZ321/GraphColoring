package experiments;

import algorithms.MetropolisHeuristic;
import algorithms.enums.MetropolisSeedingStrategy;
import datastructures.common.Shared;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class MetropolisThread implements Callable<GraphSolution> {
    private final int timeout;
    private final double beta;
    private final GraphDefinition testGraph;
    private final int threadNo;
    private final Shared shared;
    private final MetropolisSeedingStrategy seedingStrategy;

    public MetropolisThread(GraphDefinition definition, int timeout, double beta, int threadNo,
                            MetropolisSeedingStrategy seedingStrategy, Shared shared) {
        this.timeout = timeout;
        this.beta = beta;
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.shared = shared;
        this.seedingStrategy = seedingStrategy;
    }

    public MetropolisThread(GraphDefinition definition, int timeout, double beta, int threadNo,
                            MetropolisSeedingStrategy seedingStrategy) {
        this.timeout = timeout;
        this.beta = beta;
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.shared = null;
        this.seedingStrategy = seedingStrategy;
    }

    public GraphSolution call() {
        System.out.println("Starting thread " + threadNo);
        MetropolisHeuristic heuristic;
        if(Objects.isNull(shared)) {
            heuristic = new MetropolisHeuristic(testGraph, beta, timeout);
        } else {
            heuristic = new MetropolisHeuristic(testGraph, beta, timeout, shared);
        }
        System.out.println("Ending thread " + threadNo);
        return heuristic.getColoring(seedingStrategy);
    }
}
