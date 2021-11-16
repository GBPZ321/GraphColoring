package algorithms;

import algorithms.common.SolutionWithStatus;
import algorithms.partialcol.PartialColSubroutine;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

public class PartialColHeuristic implements ColoringHeuristic {
    private final GraphDefinition graphDefinition;
    private static final Integer DEFAULT_ITERATIONS = 100000;
    private static final Float DEFAULT_ALPHA = .6f;
    private static final Integer DEFAULT_L = 8;
    private final int L;
    private final float alpha;
    private final int iterations;

    public PartialColHeuristic(GraphDefinition g) {
        this.graphDefinition = g;
        this.alpha = DEFAULT_ALPHA;
        this.L = DEFAULT_L;
        this.iterations = DEFAULT_ITERATIONS;
    }

    public PartialColHeuristic(GraphDefinition g, int iter, float a, int l) {
        graphDefinition = g;
        alpha = a;
        L = l;
        iterations = iter;
    }


    @Override
    public GraphSolution getColoring() {
        GraphSolution solution = null;
        int k = 45;
        while(k > 1) {
            PartialColSubroutine partialColSubroutine = new PartialColSubroutine(graphDefinition, k, alpha, L, DEFAULT_ITERATIONS);
            SolutionWithStatus possibleSolution = partialColSubroutine.findSolution();
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
