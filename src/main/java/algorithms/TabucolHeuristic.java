package algorithms;

import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

public class TabucolHeuristic implements VertexColoringAlgorithm<Integer> {

    private final GraphDefinition graphDefinition;
    private Coloring<Integer> finalSolution;
    private static final Integer ITERATIONS = 500;

    public TabucolHeuristic(GraphDefinition graphDefinition) {
        this.graphDefinition = graphDefinition;
    }

    @Override
    public Coloring<Integer> getColoring() {
        int k = graphDefinition.getGraphWrapper().getVertexSize();
        while(k > 1) {
            TabucolSubroutine tabucolSubroutine = new TabucolSubroutine(graphDefinition, k, 1, 6, ITERATIONS);
            TabucolSolution possibleSolution = tabucolSubroutine.findSolution();
            if(possibleSolution.getStatus() == ColoringStatus.SATISFIED) {
                this.finalSolution = possibleSolution.getSolution();
            }
            if(possibleSolution.getStatus() == ColoringStatus.TIMEOUT) {
                return finalSolution;
            }
            k--;
        }
        return finalSolution;
    }

}
