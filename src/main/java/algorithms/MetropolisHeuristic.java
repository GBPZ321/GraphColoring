package algorithms;

import algorithms.interfaces.ColoringHeuristic;
import algorithms.metropolis.MetropolisSubroutine;
import algorithms.random.SimpleOrderedColoring;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

public class MetropolisHeuristic implements ColoringHeuristic {
    private final GraphWrapper wrapper;
    private final double beta;
    private final int iterations;

    public MetropolisHeuristic(GraphDefinition definition, double beta, int iterations) {
        this.beta = beta;
        this.wrapper = definition.getGraphWrapper();
        this.iterations = iterations;
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
