package utility;

import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;
import java.util.Set;

public class GraphHelperFunctions {

    /**
     * Simple satisfiability tester
     * @param definition - Graph definition under test
     * @param coloring - coloring of the vertices
     * @return a boolean representing whether the graph has satisfied the problem.
     */
    public static boolean sat(GraphDefinition definition, VertexColoringAlgorithm.Coloring<Integer> coloring) {
        Graph<Integer, DefaultEdge> graphUnderTest = definition.getGraphWrapper().getGraph();
        Set<DefaultEdge> edgeSet = graphUnderTest.edgeSet();
        Map<Integer, Integer> vertexKeyColoringValue = coloring.getColors();
        for(DefaultEdge edge : edgeSet) {
            Integer v1 = graphUnderTest.getEdgeSource(edge);
            Integer v2 = graphUnderTest.getEdgeTarget(edge);
            if(vertexKeyColoringValue.get(v1).equals(vertexKeyColoringValue.get(v2))) {
                return false;
            }
        }
        return true;
    }


}
