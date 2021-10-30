package algorithms;

import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.Triple;
import enums.ColoringStatus;
import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Map;

public class TabucolSubroutine {
    private final GraphDefinition graphDefinition;
    private final int k;
    private final int iterations;
    private SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;

    public TabucolSubroutine(GraphDefinition graphDefinition, int k, int alpha, int l, int iterationTimeout) {
        this.graphDefinition = graphDefinition;
        this.k = k;
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
    }

    TabucolSolution findSolution() {
        int runs = iterations;
        RandomInitialColoring rndColor = new RandomInitialColoring(graphDefinition.getGraphWrapper().getGraph(), k);
        VertexColoringAlgorithm.Coloring<Integer> coloring = rndColor.getColoring();
        solutionMatrix = new SolutionMatrix(coloring, graphDefinition);
        TabucolSolution tabucolSolution = new TabucolSolution();
        while(true) {
            runs--;
            if(runs == 0) {
                tabucolSolution.setStatus(ColoringStatus.TIMEOUT);
                return tabucolSolution;
            }
            int conflictNumber = solutionMatrix.getConflictNumber();
            if(conflictNumber == 0) {
                tabucolSolution.setStatus(ColoringStatus.SATISFIED);
                tabucolSolution.setSolution(coloring);
            }
            findBestMoveAndUpdateMatrices(coloring, conflictNumber);
        }
    }

    private void findBestMoveAndUpdateMatrices(VertexColoringAlgorithm.Coloring<Integer> coloring, int conflictNumber) {
        Triple<Integer, Integer, Integer> vertexColoringMove = new Triple<>();
        Graph<Integer, DefaultEdge> graph = graphDefinition.getGraphWrapper().getGraph();
        Map<Integer, Integer> colorMap = coloring.getColors();
        Map<Integer, List<Integer>> adjList = graphDefinition.getGraphWrapper().getAdjList();
        for(Integer vertex : graph.vertexSet()) {
            Integer vertexColor = colorMap.get(vertex);

            for(int potentialColorChange = 1; potentialColorChange <= coloring.getNumberColors(); ++potentialColorChange) {
                if(potentialColorChange == vertexColor) continue;
                int delta = solutionMatrix.getMatrixEntry(vertex, potentialColorChange) - solutionMatrix.getMatrixEntry(vertex, vertexColor);
                if(tabuStructure.isInTabuMatrix(vertex, potentialColorChange)) continue;
                if(vertexColoringMove.isEmpty()) {
                    vertexColoringMove.setEmpty(false);
                    vertexColoringMove.setVertex(vertex);
                    vertexColoringMove.setColor(potentialColorChange);
                    vertexColoringMove.setDelta(delta);
                }
                if(delta < vertexColoringMove.getDelta()) {
                    vertexColoringMove.setVertex(vertex);
                    vertexColoringMove.setColor(potentialColorChange);
                    vertexColoringMove.setDelta(delta);
                }
            }
        }
        Integer v = vertexColoringMove.getVertex();
        Integer i = colorMap.get(v);
        Integer j = vertexColoringMove.getColor();
        for(Integer u : adjList.get(v)) {
            int c_ui = solutionMatrix.getMatrixEntry(u, i);
            int c_uj = solutionMatrix.getMatrixEntry(u, j);
            solutionMatrix.updateMatrix(u, i, c_ui - 1);
            solutionMatrix.updateMatrix(u, j, c_uj + 1);
        }
        tabuStructure.insertTabuColor(conflictNumber, v, j);
    }
}
