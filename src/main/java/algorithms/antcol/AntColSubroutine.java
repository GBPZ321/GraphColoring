package algorithms.antcol;

import algorithms.TabucolHeuristic;
import algorithms.interfaces.Subroutine;
import datastructures.PheremoneMatrix;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AntColSubroutine implements Subroutine {

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
    private List<List<Integer>> solution;
    private List<List<Integer>> currentBestSolution;

    private ArrayList<Integer> degrees;
    private final PheremoneMatrix globalTrailMatrix;
    private PheremoneMatrix localPheremoneDelta;

    private final boolean verbose;

    public AntColSubroutine(GraphDefinition graphDefinition, Integer k, Integer constraintChecks, Integer tabuIterations, Integer randomSeed, Integer targetColors, boolean verbose) {
        Integer numberOfVertices = graphDefinition.getGraphWrapper().getVertexSize();
        this.graphDefinition = graphDefinition;
        this.k = k;
        this.constraintChecks = constraintChecks;
        this.tabuIterations = tabuIterations * numberOfVertices;
        this.randomSeed = randomSeed;
        this.targetColors = targetColors;
        this.degrees = new ArrayList<>();
        this.localPheremoneDelta = new PheremoneMatrix(numberOfVertices, numberOfVertices);
        this.globalTrailMatrix = new PheremoneMatrix(numberOfVertices, numberOfVertices);
        this.verbose = verbose;
    }

    @Override
    public SolutionWithStatus findSolution() {

        SolutionWithStatus solutionWithStatus = new SolutionWithStatus();
        prepare();

        // Iterate until we reach our constraint checking threshold.
        while (numberOfConstraintChecks < constraintChecks) {

            printDebug("[k=" + k + "] Number of constraint checks remaining: " + (constraintChecks - numberOfConstraintChecks));
            resetLocalPheremoneDelta();

            // Iterate through the number of ants. We are attempting to seek a solution using k colors.
            for (int ant = 0; ant < numberOfAnts; ant++) {

                printDebug("[k=" + k + "] Running ant " + (ant + 1) + " out of " + numberOfAnts + " ants.");

                // Construct an empty solution using a maximum of k colors.
                resetSolution();
                
                boolean solutionIsFeasible = buildSolution();
                if (!solutionIsFeasible) {
                    // Run TabuCol.
                    printDebug("[k=" + k + "] Solution is incomplete! Running TabuCol to try to create a feasible solution...");
                    TabucolHeuristic tabucolHeuristic = new TabucolHeuristic(graphDefinition, k, tabuIterations, 0.6f, 8);
                    GraphSolution tabucolSolution = tabucolHeuristic.getColoring();
                    // TODO: Need conflict count from TabuCol here.

                    // If a solution has been found, we mark it as feasible.
                    if (tabucolSolution != null && tabucolSolution.getColoring() != null) {
                        solutionIsFeasible = true;
                        solution = antColSolutionValue(tabucolSolution.getColoring(), tabucolSolution.getK());
                    }
                }

                // Create a solution cost based on feasibility or the number of conflicts from TabuCol.
                if (solutionIsFeasible) {
                    solutionCost = 3.0;
                }
                else {
                    solutionCost = 1.0 / 3.0; // TODO 1 / (number of TabuCol conflicts)
                }

                // Update the local pheremone delta by incrementing current values by the solution cost.
                updateLocalPheremoneValues(solutionCost);

                // We have found a feasible solution using |solution| colors (|solution| ≤ k).
                if (solutionIsFeasible) {
                    printDebug("[k=" + k + "] Feasible solution found for k=" + solution.size() + ".");
                    if (currentBestSolution == null || solution.size() < currentBestSolution.size()) {
                        currentBestSolution = solution;
                    }

                    // Because we have found a feasible solution |solution| using k (or fewer) colors,
                    // we can leave this `nants` cycle and set k = |solution| - 1.
                    break;
                }
                
                // Additionally, break the cycle if we've exceeded the number of conflict checks.
                if (numberOfConstraintChecks >= constraintChecks) {
                    printDebug("[k=" + k + "] Exceeded number of constraint checks at " + numberOfConstraintChecks + " (limit: " + constraintChecks + ").");
                    break;
                }
            }

            // We have found a solution that is less than or equal to the desired number of colors.
            if (currentBestSolution.size() <= targetColors) {
                printDebug("[k=" + k + "] Solution found that is ≤ target value for colors.");
                break;
            }
            else {
                // Otherwise, update the global trail matrix and continue.
                // global trail matrix value = evaporation rate * current trail value + pheremone delta value.
                updateGlobalTrailMatrix();
                k = currentBestSolution.size() - 1;

                printDebug("[k=" + k + "] ------------------------------------ Preparing for next iteration... ------------------------------------");
            }
        }

        // If we have a solution, let's remap the solution appropriately.
        // `solution` has a list of vertices per each color. Here, we
        // simply map each node to its value for `GraphSolution`.
        if (currentBestSolution != null) {
            GraphSolution graphSolution = new GraphSolution();
            Map<Integer, Integer> currentColoring = solutionMap(currentBestSolution);

            graphSolution.setColoring(currentColoring);
            graphSolution.setK(currentBestSolution.size());

            solutionWithStatus.setSolution(graphSolution);
            solutionWithStatus.setStatus(ColoringStatus.SATISFIED);
        }
        else {
            solutionWithStatus.setStatus(ColoringStatus.TIMEOUT);
        }

        return solutionWithStatus;
    }

    private Map<Integer, Integer> solutionMap(List<List<Integer>> solution) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < currentBestSolution.size(); i++) {
            List<Integer> coloredVertices = currentBestSolution.get(i);
            for (Integer vertex : coloredVertices) {
                map.put(vertex, i);
            }
        }

        return map;
    }

    private List<List<Integer>> antColSolutionValue(Map<Integer, Integer> map, Integer colors) {
        List<List<Integer>> solutionValue = Stream.generate(ArrayList<Integer>::new)
                .limit(colors)
                .collect(Collectors.toList());

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            solutionValue.get(entry.getValue()).add(entry.getKey());
        }

        return solutionValue;
    }

    private boolean buildSolution() {

        boolean solutionIsComplete = true;
        Integer numberColored = 0;
        Integer colSolutionIndex = 0;

        List<List<Integer>> tempX = new ArrayList<>();
        List<List<Integer>> tempY = new ArrayList<>();
        List<List<Integer>> independentSets = Stream.generate(ArrayList<Integer>::new)
                .limit(numberOfMultisets)
                .collect(Collectors.toList());
        List<Double> tauEta;
        Double tauEtaTotal;
        Double tau;
        Double eta;

        Random random = new Random();

        // X keeps track of all nodes that have/haven't been assigned. It also holds the
        // degrees of the subgraph introduced by X. When a node is assigned a color,
        // its degree is set to `COLORED_TERMINAL`.
        List<Integer> X = new ArrayList<>(degrees);

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

            // printDebug("Number colored: " + numberColored);

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
                List<Integer> updatedTempXISet = tempX.get(iset);
                List<Integer> updatedTempYISet = tempY.get(iset);

                // printDebug("tempXISet = " + updatedTempXISet);
                // printDebug("tempYISet = " + updatedTempYISet);

                // Pick a vertex at random from this independent set.
                Integer randomIndex = random.nextInt(updatedTempYISet.size());
                Integer vertex = updatedTempYISet.get(randomIndex) + 1;
                independentSets.get(iset).add(vertex);

                // printDebug("Random index = " + randomIndex);
                // printDebug("Vertex picked = " + vertex);

                // Update the independent set by removing adjacent nodes from this vertex.
                updatedTempYISet = removeAdjacentNodes(updatedTempYISet, vertex);
                // printDebug("updated tempYISet = " + updatedTempYISet);
                tempY.set(iset, updatedTempYISet);

                updatedTempXISet = updateX(updatedTempXISet, vertex);
                // printDebug("updated tempXISet = " + updatedTempXISet);
                tempX.set(iset, updatedTempXISet);

                // Choose the remaining nodes in the current Y independent set based on the following:
                // 1. Pheremone (which is measured by τ), and
                // 2. The degrees of nodes in the current subgraph induced by the current X independent set.
                while (!updatedTempYISet.isEmpty()) {
                    tauEta = new ArrayList(Collections.nCopies(updatedTempYISet.size(), 0.0));
                    tauEtaTotal = 0.0;

                    for (int i = 0; i < updatedTempYISet.size(); i++) {
                        // Calculate tau (τ).
                        tau = 0.0;
                        List<Integer> independentSet = independentSets.get(iset);
                        for (int j = 0; j < independentSet.size(); j++) {
                            int rowIndex = independentSet.get(j) - 1;
                            int colIndex = updatedTempYISet.get(i);
                            tau += globalTrailMatrix.getValue(rowIndex, colIndex);
                        }

                        tau /= independentSet.size();
                        tau = Math.pow(tau, alpha);
                        // printDebug("τ = " + tau);

                        // Now, calculate eta (η) values.
                        numberOfConstraintChecks++;
                        eta = (double)updatedTempXISet.get(updatedTempYISet.get(i));
                        eta = Math.pow(eta, beta);
                        // printDebug("η = " + eta);

                        // Finally, combine the two.
                        tauEta.set(i, tau * eta);
                        tauEtaTotal += tauEta.get(i);

                        // printDebug("tauEtaTotal = " + tauEtaTotal);
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
            }

            // At this point, we've created an appropriate number of independent coloring sets.
            // Pick the one that results in the least number of edges in the remaining uncolored vertices.
            int indexToSelect = chooseMinimumEdges(tempX);
            List<Integer> selectedISet = new ArrayList<>(independentSets.get(indexToSelect));

            // Assign these vertices to the current coloring index.
            numberColored += selectedISet.size();
            solution.set(colSolutionIndex, selectedISet);
            X = tempX.get(indexToSelect);

            colSolutionIndex++;
            // printDebug("iSet[r].size() = " + selectedISet.size() + ", numColored = " + numberColored + ", g.n = " + graphDefinition.getGraphWrapper().getNumberOfVertices());

            // If we've hit the current desired number of colors and we haven't colored the full graph,
            // we know this solution is incomplete. Break.
            if (colSolutionIndex == k && numberColored < graphDefinition.getGraphWrapper().getNumberOfVertices()) {
                solutionIsComplete = false;
                break;
            }
        }

        // At this point, we have produced a (possibly partial) k-coloring.
        // Remove empty color classes.
        removeEmptyColorings();

        if (!solutionIsComplete) {
            // The solution is incomplete. Randomly assign uncolored nodes and return.
            randomlyAssignColorsForIncompleteSolution(X);
            return false;
        }
        else {
            // We've found a feasible solution! 🎉
            return true;
        }
    }

    /**
     * Removes any nodes in `Y` that are either adjacent to `vertex` or are the `vertex` itself.
     * @param Y A list of vertices.
     * @param vertex A vertex.
     * @return An updated `Y` with `vertex` removed.
     */
    private List<Integer> removeAdjacentNodes(List<Integer> Y, Integer vertex) {
        List<Integer> result = new ArrayList<>(Y);
        GraphWrapper graphWrapper = graphDefinition.getGraphWrapper();

        int index = 0;
        while (index < result.size()) {
            int currentVertex = result.get(index) + 1;
            numberOfConstraintChecks++;

            List<Integer> neighboringVertices = graphWrapper.getNeighborsOfV(currentVertex);

            // System.out.print("Y[" + index + "]: " + currentVertex + ", g[Y[i]][v]: " + neighboringVertices.contains(vertex) + ", v: " + vertex);
            if ((neighboringVertices != null && neighboringVertices.contains(vertex)) || currentVertex == vertex) {
                // Set the current index equal to the last index, then remove the last item in the list.
                int lastIndex = result.size() - 1;
                result.set(index, result.get(lastIndex));
                result.remove(lastIndex);
                // printDebug(" = removed");
            }
            else {
                index++;
                // printDebug(" = saved");
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
    private List<Integer> updateX(List<Integer> X, Integer vertex) {
        List<Integer> result = new ArrayList<>(X);
        GraphWrapper graphWrapper = graphDefinition.getGraphWrapper();

//        printDebug("updateX: X prior to the update = " + X);

        // Mark the vertex as colored.
        result.set(vertex - 1, COLORED_TERMINAL);
        numberOfConstraintChecks++;

        // Since it has been colored, we now need to check for any adjacent uncolored nodes
        // and have their degrees be reduced by one.
        for (Integer neighboringVertex : graphWrapper.getNeighborsOfV(vertex)) {
            numberOfConstraintChecks++;
            Integer neighborValue = X.get(neighboringVertex - 1);
            if (neighborValue != COLORED_TERMINAL) {
                result.set(neighboringVertex - 1, --neighborValue);
            }
        }

//        printDebug("updateX: Updated X = " + result);

        return result;
    }

    /**
     * Calculates a random index to use based on tau * eta values.
     * @param tauEta The list of tau * eta values.
     * @param tauEtaTotal The accumulated value of tau * eta values.
     * @return An index picked at random.
     */
    private int randomIndexUsingTauEta(List<Double> tauEta, Double tauEtaTotal) {
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

        for (int i = 0; i < tauEta.size(); i++) {
            double tauEtaValue = tauEta.get(i);
            if (accumulator <= selectedValue && selectedValue < (accumulator + tauEtaValue)) {
                // printDebug("Selecting index: " + i);
                return i;
            }
            else {
                accumulator += tauEtaValue;
            }
        }

        // This is an edge case where the value we're attempting to select at this point is
        // incredibly miniscular (i.e. 0.0000.....1). This can be due to multiple reductions in tau eta values caused by
        // evaporation. Since it is not strictly 0, it's still worth selecting this value if it exists.
        for (int i = 0; i < tauEta.size(); i++) {
            double tauEtaValue = tauEta.get(i);
            if (tauEtaValue > 0.0) { return i; }
        }

        // Last resort: simply choose a value uniformly.
        return random.nextInt(tauEta.size());
    }

    /**
     * Picks the most appropriate X value based on the number of minimum edges.
     * @param tempX
     * @return
     */
    private int chooseMinimumEdges(List<List<Integer>> tempX) {
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

        // printDebug("Choosing minimum edge index: " + minimumIndex);
        return minimumIndex;
    }

    /**
     * Prepares the subroutine.
     */
    private void prepare() {
        createDegreesVector();

        printDebug("---------------------------------------------");
        printDebug("Running AntCol with the following parameters:");
        printDebug("Maximum constraint checks:  " + numberOfConstraintChecks);
        printDebug("Target coloring value:      " + targetColors);
        printDebug("Random seed:                " + randomSeed);
        printDebug("TabuCol iterations:         " + tabuIterations);
        printDebug("Initial `k`:                " + k);
        printDebug("Alpha:                      " + alpha);
        printDebug("Beta:                       " + beta);
        printDebug("Evaporation rate:           " + evaporationRate);
        printDebug("Number of ants:             " + numberOfAnts);
        printDebug("Number of multisets:        " + numberOfMultisets);
        printDebug("---------------------------------------------");
    }

    /**
     * Creates a degrees vector.
     */
    private void createDegreesVector() {
        for (Integer vertex : graphDefinition.getGraphWrapper().getVertices()) {
            Integer neighbors = graphDefinition.getGraphWrapper().getNeighborsOfV(vertex).size();
            this.degrees.add(neighbors);
        }
    }

    /**
     * Updates the local pheremone values with the given solution cost.
     * The solution cost is determined as follows:
     * - If the solution is feasible, the cost is 3.
     * - Otherwise, the cost is 1 / total number of TabuCol conflicts.
     * @param solutionCost The cost of the solution.
     */
    private void updateLocalPheremoneValues(double solutionCost) {
        for (List<Integer> integers : solution) {
            for (int j = 0; j < integers.size(); j++) {
                for (int k = 0; k < integers.size(); k++) {
                    int rowIndex = integers.get(j) - 1;
                    int colIndex = integers.get(k) - 1;
                    double currentDeltaValue = localPheremoneDelta.getValue(rowIndex, colIndex);
                    localPheremoneDelta.setValue(rowIndex, colIndex, currentDeltaValue + solutionCost);

                    rowIndex = integers.get(k) - 1;
                    colIndex = integers.get(j) - 1;
                    currentDeltaValue = localPheremoneDelta.getValue(rowIndex, colIndex);
                    localPheremoneDelta.setValue(rowIndex, colIndex, currentDeltaValue + solutionCost);
                }
            }
        }
    }

    /**
     * Resets the local pheremone delta matrix.
     */
    private void resetLocalPheremoneDelta() {
        printDebug("[k=" + k + "] Resetting local pheremone delta matrix.");
        Integer numberOfVertices = graphDefinition.getGraphWrapper().getVertexSize();
        this.localPheremoneDelta = new PheremoneMatrix(numberOfVertices, numberOfVertices);
    }

    /**
     * Creates an empty solution list using k colors.
     */
    private void resetSolution() {
        printDebug("[k=" + k + "] Resetting solution array.");
        this.solution = Stream.generate(ArrayList<Integer>::new)
                .limit(k)
                .collect(Collectors.toList());
    }

    /**
     * Updates the global trail matrix.
     * This value is calculated as follows:
     * New GTM = evaporation rate * current GTM value + local pheremone delta.
     */
    private void updateGlobalTrailMatrix() {
        printDebug("[k=" + k + "] Updating global trail matrix.");
        for (int i = 0; i < graphDefinition.getGraphWrapper().getNumberOfVertices(); i++) {
            for (int j = 0; j < graphDefinition.getGraphWrapper().getNumberOfVertices(); j++) {
                if (i != j) {
                    double currentValue = globalTrailMatrix.getValue(i, j);
                    globalTrailMatrix.setValue(i, j, evaporationRate * currentValue + localPheremoneDelta.getValue(i, j));
                }
            }
        }
    }

    /**
     * Removes colors in `solution` where there are no vertices mapped for a particular color.
     */
    private void removeEmptyColorings() {
        int i = 0;
        while (i < solution.size()) {
            if (solution.get(i).isEmpty()) {
                solution.remove(i);
            }
            else {
                i++;
            }
        }
    }

    /**
     * Randomly assigns colors for vertices.
     * @note This is only used when the solution is considered infeasible, and is required to be filled for
     * subsequent ant iterations.
     * @param X A list of uncolored vertices.
     */
    private void randomlyAssignColorsForIncompleteSolution(List<Integer> X) {
        Random random = new Random();
        for (int i = 0; i < graphDefinition.getGraphWrapper().getNumberOfVertices(); i++) {
            if (X.get(i) >= 0) {
                int randomColor = random.nextInt(solution.size());
                solution.get(randomColor).add(i + 1);
            }
        }
    }

    private void printDebug(String string) {
        if (verbose) { System.out.println(string); }
    }
}
