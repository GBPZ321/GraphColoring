package experiments;

import algorithms.MetropolisHeuristic;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class MetropolisExperiment {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        System.out.println("Standard metropolis algorithm for various graphs.");
        String graphsDir = "graphs/";
        List<String> files = IOUtils.readLines(Objects.requireNonNull(MetropolisExperiment.class.getClassLoader().getResourceAsStream(graphsDir)), StandardCharsets.UTF_8);
        int STANDARD_METRO_TIMEOUT = 10000;
        double STANDARD_BETA = .98;


        for(String filename : files) {
            String properName = filename.replace(".col", "");
            GraphDefinition testGraph = reader.getGraph(MetropolisExperiment.class.getClassLoader().getResourceAsStream(graphsDir + filename), properName);
            MetropolisHeuristic heuristic = new MetropolisHeuristic(testGraph, STANDARD_BETA, STANDARD_METRO_TIMEOUT);
            System.out.printf("Name :%s\t", properName);
            GraphSolution solution = heuristic.getColoring();
            System.out.printf("K: %d\n", solution.getK());
        }
    }
}
