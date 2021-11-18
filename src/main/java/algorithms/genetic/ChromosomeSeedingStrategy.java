package algorithms.genetic;

import algorithms.interfaces.SeedingStrategy;

import java.util.HashMap;
import java.util.Map;

public class ChromosomeSeedingStrategy implements SeedingStrategy {
    private final Chromosome parent1;
    private final Chromosome parent2;
    private final int crossoverPoint;
    private final int vertices;

    public ChromosomeSeedingStrategy(Chromosome parent1, Chromosome parent2, int crossoverPoint, int vertices) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.crossoverPoint = crossoverPoint;
        this.vertices = vertices;
    }

    @Override
    public Map<Integer, Integer> getStartingColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        for(int vert = 1; vert <= vertices; ++vert) {
            if(vert < crossoverPoint) {
                coloring.put(vert, parent1.getColoring().get(vert));
            } else if(vert >= crossoverPoint) {
                coloring.put(vert, parent2.getColoring().get(vert));
            }
        }
        return coloring;
    }
}
