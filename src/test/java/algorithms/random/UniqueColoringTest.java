package algorithms.random;

import graph.definition.GraphDefinition;
import org.junit.jupiter.api.Test;
import reader.DimacsReader;
import utility.GraphHelperFunctions;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class UniqueColoringTest {

    private static GraphDefinition getSimpleGraph() throws IOException {
        DimacsReader reader = new DimacsReader();
        String name = "simpleGraph.col";
        return reader.getGraph(GraphHelperFunctions.class.getClassLoader().getResourceAsStream(name), name);
    }

    private static GraphDefinition getDsjcGraph() throws IOException {
        DimacsReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        return reader.getGraph(GraphHelperFunctions.class.getClassLoader().getResourceAsStream(name), name);
    }

    void checkUniqueColoringSize(@org.jetbrains.annotations.NotNull GraphDefinition graphDefinition){
        UniqueColoring uniqueColoring = new UniqueColoring(graphDefinition.getGraphWrapper().getGraph());
        HashSet<Integer> colorSet = new HashSet<>(uniqueColoring.getStartingColoring().values());
        assertEquals(colorSet.size(), graphDefinition.getGraphWrapper().getGraph().vertexSet().size());

    }

    @Test
    void generateSimpleUniqueColoring() throws Exception {
        GraphDefinition simpleGraph = getSimpleGraph();
        checkUniqueColoringSize(simpleGraph);
    }

    @Test
    void generateDsjcUniqueColoring() throws Exception {
        GraphDefinition dsjcGraph = getDsjcGraph();
        checkUniqueColoringSize(dsjcGraph);
    }
}
