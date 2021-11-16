import algorithms.HCDHeuristic;
import algorithms.PartialColHeuristic;
import algorithms.partialcol.PartialColSubroutine;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;

import static utility.GraphHelperFunctions.sat;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        GraphDefinition testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream(name), name);
        PartialColHeuristic heuristic = new PartialColHeuristic(testGraph);
        GraphSolution solution = heuristic.getColoring();
        System.out.println("Test");
//        HCDHeuristic solver = new HCDHeuristic(testGraph.getGraphWrapper());
//        GraphSolution solution = solver.getColoring();
//        boolean satisfied = sat(testGraph, solution.getColoring());
//        System.out.println("Done? " + satisfied);

        //        TabucolHeuristic heuristic = new TabucolHeuristic(testGraph);
//        VertexColoringAlgorithm.Coloring<Integer> coloring = heuristic.getColoring(); // 31 colors



//        SaturationDegreeColoring<Integer, DefaultEdge> graphColoring = new SaturationDegreeColoring<>(testGraph.getGraphWrapper().getGraph()); // 38 colors
//        VertexColoringAlgorithm.Coloring<Integer> coloring = graphColoring.getColoring();
//        System.out.println(coloring.getNumberColors());
    }

}
