package algorithms;

import algorithms.enums.MetropolisSeedingStrategy;
import algorithms.interfaces.ColoringHeuristic;
import algorithms.interfaces.SeedingStrategy;
import algorithms.metropolis.MetropolisSubroutine;
import algorithms.random.CompletelyRandomColoring;
import algorithms.random.SimpleOrderedColoring;
import algorithms.random.UnassignedNeighborColoring;
import datastructures.common.Shared;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

public class MetropolisHeuristic extends BaseCooperative implements ColoringHeuristic {
    private final GraphWrapper wrapper;
    private final double beta;
    private final int iterations;
    private final Shared shared;

    public MetropolisHeuristic(GraphDefinition definition, double beta, int iterations) {

        this.beta = beta;
        this.wrapper = definition.getGraphWrapper();
        this.iterations = iterations;
        this.shared = null;
    }

    public MetropolisHeuristic(GraphDefinition definition, double beta, int iterations, Shared shared) {
        this.beta = beta;
        this.wrapper = definition.getGraphWrapper();
        this.iterations = iterations;
        this.shared = shared;
    }

    @Override
    public GraphSolution getColoring() {
        return getColoring(MetropolisSeedingStrategy.SIMPLE_ORDERED);
    }

    public GraphSolution getColoring(MetropolisSeedingStrategy seedingStrategy) {
        int k = wrapper.getVertexSize();
        GraphSolution coloring = null;
        while(k > 1) {
            System.out.println("K = " + k);
            if(isGlobalKLower(k)) {
                k = shared.getCurrentMinimum().getK();
                k--;
                continue;
            }
            SeedingStrategy ss;
            switch(seedingStrategy){
                case SIMPLE_ORDERED:
                    ss = new SimpleOrderedColoring(wrapper.getGraph(), k);
                    break;
                case UNCOLORED_NEIGHBOR:
                    ss = new UnassignedNeighborColoring(wrapper.getGraph(), k);
                    break;
                case RANDOM:
                    ss = new CompletelyRandomColoring(wrapper, k);
                    break;
                default:
                    continue;
            }

            MetropolisSubroutine subroutine = new MetropolisSubroutine(wrapper, iterations, ss,  k, beta);

            SolutionWithStatus solutionWithStatus = subroutine.findSolution();
            if(solutionWithStatus.getStatus() == ColoringStatus.SATISFIED) {
                coloring = solutionWithStatus.getSolution();
                updateSolution(solutionWithStatus.getSolution());
            } else if(isGlobalKLower(k)) {
                k = shared.getCurrentMinimum().getK();
            } else if(solutionWithStatus.getStatus() == ColoringStatus.TIMEOUT && isFinished(k)) {
                return coloring;
            } else {
                k = shared.getCurrentMinimum().getK();
            }
            k--;
        }
        return coloring;
    }

    @Override
    public String getName() {
        return "Metropolis";
    }
}
