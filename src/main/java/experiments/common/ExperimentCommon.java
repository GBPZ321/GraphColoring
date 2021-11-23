package experiments.common;

import experiments.MetropolisExperiment;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ExperimentCommon {
    private static final String graphsDir = "graphs/";
    public static void printLog(String experimentLabel, String header) {
        System.out.println(experimentLabel);
        System.out.println(header);
    }

    public static List<String> getListOfGraphs() throws IOException {
        return IOUtils.readLines(Objects.requireNonNull(ExperimentCommon.class.getClassLoader().getResourceAsStream(graphsDir)), StandardCharsets.UTF_8);
    }

    public static InputStream getStream(String filename) {
        return ExperimentCommon.class.getClassLoader().getResourceAsStream(graphsDir + filename);
    }

    public static <T extends Callable<GraphSolution>> int findMinSoln(ExecutorService service, List<T> threads)  throws ExecutionException, InterruptedException {
        List<Future<GraphSolution>> solutions = service.invokeAll(threads);
        int minK = Integer.MAX_VALUE;
        for (Future<GraphSolution> solutionFuture : solutions) {
            GraphSolution solution = solutionFuture.get();
            if(solution == null) {
                // Thread did no useful work.
                continue;
            }
            minK = Math.min(solutionFuture.get().getK(), minK);
        }
        return minK;
    }

    public static void printAndClose(List<Integer> k, ExecutorService executorService, String properName, InputStream resourceAsStream, GraphDefinition testGraph, int minK) throws IOException {
        System.out.printf("%s,", properName);
        System.out.printf("%d,%d,%d\n", testGraph.getGraphWrapper().getNumberOfVertices(), testGraph.getGraphWrapper().getEdgeCount(), minK);
        k.add(minK);
        resourceAsStream.close();
        executorService.shutdownNow();
    }



}
