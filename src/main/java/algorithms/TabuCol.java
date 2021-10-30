package algorithms;

import datastructures.SolutionMatrix;
import datastructures.Triple;
import enums.ColoringStatus;
import graph.definition.GraphDefinition;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import utility.Satisfies;

import java.util.Map;

import static utility.GraphHelperFunctions.satisfies;

public class TabuCol implements VertexColoringAlgorithm<Integer> {

    private final GraphDefinition graphDefinition;
    private SolutionMatrix solutionMatrix;
    private Coloring<Integer> finalSolution;
    private static final Integer ITERATIONS = 100; // 10s

    public TabuCol(GraphDefinition graphDefinition) {
        this.graphDefinition = graphDefinition;
        int n = graphDefinition.getGraphWrapper().getVertexSize();
    }

    @Override
    public Coloring<Integer> getColoring() {
        RandomInitialColoring rndColor = new RandomInitialColoring(graphDefinition.getGraphWrapper().getGraph());
        Coloring<Integer> coloring = rndColor.getColoring();
        int k = graphDefinition.getGraphWrapper().getVertexSize();
        while(k > 1) {
            TabucolSolution tabucol = tabucol(coloring);
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

    private TabucolSolution tabucol(Coloring<Integer> coloring) {
        int iterations = ITERATIONS;
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
        for(Integer vertex : graph.vertexSet()) {
            Integer vertexColor = colorMap.get(vertex);
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

        //Perform move
        //Update tabu matrix.

    }

    private Satisfies viable(Coloring<Integer> coloringArray) {
        return satisfies(graphDefinition, coloringArray);
    }
}
