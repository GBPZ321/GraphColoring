package algorithms;

import algorithms.interfaces.ColoringHeuristic;
import datastructures.StatisticMatrix;
import datastructures.common.Shared;
import datastructures.pojo.SolutionWithStatus;
import algorithms.partialcol.PartialColSubroutine;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

import java.util.Objects;

public class PartialColHeuristic extends BaseCooperative implements ColoringHeuristic {
    private final GraphDefinition graphDefinition;
    private static final Integer DEFAULT_ITERATIONS = 100000;
    public static final Float DEFAULT_ALPHA = .8f;
    public static final Integer DEFAULT_L = 8;
    private final int L;
    private final double alpha;
    private final StatisticMatrix statisticMatrix;



    public PartialColHeuristic(GraphDefinition g, double a, int l) {
        graphDefinition = g;
        alpha = a;
        L = l;
        this.shared = null;
        this.statisticMatrix = null;
    }

    public PartialColHeuristic(GraphDefinition g, double a, int l, Shared shared, StatisticMatrix matrix) {
        super(shared);
        graphDefinition = g;
        alpha = a;
        L = l;
        this.shared = shared;
        this.statisticMatrix = matrix;
    }


    @Override
    public GraphSolution getColoring() {
        GraphSolution solution = null;
        int k = graphDefinition.getGraphWrapper().getVertexSize();
        while(k > 1) {
            PartialColSubroutine partialColSubroutine;
            if(Objects.isNull(statisticMatrix)) {
                partialColSubroutine = new PartialColSubroutine(graphDefinition, k, alpha, L, DEFAULT_ITERATIONS, shared);
            } else {
                partialColSubroutine = new PartialColSubroutine(graphDefinition, k, alpha, L, DEFAULT_ITERATIONS, shared, statisticMatrix);
            }
            SolutionWithStatus possibleSolution = partialColSubroutine.findSolution();
            if(possibleSolution.getStatus() == ColoringStatus.SATISFIED) {
                solution = possibleSolution.getSolution();
            } else if(isGlobalKLower(k)) {
                k = shared.getCurrentMinimum().getK();
            } else if(possibleSolution.getStatus() == ColoringStatus.TIMEOUT && isFinished(k)) {
                return solution;
            }
            k--;
        }
        return solution;
    }

    @Override
    public String getName() {
        return "PartialCol";
    }
}
