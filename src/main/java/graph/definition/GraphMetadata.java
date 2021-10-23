package graph.definition;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphMetadata {
    /**
     * A useful mechanism for copying over the edges from input graph to output graph for pretty printing.
     */
    private List<String> edges;
    /**
     * Name of the graph for convenience.
     */
    private String graphName;
}
