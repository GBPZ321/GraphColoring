package algorithms.interfaces;

import graph.solution.GraphSolution;

import java.util.Map;

public interface ColoringHeuristic {
    GraphSolution getColoring();
    String getName();
}
