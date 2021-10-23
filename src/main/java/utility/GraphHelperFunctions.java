package utility;

import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;
import java.util.Set;

public class GraphHelperFunctions {

    public static Satisfies satisfies(GraphDefinition definition, VertexColoringAlgorithm.Coloring<Integer> coloring) {
        Graph<Integer, DefaultEdge> graphUnderTest = definition.getGraph();
        Set<DefaultEdge> edgeSet = graphUnderTest.edgeSet();
        Map<Integer, Integer> vertexKeyColoringValue = coloring.getColors();
        int errorCount = 0;
        for(DefaultEdge edge : edgeSet) {
            Integer v1 = graphUnderTest.getEdgeSource(edge);
            Integer v2 = graphUnderTest.getEdgeSource(edge);
            if(vertexKeyColoringValue.get(v1).equals(vertexKeyColoringValue.get(v2))) {
                errorCount++;
            }
        }
        return new Satisfies(errorCount, errorCount > 0);
    }
}
