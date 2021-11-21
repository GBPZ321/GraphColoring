package algorithms.antcol;

import algorithms.TabucolHeuristic;
import algorithms.interfaces.Subroutine;
import datastructures.PheremoneMatrix;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final List<List<Integer>> solution;
    private List<List<Integer>> currentBestSolution;

    private ArrayList<Integer> degrees;
    private PheremoneMatrix globalTrailMatrix;
    private PheremoneMatrix localPheremoneDelta;

    public AntColSubroutine(GraphDefinition graphDefinition, Integer constraintChecks, Integer tabuIterations, Integer randomSeed, Integer targetColors) {
        Integer numberOfVertices = graphDefinition.getGraphWrapper().getVertexSize();
        this.graphDefinition = graphDefinition;
        this.k = numberOfVertices;
        this.constraintChecks = constraintChecks;
        this.tabuIterations = tabuIterations * numberOfVertices;
        this.randomSeed = randomSeed;
        this.targetColors = targetColors;
        this.degrees = new ArrayList<>();
        this.localPheremoneDelta = new PheremoneMatrix(numberOfVertices, numberOfVertices);
        this.globalTrailMatrix = new PheremoneMatrix(numberOfVertices, numberOfVertices);
        this.solution = Stream.generate(ArrayList<Integer>::new)
                .limit(k)
                .collect(Collectors.toList());
    }

    @Override
    public SolutionWithStatus findSolution() {
        prepare();

        // Iterate until we reach our constraint checking threshold.
        while (numberOfConstraintChecks < constraintChecks) {
            resetLocalPheremoneDelta();

            // Iterate through the number of ants. We are attempting to seek a solution using k colors.
            for (int ant = 0; ant < numberOfAnts; ant++) {
                boolean solutionIsFeasible = buildSolution();
                if (!solutionIsFeasible) {
                    // Run TabuCol.
                    TabucolHeuristic tabucolHeuristic = new TabucolHeuristic(graphDefinition);
                    GraphSolution tabucolSolution = tabucolHeuristic.getColoring();
                    // TODO: Need conflict count from TabuCol here.
                }

                // Create a solution cost based on feasibility or the number of conflicts from TabuCol.
                if (solutionIsFeasible) {
                    solutionCost = 3.0;
                }
                else {
                    solutionCost = 1.0 / 3.0; // TODO 1 / (number of TabuCol conflicts)
                }

                // Update the local pheremone delta by incrementing current values by the solution cost.
                for (int i = 0; i < solution.size(); i++) {
                    for (int j = 0; j < solution.get(i).size(); j++) {
                        for (int k = 0 ; k < solution.get(i).size(), k++) {
                            int rowIndex = solution.get(i).get(j);
                            int colIndex = solution.get(i).get(k);
                            double currentDeltaValue = localPheremoneDelta.getValue(rowIndex, colIndex);
                            localPheremoneDelta.setValue(rowIndex, colIndex, currentDeltaValue + solutionCost);

                            rowIndex = solution.get(i).get(k);
                            colIndex = solution.get(i).get(j);
                            currentDeltaValue = localPheremoneDelta.getValue(rowIndex, colIndex);
                            localPheremoneDelta.setValue(rowIndex, colIndex, currentDeltaValue + solutionCost);
                        }
                    }
                }

                // We have found a feasible solution using |solution| colors (|solution| ≤ k).
                if (solutionIsFeasible) {
                    if (solution.size() < currentBestSolution.size()) {
                        currentBestSolution = solution;
                    }

                    // Because we have found a feasible solution |solution| using k (or fewer) colors,
                    // we can leave this `nants` cycle and set k = |solution| - 1.
                    break;
                }
                
                // Additionally, break the cycle if we've exceeded the number of conflict checks.
                if (numberOfConstraintChecks >= constraintChecks) {
                    break;
                }
            }

            // We have found a solution that is less than or equal to the desired number of colors.
            if (currentBestSolution.size() <= targetColors) {
                break;
            }
            else {
                // Otherwise, update the global trail matrix and continue.
                // global trail matrix value = evaporation rate * current trail value + pheremone delta value.
                for (int i = 0; i < graphDefinition.getGraphWrapper().getNumberOfVertices(); i++) {
                    for (int j = 0; j < graphDefinition.getGraphWrapper().getNumberOfVertices(); j++) {
                        if (i != j) {
                            double currentValue = globalTrailMatrix.getValue(i, j);
                            globalTrailMatrix.setValue(i, j, evaporationRate * currentValue + localPheremoneDelta.getValue(i, j));
                        }
                    }
                }

                k = currentBestSolution.size() - 1;
            }
        }

        return null;
    }

    private boolean buildSolution() {

        boolean solutionIsComplete = true;
        Integer numberColored = 0;
        Integer colSolutionIndex = 0;

        ArrayList<ArrayList<Integer>> tempX = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tempY = new ArrayList<>();
        List<List<Integer>> independentSets = Stream.generate(ArrayList<Integer>::new)
                .limit(numberOfMultisets)
                .collect(Collectors.toList());
        ArrayList<Double> tauEta;
        Double tauEtaTotal;
        Double tau;
        Double eta;

        Random random = new Random();

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

            // Create copies of X and Y as placeholders for the independent sets
            // that will be produced.
            for (int i = 0; i < numberOfMultisets; i++) {
                tempX.add(new ArrayList<>(X));
                tempY.add(new ArrayList<>(Y));
                independentSets.get(i).clear();
            }

            for (int iset = 0; iset < numberOfMultisets; iset++) {
                ArrayList<Integer> updatedTempXISet = tempX.get(iset);
                ArrayList<Integer> updatedTempYISet = tempY.get(iset);

                System.out.println("tempXISet = " + updatedTempXISet);
                System.out.println("tempYISet = " + updatedTempYISet);

                // Pick a vertex at random from this independent set.
                Integer randomIndex = random.nextInt(updatedTempYISet.size()) + 1;
                Integer vertex = updatedTempYISet.get(randomIndex);
                independentSets.get(iset).add(vertex);

                System.out.println("Random index = " + randomIndex);
                System.out.println("Vertex picked = " + vertex);

                // Update the independent set by removing adjacent nodes from this vertex.
                updatedTempYISet = removeAdjacentNodes(updatedTempYISet, vertex);
                System.out.println("updated tempYISet = " + updatedTempYISet);
                tempY.set(iset, updatedTempYISet);

                updatedTempXISet = updateX(updatedTempXISet, vertex);
                System.out.println("updated tempXISet = " + updatedTempXISet);
                tempX.set(iset, updatedTempXISet);

                // Choose the remaining nodes in the current Y independent set based on the following:
                // 1. Pheremone (which is measured by `tau`), and
                // 2. The degrees of nodes in the current subgraph induced by the current X independent set.
                while (!updatedTempYISet.isEmpty()) {
                    tauEta = new ArrayList(Collections.nCopies(updatedTempYISet.size(), 0));
                    tauEtaTotal = 0.0;

                    for (int i = 0; i < updatedTempYISet.size(); i++) {
                        // Calculate tau (τ).
                        tau = 0.0;
                        List<Integer> independentSet = independentSets.get(iset);
                        for (int j = 0; j < independentSet.size(); j++) {
                            int rowIndex = independentSet.get(j) - 1;
                            int colIndex = updatedTempYISet.get(i);
                            tau += localPheremoneDelta.getValue(rowIndex, colIndex);
                        }

                        tau /= independentSet.size();
                        tau = Math.pow(tau, alpha);
                        System.out.println("tau = " + tau);

                        // Now, calculate eta (η) values.
                        numberOfConstraintChecks++;
                        eta = (double)updatedTempXISet.get(updatedTempYISet.get(i));
                        eta = Math.pow(eta, beta);
                        System.out.println("eta = " + eta);

                        // Finally, combine the two.
                        tauEta.set(i, tau * eta);
                        tauEtaTotal += tauEta.get(i);

                        System.out.println("tauEtaTotal = " + tauEtaTotal);
                    }

                    // After calculating our tau eta combos, select an element
                    // in this iset.
                    int selectedIndex = randomIndexUsingTauEta(tauEta, tauEtaTotal);
                    int selectedVertex = updatedTempYISet.get(selectedIndex) + 1;
                    independentSets.get(iset).add(selectedVertex);

                    updatedTempYISet = removeAdjacentNodes(updatedTempYISet, selectedVertex);
                    tempY.set(iset, updatedTempYISet);

                    updatedTempXISet = updateX(updatedTempXISet, selectedVertex);
                    tempX.set(iset, updatedTempXISet);

                }

                // At this point, we've created an appropriate number of independent sets.
                // Pick the one that results in the least number of edges in the remaining
                // uncolored vertices.
                int indexToSelect = chooseMinimumEdges(tempX);
                List<Integer> selectedISet = independentSets.get(indexToSelect);

                // Assign these vertices to S[col].
                numberColored += selectedISet.size();
                solution.set(colSolutionIndex, selectedISet);
                X = tempX.get(indexToSelect);

                colSolutionIndex++;
                if (colSolutionIndex == k && numberColored < graphDefinition.getGraphWrapper().getNumberOfVertices()) {
                    solutionIsComplete = false;
                    break;
                }
            }
        }

        // At this point, we have produced a (possibly partial) k-coloring.
        // Remove empty color classes.
        int i = 0;
        while (i < solution.size()) {
            if (solution.get(i).isEmpty()) {
                solution.remove(i);
            }
            else {
                i++;
            }
        }

        if (!solutionIsComplete) {
            // The solution is incomplete. Randomly assign uncolored nodes and return.
            for (i = 0; i < graphDefinition.getGraphWrapper().getNumberOfVertices(); i++) {
                if (X.get(i) >= 0) {
                    solution.get(random.nextInt(solution.size())).add(i);
                }
            }
            return false;
        }
        else {
            // We have found a feasible solution.
            return true;
        }
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

    private int randomIndexUsingTauEta(ArrayList<Double> tauEta, Double tauEtaTotal) {
        Random random = new Random();
        if (tauEta.size() == 1) { return 0; }
        if (tauEtaTotal == 0) { return random.nextInt(tauEta.size()); }

        // According to docs/C++ source, we simulate a "roulette" wheel
        // and pick a value at random.

        // Choose a value between [0, 1) and multiply against tauEtaTotal.
        // We then iterate through the list of tauEtas to see where this
        // value "lands" across the spectrum of values via bounds.
        double accumulator = 0.0;
        double selectedValue = ThreadLocalRandom.current().nextDouble(1.0) * tauEtaTotal;
        // selectedValue = 0.13153778808191419 * tauEtaTotal; // FIXME
        for (Double tauEtaValue : tauEta) {
            if (accumulator <= selectedValue && selectedValue <= (accumulator + tauEtaValue)) {
                return tauEta.indexOf(tauEtaValue);
            }
            else {
                accumulator += tauEtaValue;
            }
        }

        // This is an edge case where the value we're attempting to select at this point is
        // incredibly miniscule. This can be due to multiple reductions in tau etas due to
        // evaporation. Since it is not strictly 0, it's still worth selecting this value if it exists.
        for (Double tauEtaValue : tauEta) {
            if (tauEtaValue > 0.0) { return tauEta.indexOf(tauEtaValue); }
        }

        // Last resort: simply choose a value uniformly.
        return random.nextInt(tauEta.size());
    }

    private int chooseMinimumEdges(ArrayList<ArrayList<Integer>> tempX) {
        int minimumIndex = 0;
        int minimumDegrees = Integer.MAX_VALUE;
        for (int i = 0; i < tempX.size(); i++) {
            int totalDegrees = 0;
            for (int j = 0; j < tempX.get(i).size(); j++) {
                numberOfConstraintChecks++;
                int degree = tempX.get(i).get(j);
                if (degree >= 0) {
                    totalDegrees += degree;
                }
            }

            if (totalDegrees < minimumDegrees) {
                minimumDegrees = totalDegrees;
                minimumIndex = i;
            }
        }

        return minimumIndex;
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
        this.localPheremoneDelta = new PheremoneMatrix(numberOfVertices, numberOfVertices);
    }
}
