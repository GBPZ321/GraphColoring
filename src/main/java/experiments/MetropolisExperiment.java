package experiments;

import algorithms.MetropolisHeuristic;
import algorithms.enums.MetropolisSeedingStrategy;
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
            case "6":
                experimentSix();
                break;
            case "7":
                experimentSeven();
                break;
            case "8":
                experimentEight();
                break;
            case "9":
                experimentNine();
                break;
            case "10":
                experimentTen();
                break;
            case "11":
                experimentEleven();
                break;
            case "12":
                experimentTwelve();
                break;
            case "13":
                experimentThirteen();
                break;
            case "14":
                experimentFourteen();
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
            runMulithreadedExperiment(threads, k, filename, .95, 1, false, MetropolisSeedingStrategy.SIMPLE_ORDERED);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentThree() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Tighter bound metropolis [4] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .97, .99, true, MetropolisSeedingStrategy.SIMPLE_ORDERED);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFour() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Even tighter bound metropolis [5] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .98, .99, true, MetropolisSeedingStrategy.SIMPLE_ORDERED);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFive() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Looser bound metropolis [4] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, true, MetropolisSeedingStrategy.SIMPLE_ORDERED);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentSix() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Much looser bound metropolis [4] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .5, 1.5, true, MetropolisSeedingStrategy.SIMPLE_ORDERED);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }


    private static void experimentSeven() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] metropolis algorithm for various graphs with neighbor expansion.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, false, MetropolisSeedingStrategy.UNCOLORED_NEIGHBOR);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentEight() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Tighter bound metropolis [4] algorithm for various graphs with cooperation with neighbor expansion.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .97, .99, true, MetropolisSeedingStrategy.UNCOLORED_NEIGHBOR);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentNine() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Even tighter bound metropolis [5] algorithm for various graphs with cooperation with neighbor expansion.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .98, .99, true, MetropolisSeedingStrategy.UNCOLORED_NEIGHBOR);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentTen() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Looser bound metropolis [4] algorithm for various graphs with cooperation with neighbor expansion.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, true, MetropolisSeedingStrategy.UNCOLORED_NEIGHBOR);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }


    private static void experimentEleven() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = " Random Mulithreaded [8] metropolis algorithm for various graphs.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, false, MetropolisSeedingStrategy.RANDOM);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentTwelve() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Random Tighter bound metropolis [4] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .97, .99, true, MetropolisSeedingStrategy.RANDOM);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentThirteen() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Random Even tighter bound metropolis [5] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .98, .99, true, MetropolisSeedingStrategy.RANDOM);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFourteen() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Random Looser bound metropolis [4] algorithm for various graphs with cooperation.";
        System.out.println(experimentLabel);
        System.out.println(header);

        int threads = 8;

        List<String> files = getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .95, 1, true, MetropolisSeedingStrategy.RANDOM);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void runMulithreadedExperiment(int threads, List<Integer> k, String filename, double betaLower, double betaHigher, boolean cooperative, MetropolisSeedingStrategy seedingStrategy) throws IOException, InterruptedException, ExecutionException {
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
                thread = new MetropolisThread(testGraph, ITERATIONS, beta, threadCnt++, seedingStrategy, shared);
            } else {
                thread = new MetropolisThread(testGraph, ITERATIONS, beta, threadCnt++, seedingStrategy);
            }
            graphTrials.add(thread);
        }
        int minK = ExperimentCommon.findMinSoln(executorService, graphTrials);
        ExperimentCommon.printAndClose(k, executorService, properName, resourceAsStream, testGraph, minK);
    }


}
