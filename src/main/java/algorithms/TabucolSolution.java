package algorithms;

import datastructures.pojo.ColoringStatus;
import lombok.Data;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

@Data
public class TabucolSolution {
    private ColoringStatus status;
    private VertexColoringAlgorithm.Coloring<Integer> solution;
}
