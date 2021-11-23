package experiments;

import algorithms.MetropolisHeuristic;
import datastructures.common.Shared;
import experiments.common.ExperimentCommon;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import org.apache.commons.io.IOUtils;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static algorithms.random.RandomUtils.getNRandomDoublesInRange;

public class MetropolisExperiment {
    private static final String header = "Graph Name, Vertices, Edges, Coloring";
    private static final GraphReader reader = new DimacsReader();
    private static final int ITERATIONS = 20000;
    private static final String graphsDir = "graphs/";

    private static List<String> getListOfGraphs() throws IOException {
        List<String> files = IOUtils.readLines(Objects.requireNonNull(MetropolisExperiment.class.getClassLoader().getResourceAsStream(graphsDir)), StandardCharsets.UTF_8);
        return files;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        switch (args[0]) {
            case "1":
                experimentOne();
                break;
            case "2":
                experimentTwo();
                break;
            case "3":
                experimentThree();
                break;
            case "4":
                experimentFour();
                break;
            case "5":
                experimentFive();
                break;
        }
        System.exit(0);
    }

    private static void experimentOne() throws IOException {
        String experimentLabel = "Standard metropolis algorithm for various graphs.";
        ExperimentCommon.printLog(experimentLabel, header);


        double STANDARD_BETA = .98;
        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            String properName = filename.replace(".col", "");
            InputStream resourceAsStream = MetropolisExperiment.class.getClassLoader().getResourceAsStream(graphsDir + filename);
            GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
            MetropolisHeuristic heuristic = new MetropolisHeuristic(testGraph, STANDARD_BETA, ITERATIONS);
            System.out.printf("%s,", properName);
            GraphSolution solution = heuristic.getColoring();
            k.add(solution.getK());
            System.out.printf("%d,%d,%d\n", testGraph.getGraphWrapper().getNumberOfVertices(), testGraph.getGraphWrapper().getEdgeCount(), solution.getK());
            resourceAsStream.close();
        }

        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentTwo() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] metropolis algorithm for various graphs.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, false);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentThree() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Tighter bound metropolis [4] algorithm for various graphs.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .97, .99, true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFour() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Tighter bound metropolis [5] algorithm for various graphs.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .98, .99, true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFive() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Tighter bound metropolis [4] algorithm for various graphs.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void runMulithreadedExperiment(int threads, List<Integer> k, String filename, double betaLower, double betaHigher, boolean cooperative) throws IOException, InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        String properName = filename.replace(".col", "");
        InputStream resourceAsStream = MetropolisExperiment.class.getClassLoader().getResourceAsStream(graphsDir + filename);
        GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
        Shared shared = new Shared(testGraph.getGraphWrapper().getNumberOfVertices(), threads);
        List<MetropolisThread> graphTrials = new ArrayList<>();
        List<Double> parameters = getNRandomDoublesInRange(betaLower, betaHigher, threads);
        int threadCnt = 1;
        for (Double beta : parameters) {
            MetropolisThread thread;
            if(cooperative) {
                thread = new MetropolisThread(testGraph, ITERATIONS, beta, threadCnt++, shared);
            } else {
                thread = new MetropolisThread(testGraph, ITERATIONS, beta, threadCnt++);
            }
            graphTrials.add(thread);
        }
        int minK = ExperimentCommon.findMinSoln(executorService, graphTrials);
        ExperimentCommon.printAndClose(k, executorService, properName, resourceAsStream, testGraph, minK);
    }


}
