package algorithms.metropolis;

import algorithms.interfaces.ColoringHeuristic;
import algorithms.interfaces.SeedingStrategy;
import algorithms.interfaces.Subroutine;
import algorithms.random.SimpleOrderedColoring;
import datastructures.SolutionMatrix;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class MetropolisSubroutine implements Subroutine {
    private final GraphWrapper graphWrapper;
    private final int iterations;
    private final int k;
    private final Map<Integer, Integer> coloring;
    private final SolutionMatrix matrix;
    private static final Random rnd = new Random();
    private final double beta;

    public MetropolisSubroutine(GraphWrapper graphWrapper, int iterations, SeedingStrategy seedingStrategy, int k, double beta) {
        this.graphWrapper = graphWrapper;
        this.iterations = iterations;
        this.k = k;
        this.coloring = seedingStrategy.getStartingColoring();
        this.matrix = new SolutionMatrix(coloring, k, graphWrapper);
        this.beta = beta;
    }

    @Override
    public SolutionWithStatus findSolution() {
        int runs = iterations;
        List<Integer> vertices = new ArrayList<>(graphWrapper.getVertices());

        while(runs-- > 0) {
            int conflictNumber = matrix.getConflictNumber();
            if(conflictNumber == 0) {
                return new SolutionWithStatus(ColoringStatus.SATISFIED, new GraphSolution(coloring, k));
            }
            int sz = graphWrapper.getVertexSize();
            int v = vertices.get(rnd.nextInt(sz));
            int currColor = coloring.get(v);
            int nextColor = -1;
            while((nextColor = rnd.nextInt(k)) == currColor);
            int performance = matrix.getMovePerformance(v, currColor, nextColor);
            double probability = Math.exp(-beta * performance);
            if(performance >= 0) {
                coloring.put(v, nextColor);
                matrix.updateSolution(v, currColor, nextColor);
            } else if(rnd.nextDouble() > probability){
                coloring.put(v, nextColor);
                matrix.updateSolution(v, currColor, nextColor);
            }
            // NO-OP.
        }

        if(matrix.getConflictNumber() == 0) {
            return new SolutionWithStatus(ColoringStatus.SATISFIED, new GraphSolution(coloring, k));
        } else {
            return new SolutionWithStatus(ColoringStatus.TIMEOUT, null);
        }
    }
}
