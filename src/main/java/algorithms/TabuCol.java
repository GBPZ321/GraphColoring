package algorithms;

import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import utility.GraphHelperFunctions;
import utility.Satisfies;

public class TabuCol implements VertexColoringAlgorithm<Integer> {

    private final Integer startingK;
    private final GraphDefinition graphDefinition;
    private static final Integer TIMEOUT = 10000; // 10s

    public TabuCol(Integer startingK, GraphDefinition graphDefinition) {
        this.startingK = startingK;
        this.graphDefinition = graphDefinition;
    }

    @Override
    public Coloring<Integer> getColoring() {
        RandomInitialColoring coloring = new RandomInitialColoring(graphDefinition.getGraph(), startingK);
        Coloring<Integer> coloringArray = coloring.getColoring();
        int i = 0;
        long startTime = System.currentTimeMillis();
        while(viable(startTime, coloringArray)) {
            // Check adjacent colorings
            // If coloring is better, swap.
            // Keep going etc.
        }
        return null;
    }

    private boolean viable(long startTime, Coloring<Integer> coloringArray) {
        if(System.currentTimeMillis() - startTime > TIMEOUT) return false;
        Satisfies sat = GraphHelperFunctions.satisfies(graphDefinition, coloringArray);
        return sat.getErrorCount() > 0;
    }
}
