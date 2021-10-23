package formatter;

import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class GraphOutputFormatter {
    public static void writeGraphToFile(VertexColoringAlgorithm.Coloring<Integer> coloring, GraphDefinition graphDefinition) throws IOException {

        Schema schema = Schema.builder()
                .vertexCount(graphDefinition.getGraph().vertexSet().size())
                .edgeCount(graphDefinition.getGraph().edgeSet().size())
                .colors(coloring.getColors())
                .edges(graphDefinition.getMetadata().getEdges())
                .build();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(graphDefinition.getMetadata().getGraphName() + ".soln"), StandardCharsets.UTF_8))) {
            writer.write(schema.toString());
        }
    }

}
