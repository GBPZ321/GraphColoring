package algorithms.common;

import datastructures.pojo.ColoringStatus;
import graph.solution.GraphSolution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionWithStatus {
    private ColoringStatus status;
    private GraphSolution solution;
}
