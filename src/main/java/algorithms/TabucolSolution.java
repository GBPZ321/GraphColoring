package algorithms;

import enums.ColoringStatus;
import lombok.Data;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

@Data
public class TabucolSolution {
    private ColoringStatus status;
    private VertexColoringAlgorithm.Coloring<Integer> solution;
}
