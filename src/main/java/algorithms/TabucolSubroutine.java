package algorithms;

import datastructures.pojo.Move;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        SimpleOrderedColoring rndColor = new SimpleOrderedColoring(graphDefinition.getGraphWrapper().getGraph(), k);
        VertexColoringAlgorithm.Coloring<Integer> coloring = rndColor.getColoring();
        solutionMatrix = new SolutionMatrix(coloring.getColors(), coloring.getNumberColors(), graphDefinition);
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
                return tabucolSolution;
            }
            findBestMoveAndUpdateMatrices(coloring, conflictNumber);
        }
    }

    private void findBestMoveAndUpdateMatrices(VertexColoringAlgorithm.Coloring<Integer> coloring, int conflictNumber) {
        Set<Integer> vertexSet = graphDefinition.getGraphWrapper().getGraph().vertexSet();
        Map<Integer, Integer> colorMap = coloring.getColors();
        Move move = findBestMove(vertexSet, colorMap, conflictNumber);
        Integer v = move.getVertex();
        Integer oldColor = colorMap.get(v);
        Integer replacementColor = move.getColor();
        coloring.getColors().put(v, replacementColor);
        solutionMatrix.updateSolution(v, oldColor, replacementColor);
        tabuStructure.insertTabuColor(conflictNumber, v, replacementColor);
    }

    private Move findBestMove(Set<Integer> vertexSet, Map<Integer, Integer> colorMap, int conflictNumber) {
        int max = -vertexSet.size();
        Move move = new Move();
        for(int newColor = 0; newColor < k; ++newColor) {
            for(Integer vertex : vertexSet) {
                Integer oldColor = colorMap.get(vertex);
                if(newColor == oldColor) continue;
                if(tabuStructure.isInTabuMatrix(vertex, newColor)) continue;
                int delta = solutionMatrix.getMovePerformance(vertex, oldColor, newColor);
                if(delta > max) {
                    move.setColor(newColor);
                    move.setVertex(vertex);
                    max = delta;
                }
                if(delta == conflictNumber) return move;
            }
        }
        return move;
    }
}
