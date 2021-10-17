package reader;

import graph.ColorableVertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DimacsReader implements GraphReader {
    @Override
    public Graph<ColorableVertex, DefaultEdge> getGraph(InputStream stream) throws IOException {
        Map<Integer, ColorableVertex> referenceMap = new HashMap<>();
        Graph<ColorableVertex, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        int vertices = 0;
        int edges = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("c")) continue;
                if(line.startsWith("p")) {
                    String[] problemStatement = line.split(" ");
                    assert problemStatement.length == 4;
                    vertices = Integer.parseInt(problemStatement[2]);
                    for(int i = 1; i <= vertices; ++i) {
                        ColorableVertex v = new ColorableVertex(String.valueOf(i));
                        g.addVertex(v);
                        referenceMap.put(i, v);
                    }
                    edges = Integer.parseInt(problemStatement[3]);
                }
                if(line.startsWith("e")) {
                    String[] components = line.split(" ");
                    assert components.length == 3;
                    ColorableVertex v1 = referenceMap.get(Integer.parseInt(components[1]));
                    ColorableVertex v2 = referenceMap.get(Integer.parseInt(components[2]));
                    g.addEdge(v1, v2);
                    edges--;
                }
            }
        }
        assert edges == 0;
        return g;
    }
}
