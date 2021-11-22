package experiments;

import algorithms.TabucolHeuristic;
import datastructures.common.Shared;
import experiments.common.ExperimentCommon;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static algorithms.random.RandomUtils.getNRandomDoublesInRange;
import static algorithms.random.RandomUtils.getNRandomIntsInRange;

public class TabucolExperiment {
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
        }
        System.exit(0);
    }

    private static void experimentOne() throws IOException {
        String experimentLabel = "Standard tabucol algorithm for various graphs.";
        ExperimentCommon.printLog(experimentLabel, header);


        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            String properName = filename.replace(".col", "");
            InputStream resourceAsStream = ExperimentCommon.getStream(filename);
            GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
            TabucolHeuristic heuristic = new TabucolHeuristic(testGraph);
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
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .5, 1.1, 3, 10, false);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentThree() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs cooperative.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .5, 1.1,3,10, true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFour() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs cooperative.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .7, 1,5,9, true);
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void runMulithreadedExperiment(int threads, List<Integer> k, String filename, double alphaLower, double alphaHigher, int lLower, int lHigher, boolean cooperative) throws IOException, InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        String properName = filename.replace(".col", "");
        InputStream resourceAsStream = ExperimentCommon.getStream(filename);
        GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
        Shared shared = new Shared(testGraph.getGraphWrapper().getNumberOfVertices(), threads);
        List<TabucolThread> graphTrials = new ArrayList<>();
        List<Double> alpha = getNRandomDoublesInRange(alphaLower, alphaHigher, threads);
        List<Integer> Ls = getNRandomIntsInRange(lLower, lHigher, threads);
        int threadCnt = 1;
        for (int i = 0; i < alpha.size(); ++i) {
            TabucolThread thread;
            if(cooperative) {
                thread = new TabucolThread(testGraph, Ls.get(i), alpha.get(i), threadCnt++, shared);
            } else {
                thread = new TabucolThread(testGraph, Ls.get(i), alpha.get(i), threadCnt++);
            }
            graphTrials.add(thread);
        }
        List<Future<GraphSolution>> solutions = executorService.invokeAll(graphTrials);
        int minK = Integer.MAX_VALUE;
        for (Future<GraphSolution> solutionFuture : solutions) {
            GraphSolution solution = solutionFuture.get();
            if(solution == null) {
                // Thread did no useful work.
                continue;
            }
            minK = Math.min(solutionFuture.get().getK(), minK);
        }

        System.out.printf("%s,", properName);
        System.out.printf("%d,%d,%d\n", testGraph.getGraphWrapper().getNumberOfVertices(), testGraph.getGraphWrapper().getEdgeCount(), minK);
        k.add(minK);
        resourceAsStream.close();
        executorService.shutdownNow();
    }
}
