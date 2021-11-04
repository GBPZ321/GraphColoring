import algorithms.TabucolHeuristic;
import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        GraphDefinition testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream(name), name);
        TabucolHeuristic heuristic = new TabucolHeuristic(testGraph);
        VertexColoringAlgorithm.Coloring<Integer> coloring = heuristic.getColoring();
        System.out.println(coloring.getNumberColors());
    }

}
