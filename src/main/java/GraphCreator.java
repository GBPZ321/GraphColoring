import algorithms.AntColHeuristic;
import algorithms.TabucolHeuristic;
import formatter.GraphOutputFormatter;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        String name = "graphs\\dsjc125.1.col";
        GraphDefinition testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream(name), name);

        TabucolHeuristic tabucolHeuristic = new TabucolHeuristic(testGraph);
        GraphSolution solution = tabucolHeuristic.getColoring();
        System.out.println(solution.getK());
        GraphOutputFormatter.writeGraphToFile(solution.getColoring(), testGraph);




    }

}
