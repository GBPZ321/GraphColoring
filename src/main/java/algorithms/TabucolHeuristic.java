package algorithms;

import algorithms.interfaces.ColoringHeuristic;
import algorithms.random.SimpleOrderedColoring;
import algorithms.tabucol.TabucolSubroutine;
import datastructures.StatisticMatrix;
import datastructures.common.Shared;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.Objects;

public class TabucolHeuristic extends BaseCooperative implements ColoringHeuristic {

    public static final Integer DEFAULT_ITERATIONS = 100000;
    private final GraphDefinition graphDefinition;
    private static final Double DEFAULT_ALPHA = .6;
    private static final Integer DEFAULT_L = 8;
    private final int L;
    private final double alpha;
    private final StatisticMatrix statisticMatrix;

    public TabucolHeuristic(GraphDefinition g) {
        this.graphDefinition = g;
        this.alpha = DEFAULT_ALPHA;
        this.L = DEFAULT_L;
        this.statisticMatrix = null;
    }

    public TabucolHeuristic(GraphDefinition g, double a, int l) {
        graphDefinition = g;
        alpha = a;
        L = l;
        this.statisticMatrix = null;
    }

    public TabucolHeuristic(GraphDefinition g, double a, int l, Shared shared, StatisticMatrix statisticMatrix) {
        super(shared);
        graphDefinition = g;
        alpha = a;
        L = l;
        this.statisticMatrix = statisticMatrix;
    }

    @Override
    public GraphSolution getColoring() {
        GraphSolution solution = null;
        GraphWrapper graphWrapper = graphDefinition.getGraphWrapper();
        int k = graphWrapper.getVertexSize();
        while(k > 1) {
            System.out.println("K = " + k);
            if(isGlobalKLower(k)) {
                k = shared.getCurrentMinimum().getK();
                k--;
                continue;
            }
            TabucolSubroutine tabucolSubroutine;
            if(Objects.isNull(statisticMatrix)) {
                 tabucolSubroutine = new TabucolSubroutine(graphDefinition, k, alpha, L, DEFAULT_ITERATIONS, new SimpleOrderedColoring(graphWrapper.getGraph(), k), this.shared);
            } else {
                tabucolSubroutine = new TabucolSubroutine(graphDefinition, k, alpha, L, DEFAULT_ITERATIONS, new SimpleOrderedColoring(graphWrapper.getGraph(), k), this.statisticMatrix, this.shared);
            }
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
