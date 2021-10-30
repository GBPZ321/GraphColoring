package algorithms;

import datastructures.SolutionMatrix;
import datastructures.Triple;
import enums.ColoringStatus;
import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import utility.Satisfies;

import java.util.List;
import java.util.Map;

import static utility.GraphHelperFunctions.satisfies;

public class TabuCol implements VertexColoringAlgorithm<Integer> {

    private final GraphDefinition graphDefinition;
    private SolutionMatrix solutionMatrix;
    private Coloring<Integer> finalSolution;
    private static final Integer ITERATIONS = 100;

    public TabuCol(GraphDefinition graphDefinition) {
        this.graphDefinition = graphDefinition;
    }

    @Override
    public Coloring<Integer> getColoring() {
        int k = graphDefinition.getGraphWrapper().getVertexSize();
        while(k > 1) {
            TabucolSolution tabucol = tabucol(k, ITERATIONS, 1, 1);
            if(tabucol.getStatus() == ColoringStatus.SATISFIED) {
                this.finalSolution = tabucol.getSolution();
            }
            if(tabucol.getStatus() == ColoringStatus.TIMEOUT) {
                return finalSolution;
            }
            k--;
        }
        return finalSolution;
    }

    private TabucolSolution tabucol(int k, int iterations, int L, int alpha) {
        System.out.println("Timeout : " + iterations);
        System.out.println("L: " + L);
        System.out.println("Alpha: " + alpha);
        RandomInitialColoring rndColor = new RandomInitialColoring(graphDefinition.getGraphWrapper().getGraph(), k);
        Coloring<Integer> coloring = rndColor.getColoring();
        solutionMatrix = new SolutionMatrix(coloring, graphDefinition);
        TabucolSolution tabucolSolution = new TabucolSolution();
        while(true) {
            iterations--;
            if(iterations == 0) {
                tabucolSolution.setStatus(ColoringStatus.TIMEOUT);
                return tabucolSolution;
            }
            Satisfies satisfies = viable(coloring);
            if(satisfies.getErrorCount() == 0) {
                tabucolSolution.setStatus(ColoringStatus.SATISFIED);
                tabucolSolution.setSolution(coloring);
            }
            findBestMoveAndUpdateMatrices(coloring);
        }
    }

    private void findBestMoveAndUpdateMatrices(Coloring<Integer> coloring) {
        Triple<Integer, Integer, Integer> vertexColoringMove = new Triple<>();
        Graph<Integer, DefaultEdge> graph = graphDefinition.getGraphWrapper().getGraph();
        Map<Integer, Integer> colorMap = coloring.getColors();
        Map<Integer, List<Integer>> adjList = graphDefinition.getGraphWrapper().getAdjList();
        for(Integer vertex : graph.vertexSet()) {
            Integer vertexColor = colorMap.get(vertex);
            //TODO: NEED TO SEE IF MOVE IN TABU MATRIX
            for(int colors = 1; colors <= coloring.getNumberColors(); ++colors) {
                if(colors == vertexColor) continue;
                int delta = solutionMatrix.getMatrixEntry(vertex, colors) - solutionMatrix.getMatrixEntry(vertex, vertexColor);
                if(vertexColoringMove.isEmpty()) {
                    vertexColoringMove.setEmpty(false);
                    vertexColoringMove.setVertex(vertex);
                    vertexColoringMove.setColor(colors);
                    vertexColoringMove.setDelta(delta);
                }
                if(delta < vertexColoringMove.getDelta()) {
                    vertexColoringMove.setVertex(vertex);
                    vertexColoringMove.setColor(colors);
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

        //Update tabu matrix.

    }

    private Satisfies viable(Coloring<Integer> coloringArray) {
        return satisfies(graphDefinition, coloringArray);
    }
}
