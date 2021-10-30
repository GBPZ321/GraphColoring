package graph.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class GraphDefinition {
    /**
     * The graph itself.
     */
    private GraphWrapper graphWrapper;
    /**
     * Useful information for printing or knowledge about graph.
     */
    private GraphMetadata metadata;
}
