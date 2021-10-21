package graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GraphDefinition {
    private Graph<Integer, DefaultEdge> graph;
    private List<String> edges;
    private String graphName;
}
