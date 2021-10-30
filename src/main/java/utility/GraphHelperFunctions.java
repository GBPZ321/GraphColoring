package utility;

import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphHelperFunctions {

    public static Satisfies satisfies(GraphDefinition definition, VertexColoringAlgorithm.Coloring<Integer> coloring) {
        Graph<Integer, DefaultEdge> graphUnderTest = definition.getGraphWrapper().getGraph();
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

    public static boolean sat(GraphDefinition definition, VertexColoringAlgorithm.Coloring<Integer> coloring) {
        Graph<Integer, DefaultEdge> graphUnderTest = definition.getGraphWrapper().getGraph();
        Set<DefaultEdge> edgeSet = graphUnderTest.edgeSet();
        Map<Integer, Integer> vertexKeyColoringValue = coloring.getColors();
        for(DefaultEdge edge : edgeSet) {
            Integer v1 = graphUnderTest.getEdgeSource(edge);
            Integer v2 = graphUnderTest.getEdgeSource(edge);
            if(vertexKeyColoringValue.get(v1).equals(vertexKeyColoringValue.get(v2))) {
                return false;
            }
        }
        return true;
    }

    public static Integer conflictNumberForVertex(GraphDefinition definition, Integer vertex, VertexColoringAlgorithm.Coloring<Integer> coloring) {
        GraphWrapper wrapper = definition.getGraphWrapper();
        List<Integer> connected = wrapper.getAdjList().get(vertex);
        int conflictNum = 0;
        Map<Integer, Integer> colors = coloring.getColors();
        for(Integer connectedVertex : connected) {
            if(colors.get(vertex).equals(colors.get(connectedVertex))) {
                conflictNum++;
            }
        }
        return conflictNum;
    }


}
