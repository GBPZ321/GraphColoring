package algorithms.tabucol;

import datastructures.pojo.ColoringStatus;
import graph.solution.GraphSolution;
import lombok.Data;

@Data
public class TabucolSolution {
    private ColoringStatus status;
    private GraphSolution solution;
}
