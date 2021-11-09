package graph.definition;

import lombok.Data;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper around a graph. Provides some simple APIs along with access to the underlying object for access as well.
 */
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

    /**
     * Get the neighbors of the vertex labelled V.
     * @param v - the vertex
     * @return - neighbors of v.
     */
    public List<Integer> getNeighborsOfV(int v) {
        return adjList.get(v);
    }

    /**
     * Get the set of vertices.
     * @return The vertex set.
     */
    public Set<Integer> getVertices() {
        return graph.vertexSet();
    }

    public Set<DefaultEdge> getEdges() {
        return graph.edgeSet();
    }

    public int getNumberOfEdges() {
        return edgeCount;
    }

    public int getNumberOfVertices() {
        return vertexSize;
    }


}
