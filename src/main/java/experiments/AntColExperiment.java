package experiments;

import algorithms.AntColHeuristic;
import datastructures.common.Shared;
import experiments.common.ExperimentCommon;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static algorithms.random.RandomUtils.getNRandomDoublesInRange;

public class AntColExperiment {
    private static final String header = "Graph Name, Vertices, Edges, Coloring";
    private static final GraphReader reader = new DimacsReader();

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
        }
        System.exit(0);
    }

    private static void experimentOne() throws IOException {
        String experimentLabel = "Standard antcol algorithm for various graphs.";
        ExperimentCommon.printLog(experimentLabel, header);


        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            String properName = filename.replace(".col", "");
            InputStream resourceAsStream = ExperimentCommon.getStream(filename);
            GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
            AntColHeuristic heuristic = new AntColHeuristic(testGraph, false);
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
        String experimentLabel = "Mulithreaded [8] antcol algorithm for various graphs (evap rate: 0.6 ≤ x ≤ 1.2).";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for (String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .60, 1.20, 10,false);
        }
        for (int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentThree() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] antcol algorithm for various graphs, cooperative (evap rate: 0.6 ≤ x ≤ 1.2).";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .60, 1.20, 10,true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFour() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] antcol algorithm for various graphs (evap rate: 0.15 ≤ x ≤ 0.6).";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for (String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .15, .60, 10, false);
        }
        for (int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFive() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] antcol algorithm for various graphs, cooperative (evap rate: 0.25 ≤ x ≤ 0.75).";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .25, .75, 10,true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentSix() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] antcol algorithm for various graphs (evap rate: 0.75, nants: 20).";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .75, .75, 20,false);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentSeven() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] antcol algorithm for various graphs cooperative (evap rate: 0.75, nants: 5).";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .75, .75, 20,true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void runMulithreadedExperiment(int threads, List<Integer> k, String filename, double evapRateLower, double evapRateHigher, int numberOfAnts, boolean cooperative) throws IOException, InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        String properName = filename.replace(".col", "");
        InputStream resourceAsStream = ExperimentCommon.getStream(filename);
        GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
        Shared shared = new Shared(testGraph.getGraphWrapper().getNumberOfVertices(), threads);
        List<AntColThread> graphTrials = new ArrayList<>();
        List<Double> evaporationRates;
        if(evapRateHigher != evapRateLower) {
            evaporationRates = getNRandomDoublesInRange(evapRateLower, evapRateHigher, threads);
        } else {
            evaporationRates = Collections.nCopies(threads, evapRateHigher);
        }

        int threadCnt = 1;
        for (int i = 0; i < evaporationRates.size(); ++i) {
            AntColThread thread;
            if (cooperative) {
                thread = new AntColThread(testGraph, threadCnt++, evaporationRates.get(i), numberOfAnts, shared);
            } else {
                thread = new AntColThread(testGraph, threadCnt++, evaporationRates.get(i), numberOfAnts);
            }
            graphTrials.add(thread);
        }

        int minK = ExperimentCommon.findMinSoln(executorService, graphTrials);
        ExperimentCommon.printAndClose(k, executorService, properName, resourceAsStream, testGraph, minK);
    }


}
