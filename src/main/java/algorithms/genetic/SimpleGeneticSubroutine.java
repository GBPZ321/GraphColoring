package algorithms.genetic;

import algorithms.genetic.mutation.AdvancedMutation;
import algorithms.genetic.mutation.SimpleRandomMutation;
import algorithms.genetic.selection.BestFitnessParents;
import algorithms.genetic.selection.RandomParents;
import algorithms.interfaces.SeedingStrategy;
import algorithms.interfaces.Subroutine;
import algorithms.random.CompletelyRandomColoring;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.SolutionWithStatus;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static algorithms.random.RandomUtils.getRandomNumber;

public class SimpleGeneticSubroutine implements Subroutine {
    private List<Chromosome> populationList;
    private final int populationCapacity;
    private final int numberOfGenerations;
    private GraphWrapper wrapper;
    private int k;
    private static final int ALGORITHM_TOGGLE = 4;

    public SimpleGeneticSubroutine(GraphWrapper wrapper, int populationCapacity, int k, int generations) {
        this.numberOfGenerations = generations;
        this.populationCapacity = populationCapacity;
        this.populationList = new ArrayList<>();
        this.wrapper = wrapper;
        this.k = k;
        addNElements(wrapper, populationCapacity, k);
    }

    private void addNElements(GraphWrapper wrapper, int N, int k) {
        for(int i = 0; i < N; i++) {
            SeedingStrategy crc = new CompletelyRandomColoring(wrapper, k);
            populationList.add(new Chromosome(crc, wrapper, k));
        }
    }


    @Override
    public SolutionWithStatus findSolution() {
        int generation = 0;
        int optimalFitness = Integer.MAX_VALUE;
        while(generation < numberOfGenerations) {
            optimalFitness = findOptimalFitness().getFitness();
            System.out.println(optimalFitness);
            if(optimalFitness == 0) {
                break;
            }
            runGeneticAlgorithm(optimalFitness);
            yourFamilyHasBeenEliminated();
            generation++;
        }


        Chromosome optimal = findOptimalFitness();
        if(optimal.getFitness() != 0) {
            return new SolutionWithStatus(ColoringStatus.TIMEOUT, new GraphSolution(optimal.getColoring(), k));
        } else {
            return new SolutionWithStatus(ColoringStatus.SATISFIED, new GraphSolution(optimal.getColoring(), k));
        }
    }

    private Chromosome findOptimalFitness() {
        return populationList.stream().min(Comparator.comparing(Chromosome::getFitness)).get();
    }

    private void yourFamilyHasBeenEliminated() {
        populationList = populationList.stream().sorted().limit(populationCapacity / 2).collect(Collectors.toList());
        addNElements(wrapper, populationCapacity / 2, k);
    }

    private void runGeneticAlgorithm(int optimalFitness) {
        ParentSelectionMethod parentSelectionMethod;
        if(optimalFitness >= ALGORITHM_TOGGLE) {
            parentSelectionMethod = new BestFitnessParents();
        } else {
            parentSelectionMethod  = new RandomParents();
        }

        List<Chromosome> parents = parentSelectionMethod.getParents(populationList);
        Chromosome crossoverChromosome = crossover(parents);
        if(crossoverChromosome.getFitness() == 0) {
            populationList.add(crossoverChromosome);
            return;
        }

        Mutation strategy;
        if(optimalFitness >= ALGORITHM_TOGGLE) {
            strategy = new AdvancedMutation(wrapper, k);
        } else {
            strategy = new SimpleRandomMutation(wrapper, k);
        }
        Chromosome offspring = strategy.mutate(crossoverChromosome);
        populationList.add(offspring);


    }

    private Chromosome crossover(List<Chromosome> parents) {
        int randomPoint = getRandomNumber(0, k);
        Chromosome p0 = parents.get(0);
        Chromosome p1 = parents.get(0);
        if(parents.size() > 1) {
            p1 = parents.get(1);
        }
        ChromosomeSeedingStrategy chromosomeSeedingStrategy = new ChromosomeSeedingStrategy(p0, p1, randomPoint, wrapper.getNumberOfVertices());
        return new Chromosome(chromosomeSeedingStrategy, wrapper, k);
    }

}
