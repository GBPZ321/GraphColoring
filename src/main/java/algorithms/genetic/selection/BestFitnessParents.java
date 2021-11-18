package algorithms.genetic.selection;

import algorithms.genetic.Chromosome;
import algorithms.genetic.ParentSelectionMethod;

import java.util.List;
import java.util.stream.Collectors;

public class BestFitnessParents implements ParentSelectionMethod {
    @Override
    public List<Chromosome> getParents(List<Chromosome> populationList) {
        return populationList.stream().sorted().limit(1).collect(Collectors.toList());
    }
}
