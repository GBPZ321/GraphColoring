package graph.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GraphSolution {
    private Map<Integer, Integer> coloring;
    private int k;
}
