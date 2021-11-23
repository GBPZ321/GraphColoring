package algorithms.antcol;

import algorithms.interfaces.Subroutine;
import com.google.common.primitives.UnsignedLong;
import datastructures.Matrix;
import datastructures.SolutionMatrix;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AntColSubroutine implements Subroutine {

//    private final SolutionMatrix solutionMatrix;
    /**
     * The graph.
     */
    private final GraphDefinition graphDefinition;

    /**
     * Number of constraint checks to make.
     */
    private final long constraintChecks;

    /**
     * Number of times to use Tabucol.
     */
    private final Integer tabuIterations;

    /**
     * A random seed value.
     */
    private final Integer randomSeed;

    /**
     * Desired number of colors to find.
     */
    private final Integer targetColors;

    private long numberOfConstraintChecks = 0;

    private final Integer COLORED_TERMINAL = Integer.MIN_VALUE;

    private final Double alpha = 2.0;
    private final Double beta = 3.0;
    private final Double evaporationRate = 0.75;
    private Double solutionCost;
    private final Integer numberOfAnts = 10;
    private final Integer numberOfMultisets = 5;
    private Integer k;
    private Integer numberOfClashes;
    private boolean isFeasible = false;
    private ArrayList<Integer> bestFoundSolution;
    private ArrayList<Integer> currentSolution;

    private ArrayList<Integer> degrees;
    private Matrix localPheremoneDelta;

    public AntColSubroutine(GraphDefinition graphDefinition, Integer constraintChecks, Integer tabuIterations, Integer randomSeed, Integer targetColors) {
        Integer numberOfVertices = graphDefinition.getGraphWrapper().getVertexSize();
        this.graphDefinition = graphDefinition;
        this.k = numberOfVertices;
        this.constraintChecks = constraintChecks;
        this.tabuIterations = tabuIterations * numberOfVertices;
        this.randomSeed = randomSeed;
        this.targetColors = targetColors;
        this.degrees = new ArrayList<>();
        this.localPheremoneDelta = new Matrix(numberOfVertices, numberOfVertices);
    }

    @Override
    public SolutionWithStatus findSolution() {
        prepare();

        // Iterate until we reach our constraint checking threshold.
        while (numberOfConstraintChecks < constraintChecks) {
            resetLocalPheremoneDelta();

            // Iterate through the number of ants. We are attempting to seek a solution using k colors.
            for (int ant = 0; ant < numberOfAnts; ant++) {
                boolean foundSolution = buildSolution();
                break; // FIXME; remove
            }
        }

        return null;
    }

    private boolean buildSolution() {

        Integer numberColored = 0;

        ArrayList<ArrayList<Integer>> tempX = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tempY = new ArrayList<>();
        ArrayList<ArrayList<Integer>> independentSets = new ArrayList<>();

        // X keeps track of all nodes that have/haven't been assigned. It also holds the
        // degrees of the subgraph introduced by X. When a node is assigned a color,
        // its degree is set to `COLORED_TERMINAL`.
        ArrayList<Integer> X = new ArrayList<>(degrees);

        while (numberColored < graphDefinition.getGraphWrapper().getVertexSize()) {

            // Y holds all vertices that are NOT colored and which are suitable for the current color.
            // The set of uncolored nodes Y are those whose degrees are not equal to `COLORED_TERMINAL`.
            // For simplicity, we just consider values ≥ 0.
            ArrayList<Integer> Y = new ArrayList<>();
            for (int i = 0; i < X.size(); i++) {
                if (X.get(i) >= 0) {
                    Y.add(i);
                }
            }

            System.out.println("Number colored: " + numberColored);
            System.out.println("X: " + X);
            System.out.println("Y: " + Y);

            tempX.clear();
            tempY.clear();
            independentSets.clear();

            // Create copies of X and Y as placeholders for the independent sets
            // that will be produced.
            for (int i = 0; i < numberOfMultisets; i++) {
                tempX.add(X);
                tempY.add(Y);
            }

            for (int iset = 0; iset < numberOfMultisets; iset++) {
                Random random = new Random();
                ArrayList<Integer> updatedTempXISet = tempX.get(iset);
                ArrayList<Integer> updatedTempYISet = tempY.get(iset);

                System.out.println("tempXISet = " + updatedTempXISet);
                System.out.println("tempYISet = " + updatedTempYISet);

                // Pick a vertex at random from this independent set.
                Integer randomIndex = random.nextInt(updatedTempYISet.size()) + 1;
                randomIndex = 226 + 1;
                Integer vertex = updatedTempYISet.get(randomIndex);

                System.out.println("Random index = " + randomIndex);
                System.out.println("Vertex picked = " + vertex);

                // Update the independent set by removing adjacent nodes from this vertex.
                updatedTempYISet = removeAdjacentNodes(updatedTempYISet, vertex);
                System.out.println("updated tempYISet = " + updatedTempYISet);

                tempY.set(iset, updatedTempYISet);

                updatedTempXISet = updateX(updatedTempXISet, vertex);

                System.out.println("updated tempXISet = " + updatedTempXISet);

            }
        }

        return false;
    }

    /**
     * Removes any nodes in `Y` that are either adjacent to `vertex` or are the `vertex` itself.
     * @param Y A list of vertices.
     * @param vertex A vertex.
     * @return An updated `Y` with `vertex` removed.
     */
    private ArrayList<Integer> removeAdjacentNodes(ArrayList<Integer> Y, Integer vertex) {
        ArrayList<Integer> result = new ArrayList<>(Y);
        GraphWrapper graphWrapper = graphDefinition.getGraphWrapper();

        int index = 0;
        while (index < result.size()) {
            int currentVertex = result.get(index) + 1;
            numberOfConstraintChecks++;
            List<Integer> neighboringVertices = graphWrapper.getNeighborsOfV(currentVertex);

            // System.out.println("Y[" + index + "]: " + currentVertex + ", neighboringVertices: " + neighboringVertices);
            if ((neighboringVertices != null && neighboringVertices.contains(vertex)) || currentVertex == vertex) {
                // Set the current index equal to the last index, then remove the last item in the list.
                int lastIndex = result.size() - 1;
                result.set(index, result.get(lastIndex));
                result.remove(lastIndex);
            }
            else {
                index++;
            }
        }

        return result;
    }

    /**
     * Updates `X` by marking the vertex as colored.
     * @param X A list of vertices that have/haven't been assigned.
     * @param vertex A vertex.
     * @return An updated `X` with the color removed and its adjacent uncolored nodes reduced by one.
     */
    private ArrayList<Integer> updateX(ArrayList<Integer> X, Integer vertex) {
        ArrayList<Integer> result = new ArrayList<>(X);
        GraphWrapper graphWrapper = graphDefinition.getGraphWrapper();

        System.out.println("updateX: X prior to the update = " + X);

        // Mark the vertex as colored.
        // Result set is zero-based, hence why we subtract one here.
        // Neighbors are indexed starting at one.
        result.set(vertex - 1, COLORED_TERMINAL);
        numberOfConstraintChecks++;

        // Since it has been colored, we now need to check for any adjacent uncolored nodes
        // and have their degrees be reduced by one.
        for (Integer neighboringVertex : graphWrapper.getNeighborsOfV(vertex)) {
            numberOfConstraintChecks++;
            Integer neighborValue = X.get(neighboringVertex - 1);
            if (neighborValue != COLORED_TERMINAL) {
                result.set(neighboringVertex - 1, neighborValue - 1);
            }
        }

        System.out.println("updateX: Updated X = " + result);

        return result;
    }

    private void prepare() {
        createDegreesVector();
    }

    private void createDegreesVector() {
        for (Integer vertex : graphDefinition.getGraphWrapper().getVertices()) {
            Integer neighbors = graphDefinition.getGraphWrapper().getNeighborsOfV(vertex).size();
            this.degrees.add(neighbors);
        }
    }

    private void resetLocalPheremoneDelta() {
        Integer numberOfVertices = graphDefinition.getGraphWrapper().getVertexSize();
        this.localPheremoneDelta = new Matrix(numberOfVertices, numberOfVertices);
    }
}
