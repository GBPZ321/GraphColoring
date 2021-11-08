package graph.definition;

import lombok.Data;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Map;

@Data
public class GraphWrapper {
    private final Graph<Integer, DefaultEdge> graph;
    private final Map<Integer, List<Integer>> adjList;
    private int edgeCount;
    private int vertexSize;

    public GraphWrapper(Graph<Integer, DefaultEdge> graph, Map<Integer, List<Integer>> adjList) {
        this.graph = graph;
        this.adjList = adjList;
        this.vertexSize = graph.vertexSet().size();
        this.edgeCount =  graph.edgeSet().size();
    }

    public List<Integer> getNeighborsOfV(int v) {
        return adjList.get(v);
    }
}
