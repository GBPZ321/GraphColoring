package algorithms;

import algorithms.tabucol.TabucolSolution;
import algorithms.tabucol.TabucolSubroutine;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

public class TabucolHeuristic implements ColoringHeuristic {

    private final GraphDefinition graphDefinition;
    private static final Integer DEFAULT_ITERATIONS = 100000;
    private static final Float DEFAULT_ALPHA = .6f;
    private static final Integer DEFAULT_L = 8;
    private final int L;
    private final float alpha;
    private final int iterations;

    public TabucolHeuristic(GraphDefinition g) {
        this.graphDefinition = g;
        this.alpha = DEFAULT_ALPHA;
        this.L = DEFAULT_L;
        this.iterations = DEFAULT_ITERATIONS;
    }

    public TabucolHeuristic(GraphDefinition g, int iter, float a, int l) {
        graphDefinition = g;
        alpha = a;
        L = l;
        iterations = iter;
    }

    @Override
    public GraphSolution getColoring() {
        GraphSolution solution = null;
        int k = graphDefinition.getGraphWrapper().getVertexSize();
        while(k > 1) {
            TabucolSubroutine tabucolSubroutine = new TabucolSubroutine(graphDefinition, k, alpha, iterations, DEFAULT_ITERATIONS);
            TabucolSolution possibleSolution = tabucolSubroutine.findSolution();
            if(possibleSolution.getStatus() == ColoringStatus.SATISFIED) {
                solution = possibleSolution.getSolution();
            }
            if(possibleSolution.getStatus() == ColoringStatus.TIMEOUT) {
                return solution;
            }
            k--;
        }
        return solution;
    }

}
