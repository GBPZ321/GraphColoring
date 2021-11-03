import formatter.GraphOutputFormatter;
import graph.definition.GraphDefinition;
import org.jgrapht.alg.color.SaturationDegreeColoring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import reader.DimacsReader;
import reader.GraphReader;

import java.io.IOException;

public class GraphCreator {

    public static void main(String[] args) throws IOException {
        GraphReader reader = new DimacsReader();
        String name = "dsjc250.5.col";
        GraphDefinition testGraph = reader.getGraph(GraphCreator.class.getClassLoader().getResourceAsStream(name), name);
        SaturationDegreeColoring<Integer, DefaultEdge> graphColoring = new SaturationDegreeColoring<>(testGraph.getGraphWrapper().getGraph());
        VertexColoringAlgorithm.Coloring<Integer> coloring = graphColoring.getColoring();
        System.out.println(coloring.getNumberColors());
    }

}
