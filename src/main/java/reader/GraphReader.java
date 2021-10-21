package reader;

import graph.ColorableVertex;
import graph.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a graph from a specified input stream and returns a colorable graph.
 */
public interface GraphReader {
    GraphDefinition getGraph(InputStream stream, String graphName) throws IOException;
}
