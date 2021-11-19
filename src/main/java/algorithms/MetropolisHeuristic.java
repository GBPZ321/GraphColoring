package algorithms;

import algorithms.interfaces.ColoringHeuristic;
import algorithms.metropolis.MetropolisSubroutine;
import algorithms.random.SimpleOrderedColoring;
import datastructures.common.Shared;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.Objects;

public class MetropolisHeuristic implements ColoringHeuristic {
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
        int k = wrapper.getVertexSize();
        GraphSolution coloring = null;
        while(k > 1) {
            MetropolisSubroutine subroutine = new MetropolisSubroutine(wrapper, iterations, new SimpleOrderedColoring(wrapper.getGraph(), k),  k, beta);

            SolutionWithStatus solutionWithStatus = subroutine.findSolution();
            if(solutionWithStatus.getStatus() == ColoringStatus.SATISFIED) {
                coloring = solutionWithStatus.getSolution();
                k--;
            } else if(solutionWithStatus.getStatus() == ColoringStatus.TIMEOUT) {
                if(Objects.isNull(shared)) {
                    return coloring;
                }
                int globalK = shared.getCurrentMinimum().getK();
                if(globalK < k) {
                    k = globalK;
                    continue; // New life because a cooperative friend is still running.
                }
                return coloring;
            }
        }
        return coloring;
    }

    @Override
    public String getName() {
        return "Metropolis";
    }
}
