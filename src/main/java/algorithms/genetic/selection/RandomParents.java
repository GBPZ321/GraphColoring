package algorithms.genetic.selection;

import algorithms.genetic.Chromosome;
import algorithms.genetic.ParentSelectionMethod;

import java.util.List;
import java.util.stream.Collectors;

import static algorithms.random.RandomUtils.pickRandom;

public class RandomParents implements ParentSelectionMethod {
    @Override
    public List<Chromosome> getParents(List<Chromosome> populationList) {
        return pickRandom(populationList, 4)
                .stream()
                .sorted()
                .limit(2)
                .collect(Collectors.toList());
    }
}
