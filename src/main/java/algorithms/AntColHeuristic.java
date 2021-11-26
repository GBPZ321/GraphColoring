package algorithms;

import algorithms.antcol.AntColSubroutine;
import algorithms.interfaces.ColoringHeuristic;
import datastructures.common.Shared;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;

public class AntColHeuristic implements ColoringHeuristic {

    private final GraphDefinition graphDefinition;
    private final static Integer DEFAULT_CONSTRAINT_CHECKS = 100000000;
    private final static Integer DEFAULT_K = 250;
    private final static Integer DEFAULT_TABU_ITERATIONS = 2;
    private final static Integer DEFAULT_RANDOM_SEED = 1;
    private final static Integer DEFAULT_TARGET_COLORS = 1;
    private final static Double DEFAULT_EVAPORATION_RATE = 0.75;
    private final static Integer DEFAULT_NUMBER_OF_ANTS = 10;

    private final Integer constraintChecks;
    private final Integer k;
    private final Integer tabuIterations;
    private final Integer randomSeed;
    private final Integer targetColors;
    private final Double evaporationRate;
    private final Integer numberOfAnts;
    private final boolean verbose;
    private final Shared shared;

    public AntColHeuristic(GraphDefinition graphDefinition, boolean verbose) {
        this(graphDefinition,
                DEFAULT_K,
                DEFAULT_CONSTRAINT_CHECKS,
                DEFAULT_TABU_ITERATIONS,
                DEFAULT_RANDOM_SEED,
                DEFAULT_TARGET_COLORS,
                DEFAULT_EVAPORATION_RATE,
                DEFAULT_NUMBER_OF_ANTS,
                verbose,
                null);
    }

    public AntColHeuristic(GraphDefinition graphDefinition, Double evaporationRate, Integer numberOfAnts, boolean verbose, Shared shared) {
        this(graphDefinition,
                DEFAULT_K,
                DEFAULT_CONSTRAINT_CHECKS,
                DEFAULT_TABU_ITERATIONS,
                DEFAULT_RANDOM_SEED,
                DEFAULT_TARGET_COLORS,
                evaporationRate,
                numberOfAnts,
                verbose,
                shared);
    }

    public AntColHeuristic(GraphDefinition graphDefinition,
                           Integer k,
                           Integer constraintChecks,
                           Integer tabuIterations,
                           Integer randomSeed,
                           Integer targetColors,
                           Double evaporationRate,
                           Integer numberOfAnts,
                           boolean verbose,
                           Shared shared) {
        this.graphDefinition = graphDefinition;
        this.k = k;
        this.constraintChecks = constraintChecks;
        this.tabuIterations = tabuIterations;
        this.randomSeed = randomSeed;
        this.targetColors = targetColors;
        this.evaporationRate = evaporationRate;
        this.numberOfAnts = numberOfAnts;
        this.verbose = verbose;
        this.shared = shared;
    }

    @Override
    public GraphSolution getColoring() {
        AntColSubroutine antColSubroutine = new AntColSubroutine(graphDefinition,
                k,
                constraintChecks,
                tabuIterations,
                randomSeed,
                targetColors,
                evaporationRate,
                numberOfAnts,
                verbose,
                shared);
        SolutionWithStatus solution = antColSubroutine.findSolution();
        return solution.getSolution();
    }

    @Override
    public String getName() {
        return "AntCol";
    }
}
