import algorithms.HCDSubroutine;
import algorithms.TabucolHeuristic;
import graph.definition.GraphDefinition;
import org.jgrapht.alg.color.SaturationDegreeColoring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;

import static utility.GraphHelperFunctions.sat;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        GraphDefinition testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream(name), name);
        HCDSubroutine subroutine = new HCDSubroutine(testGraph.getGraphWrapper());
        subroutine.run();
        boolean satisfied = sat(testGraph, subroutine.getFinalColoring());
        System.out.println("Done? " + satisfied);

        //        TabucolHeuristic heuristic = new TabucolHeuristic(testGraph);
//        VertexColoringAlgorithm.Coloring<Integer> coloring = heuristic.getColoring(); // 31 colors



//        SaturationDegreeColoring<Integer, DefaultEdge> graphColoring = new SaturationDegreeColoring<>(testGraph.getGraphWrapper().getGraph()); // 38 colors
//        VertexColoringAlgorithm.Coloring<Integer> coloring = graphColoring.getColoring();
//        System.out.println(coloring.getNumberColors());
    }

}
