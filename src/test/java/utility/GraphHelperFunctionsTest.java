package utility;

import algorithms.SimpleOrderedColoring;
import graph.definition.GraphDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reader.DimacsReader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static utility.GraphHelperFunctions.sat;

class GraphHelperFunctionsTest {

    private static GraphDefinition graphDefinition;

    @BeforeAll
    static void setUp() throws IOException {
        graphDefinition = getComplicatedGraph();
    }

    private static GraphDefinition getComplicatedGraph() throws IOException {
        DimacsReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        return reader.getGraph(GraphHelperFunctions.class.getClassLoader().getResourceAsStream(name), name);
    }

    @Test
    void sat_numerous() throws IOException {
        SimpleOrderedColoring orderedColoring = new SimpleOrderedColoring(graphDefinition.getGraphWrapper().getGraph(), graphDefinition.getGraphWrapper().getVertexSize());
        assertTrue(sat(graphDefinition, orderedColoring.getColoring()));
    }

    @Test
    void sat_trivial() throws IOException {
        SimpleOrderedColoring orderedColoring = new SimpleOrderedColoring(graphDefinition.getGraphWrapper().getGraph(), 2);
        assertFalse(sat(graphDefinition, orderedColoring.getColoring()));
    }
}