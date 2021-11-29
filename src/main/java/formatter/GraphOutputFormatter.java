package formatter;

import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GraphOutputFormatter {
    public static void writeGraphToFile(VertexColoringAlgorithm.Coloring<Integer> coloring, GraphDefinition graphDefinition) throws IOException {

        Schema schema = Schema.builder()
                .vertexCount(graphDefinition.getGraphWrapper().getVertexSize())
                .edgeCount(graphDefinition.getGraphWrapper().getGraph().edgeSet().size())
                .colors(coloring.getColors())
                .edges(graphDefinition.getMetadata().getEdges())
                .build();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(graphDefinition.getMetadata().getGraphName() + ".soln"), StandardCharsets.UTF_8))) {
            writer.write(schema.toString());
        }
    }

    public static void writeGraphToFile(Map<Integer, Integer> coloring, GraphDefinition graphDefinition) throws IOException {

        Schema schema = Schema.builder()
                .vertexCount(graphDefinition.getGraphWrapper().getVertexSize())
                .edgeCount(graphDefinition.getGraphWrapper().getGraph().edgeSet().size())
                .colors(coloring)
                .edges(graphDefinition.getMetadata().getEdges())
                .build();

        String outputName = graphDefinition.getMetadata().getGraphName() + ".soln";
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        String pathname = s + "\\" + "plotting" + "\\" + outputName;
        File myFile = new File(pathname);
        myFile.createNewFile();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(myFile, false), StandardCharsets.UTF_8))) {
            writer.write(schema.toString());
        }
    }

}
