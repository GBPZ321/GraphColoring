package algorithms;

import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

public class AntColHeuristic implements VertexColoringAlgorithm<Integer> {

    private final GraphDefinition graphDefinition;
    private Coloring<Integer> finalSolution;

    public AntColHeuristic(GraphDefinition graphDefinition) {
        this.graphDefinition = graphDefinition;
    }

    @Override
    public Coloring<Integer> getColoring() {
        return finalSolution;
    }
}
