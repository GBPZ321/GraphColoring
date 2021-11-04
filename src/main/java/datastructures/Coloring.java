package datastructures;

import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Coloring implements VertexColoringAlgorithm.Coloring<Integer> {
    private final Map<Integer, Integer> colorings;

    public Coloring(Map<Integer, Integer> colorings) {
        this.colorings = colorings;
    }

    @Override
    public int getNumberColors() {
        return new HashSet<>(colorings.values()).size();
    }

    @Override
    public Map<Integer, Integer> getColors() {
        return colorings;
    }

    @Override
    public List<Set<Integer>> getColorClasses() {
        throw new RuntimeException("Not implemented");
    }
}
