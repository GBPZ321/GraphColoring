package algorithms;

import graph.definition.GraphDefinition;

public class PartialColHeuristic {
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



}
