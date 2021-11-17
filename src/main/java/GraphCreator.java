import algorithms.HCDHeuristic;
import algorithms.MetropolisHeuristic;
import algorithms.PartialColHeuristic;
import algorithms.TabucolHeuristic;
import algorithms.interfaces.ColoringHeuristic;
import algorithms.partialcol.PartialColSubroutine;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static utility.GraphHelperFunctions.sat;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        GraphDefinition testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream(name), name);
//        List<ColoringHeuristic> heuristics = Arrays.asList(new PartialColHeuristic(testGraph), new HCDHeuristic(testGraph.getGraphWrapper()), new TabucolHeuristic(testGraph));
        List<ColoringHeuristic> heuristics = Collections.singletonList(new MetropolisHeuristic(testGraph));
        for(ColoringHeuristic heuristic : heuristics) {
            GraphSolution solution = heuristic.getColoring();
            System.out.println(solution.getK());
            System.out.println(heuristic.getName());
            System.out.println(sat(testGraph, solution.getColoring()));
        }
//        PartialColHeuristic heuristic = new PartialColHeuristic(testGraph);
//        GraphSolution solution = heuristic.getColoring();
//        System.out.println("Test");
//        HCDHeuristic solver = new HCDHeuristic(testGraph.getGraphWrapper());
//        GraphSolution solution = solver.getColoring();
//        boolean satisfied = sat(testGraph, solution.getColoring());
//        System.out.println("Done? " + satisfied);

//        TabucolHeuristic heuristic = new TabucolHeuristic(testGraph);
//        GraphSolution coloring = heuristic.getColoring();
//        sat(testGraph, coloring.getColoring());
//        System.out.println(coloring.getK());
//        VertexColoringAlgorithm.Coloring<Integer> coloring = heuristic.getColoring(); // 31 colors



//        SaturationDegreeColoring<Integer, DefaultEdge> graphColoring = new SaturationDegreeColoring<>(testGraph.getGraphWrapper().getGraph()); // 38 colors
//        VertexColoringAlgorithm.Coloring<Integer> coloring = graphColoring.getColoring();
//        System.out.println(coloring.getNumberColors());
    }

}
