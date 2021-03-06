package experiments;

import algorithms.TabucolHeuristic;
import datastructures.StatisticMatrix;
import datastructures.common.Shared;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

import java.util.Objects;
import java.util.concurrent.Callable;

public class TabucolThread implements Callable<GraphSolution> {
    private final int L;
    private final double alpha;
    private final GraphDefinition testGraph;
    private final int threadNo;
    private final Shared shared;
    private final StatisticMatrix statisticMatrix;

    public TabucolThread(GraphDefinition definition, int L, double alpha, int threadNo, Shared shared, StatisticMatrix statisticMatrix) {
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.L =L;
        this.alpha = alpha;
        this.shared = shared;
        this.statisticMatrix = statisticMatrix;
    }

    public TabucolThread(GraphDefinition definition, int L, double alpha, int threadNo, Shared shared) {
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.L =L;
        this.alpha = alpha;
        this.shared = shared;
        this.statisticMatrix = null;
    }

    public TabucolThread(GraphDefinition definition, int L, double alpha, int threadNo) {
        this.L = L;
        this.alpha = alpha;
        this.testGraph = definition;
        this.threadNo = threadNo;
        this.shared = null;
        this.statisticMatrix = null;
    }

    public GraphSolution call() {
        System.out.println("Starting thread " + threadNo + " with params " + alpha + " " + L);
        TabucolHeuristic heuristic;
        if(Objects.isNull(shared)) {
            heuristic = new TabucolHeuristic(testGraph, alpha, L);
        } else {
            heuristic = new TabucolHeuristic(testGraph, alpha, L, shared, statisticMatrix);
        }
        System.out.println("Ending thread " + threadNo);
        return heuristic.getColoring();
    }
}
