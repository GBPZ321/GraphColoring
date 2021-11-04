package reader;

import graph.definition.GraphDefinition;
import graph.definition.GraphMetadata;
import graph.definition.GraphWrapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimacsReader implements GraphReader {

    /**
     * Reads a graph in DIMACS format
     * @param stream - input stream for Graph
     * @param name - name of the graph
     * @return A graph definition.
     * @throws IOException Stream error
     */
    @Override
    public GraphDefinition getGraph(InputStream stream, String name) throws IOException {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        List<String> e = new ArrayList<>();
        Map<Integer, List<Integer>> adjacencyListMap = new HashMap<>();
        int vertices;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("c")) continue;
                else if(line.startsWith("p")) {
                    String[] problemStatement = line.split(" ");
                    assert problemStatement.length == 4;
                    vertices = Integer.parseInt(problemStatement[2]);
                    for(int i = 1; i <= vertices; ++i) {
                        g.addVertex(i);
                        adjacencyListMap.put(i, new ArrayList<>());
                    }
                }
                else if(line.startsWith("e")) {
                    e.add(line.replace("e ", "").trim());
                    String[] components = line.split(" ");
                    assert components.length == 3;
                    Integer v1 = Integer.parseInt(components[1]);
                    Integer v2 = Integer.parseInt(components[2]);
                    adjacencyListMap.get(v1).add(v2);
                    g.addEdge(v1, v2);
                }
            }
        }


        return GraphDefinition.builder()
                .graphWrapper(new GraphWrapper(g, adjacencyListMap))
                .metadata(GraphMetadata.builder()
                        .edges(e)
                        .graphName(name)
                        .build())
                .build();
    }
}
