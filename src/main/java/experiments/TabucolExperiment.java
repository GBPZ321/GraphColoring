package experiments;

import algorithms.TabucolHeuristic;
import datastructures.StatisticMatrix;
import datastructures.common.Shared;
import experiments.common.AdditionalConfiguration;
import experiments.common.ExperimentCommon;
import experiments.common.ColorConfiguration;
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
            runMulithreadedExperiment(threads, k, filename, .5, 1.1, 3, 10, ColorConfiguration.builder().configuration(AdditionalConfiguration.TABUCOL).cooperative(false).withMatrix(false).build());
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
            runMulithreadedExperiment(threads, k, filename, .5, 1.1,3,10, ColorConfiguration.builder().configuration(AdditionalConfiguration.TABUCOL).cooperative(true).withMatrix(false).build());
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFour() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs cooperative - tighter alpha, tighter L.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .7, 1,5,9, ColorConfiguration.builder().configuration(AdditionalConfiguration.TABUCOL).cooperative(true).withMatrix(false).build());
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentFive() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs cooperative w/matrix.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .7, 1,5,9, ColorConfiguration.builder().configuration(AdditionalConfiguration.TABUCOL).cooperative(true).withMatrix(true).build());
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentSix() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs cooperative w/matrix - tighter alpha.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .85, .95,3,12, ColorConfiguration.builder().configuration(AdditionalConfiguration.TABUCOL).cooperative(true).withMatrix(true).build());
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void experimentSeven() throws IOException, InterruptedException, ExecutionException {
        String experimentLabel = "Mulithreaded [8] tabucol algorithm for various graphs cooperative w/matrix - no tolerance.";
        ExperimentCommon.printLog(experimentLabel, header);

        int threads = 8;

        List<String> files = ExperimentCommon.getListOfGraphs();
        List<Integer> k = new ArrayList<>();
        for(String filename : files) {
            runMulithreadedExperiment(threads, k, filename, .9, .9,8,8, ColorConfiguration.builder().configuration(AdditionalConfiguration.TABUCOL).cooperative(true).withMatrix(true).build());
        }
        for(int kz : k) {
            System.out.println(kz);
        }
    }

    private static void runMulithreadedExperiment(int threads, List<Integer> k, String filename, double alphaLower, double alphaHigher, int lLower, int lHigher, ColorConfiguration configuration) throws IOException, InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        String properName = filename.replace(".col", "");
        InputStream resourceAsStream = ExperimentCommon.getStream(filename);
        GraphDefinition testGraph = reader.getGraph(resourceAsStream, properName);
        Shared shared = new Shared(testGraph.getGraphWrapper().getNumberOfVertices(), threads);
        StatisticMatrix matrix = new StatisticMatrix(testGraph.getGraphWrapper().getNumberOfVertices());
        List<TabucolThread> graphTrials = new ArrayList<>();
        List<Double> alpha;
        if(alphaHigher != alphaLower) {
            alpha = getNRandomDoublesInRange(alphaLower, alphaHigher, threads);
        } else {
            alpha = Collections.nCopies(threads, alphaHigher);
        }

        List<Integer> Ls;
        if(lHigher != lLower) {
             Ls = getNRandomIntsInRange(lLower, lHigher, threads);
        } else {
            Ls = Collections.nCopies(threads, lHigher);
        }
        int threadCnt = 1;
        for (int i = 0; i < alpha.size(); ++i) {
            TabucolThread thread;
            if(configuration.isCooperative() && configuration.isWithMatrix()) {
                thread = new TabucolThread(testGraph, Ls.get(i), alpha.get(i), threadCnt++, shared, matrix);
            } else if(configuration.isCooperative()) {
                thread = new TabucolThread(testGraph, Ls.get(i), alpha.get(i), threadCnt++, shared);
            } else {
                thread = new TabucolThread(testGraph, Ls.get(i), alpha.get(i), threadCnt++);
            }
            graphTrials.add(thread);
        }

        int minK = ExperimentCommon.findMinSoln(executorService,graphTrials);
        ExperimentCommon.printAndClose(k, executorService, properName, resourceAsStream, testGraph, minK);
    }


}
