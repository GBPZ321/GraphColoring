package reader;

import graph.definition.GraphDefinition;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a graph from a specified input stream and returns a colorable graph.
 * You get to choose the source.
 */
public interface GraphReader {
    GraphDefinition getGraph(InputStream stream, String graphName) throws IOException;
}
