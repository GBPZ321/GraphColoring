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
                return tabucolSolution;
            }
            findBestMoveAndUpdateMatrices(coloring, conflictNumber);
        }
    }

    private void findBestMoveAndUpdateMatrices(VertexColoringAlgorithm.Coloring<Integer> coloring, int conflictNumber) {
        Set<Integer> vertexSet = graphDefinition.getGraphWrapper().getGraph().vertexSet();
        int max = -vertexSet.size();
        Move move = new Move();
        Map<Integer, Integer> colorMap = coloring.getColors();
        for(int newColor = 0; newColor < k; ++newColor) {
            for(Integer vertex : vertexSet) {
                if(solutionMatrix.getMatrixEntry(vertex, colorMap.get(vertex)) == 0) continue;
                Integer oldColor = colorMap.get(vertex);
                if(newColor == oldColor) continue;
                if(tabuStructure.isInTabuMatrix(vertex, newColor)) continue;
                int delta = solutionMatrix.getMovePerformance(vertex, oldColor, newColor, conflictNumber);
                if(delta > conflictNumber) {
                    System.out.println("Interesting");
                }
                if(delta > max) {
                    move.setColor(newColor);
                    move.setVertex(vertex);
                    max = delta;
                }
            }
        }

        if(max > conflictNumber) {
            System.out.println("Problem");
        }

        Integer v = move.getVertex();
        Integer oldColor = colorMap.get(v);
        Integer replacementColor = move.getColor();
        coloring.getColors().put(v, replacementColor);
        solutionMatrix.updateSolution(v, oldColor, replacementColor);
        tabuStructure.insertTabuColor(conflictNumber, v, replacementColor);
    }


}
