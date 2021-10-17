import graph.ColorableVertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        Graph<ColorableVertex, DefaultEdge> testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream("dsjc250.5.col"));
    }

}
