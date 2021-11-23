package algorithms;

import algorithms.interfaces.ColoringHeuristic;
import algorithms.random.SimpleOrderedColoring;
import datastructures.common.Shared;
import datastructures.pojo.SolutionWithStatus;
import algorithms.tabucol.TabucolSubroutine;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.Objects;

public class TabucolHeuristic extends BaseCooperative implements ColoringHeuristic {

    public static final Integer DEFAULT_ITERATIONS = 100000;
    private final GraphDefinition graphDefinition;
    private static final Double DEFAULT_ALPHA = .6;
    private static final Integer DEFAULT_L = 8;
    private int k;
    private final int L;
    private final double alpha;
    private final int iterations;

    public TabucolHeuristic(GraphDefinition g) {
        this.graphDefinition = g;
        this.k = g.getGraphWrapper().getVertexSize();
        this.alpha = DEFAULT_ALPHA;
        this.L = DEFAULT_L;
        this.iterations = DEFAULT_ITERATIONS;
    }

    public TabucolHeuristic(GraphDefinition g, int iter, double a, int l) {
        graphDefinition = g;
        k = g.getGraphWrapper().getVertexSize();
        iterations = iter;
        alpha = a;
        L = l;
    }

    public TabucolHeuristic(GraphDefinition g, int k, int iter, double a, int l) {
        graphDefinition = g;
        this.k = k;
        iterations = iter;
        alpha = a;
        L = l;
    }

    public TabucolHeuristic(GraphDefinition g, int iter, double a, int l, Shared shared) {
        super(shared);
        graphDefinition = g;
        k = g.getGraphWrapper().getVertexSize();
        iterations = iter;
        alpha = a;
        L = l;
    }

    public TabucolHeuristic(GraphDefinition g, int k, int iter, double a, int l, Shared shared) {
        super(shared);
        graphDefinition = g;
        this.k = k;
        iterations = iter;
        alpha = a;
        L = l;
    }

    @Override
    public GraphSolution getColoring() {
        GraphSolution solution = null;
        GraphWrapper graphWrapper = graphDefinition.getGraphWrapper();
        while(k > 1) {
            System.out.println("K = " + k);
            if(isGlobalKLower(k)) {
                k = shared.getCurrentMinimum().getK();
                k--;
                continue;
            }
            TabucolSubroutine tabucolSubroutine = new TabucolSubroutine(graphDefinition, k, alpha, L, iterations, new SimpleOrderedColoring(graphWrapper.getGraph(), k));
            SolutionWithStatus possibleSolution = tabucolSubroutine.findSolution();
            if(possibleSolution.getStatus() == ColoringStatus.SATISFIED) {
                solution = possibleSolution.getSolution();
            } else if(isGlobalKLower(k)) {
                k = shared.getCurrentMinimum().getK();
            } else if(possibleSolution.getStatus() == ColoringStatus.TIMEOUT && isFinished(k)) {
                return solution;
            } else {
                k = shared.getCurrentMinimum().getK();
            }
            k--;
        }
        return solution;
    }

    @Override
    public String getName() {
        return "Tabucol";
    }

}
