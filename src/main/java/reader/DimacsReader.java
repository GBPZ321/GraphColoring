package reader;

import graph.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DimacsReader implements GraphReader {
    @Override
    public GraphDefinition getGraph(InputStream stream, String name) throws IOException {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        List<String> e = new ArrayList<>();
        int edges = 0;
        int vertices = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("c")) continue;
                if(line.startsWith("p")) {
                    String[] problemStatement = line.split(" ");
                    assert problemStatement.length == 4;
                    vertices = Integer.parseInt(problemStatement[2]);
                    for(int i = 1; i <= vertices; ++i) {
                        g.addVertex(i);
                    }
                    edges = Integer.parseInt(problemStatement[3]);

                }
                if(line.startsWith("e")) {
                    e.add(line.replace("e ", "").trim());
                    String[] components = line.split(" ");
                    assert components.length == 3;
                    Integer v1 = Integer.parseInt(components[1]);
                    Integer v2 = Integer.parseInt(components[2]);
                    g.addEdge(v1, v2);
                    edges--;
                }
            }
        }
        assert edges == 0;
        return new GraphDefinition(g, e, name);
    }
}
