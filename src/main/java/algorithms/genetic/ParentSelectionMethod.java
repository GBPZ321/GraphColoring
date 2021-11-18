package algorithms.genetic;

import java.util.List;

public interface ParentSelectionMethod {
    List<Chromosome> getParents(List<Chromosome> populationList);
}
