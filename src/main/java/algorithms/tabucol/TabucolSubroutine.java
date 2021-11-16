package algorithms.tabucol;

import algorithms.common.SolutionWithStatus;
import algorithms.random.SimpleOrderedColoring;
import datastructures.pojo.Move;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.solution.GraphSolution;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.util.*;

public class TabucolSubroutine {
    private final GraphDefinition graphDefinition;
    private final int k;
    private final int iterations;
    private SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private final Random tieBreaker;

    public TabucolSubroutine(GraphDefinition graphDefinition, int k, float alpha, int l, int iterationTimeout) {
        this.graphDefinition = graphDefinition;
        this.k = k;
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
        this.tieBreaker = new Random();
    }

    public SolutionWithStatus findSolution() {
        int runs = iterations;
        SimpleOrderedColoring rndColor = new SimpleOrderedColoring(graphDefinition.getGraphWrapper().getGraph(), k);
        VertexColoringAlgorithm.Coloring<Integer> coloring = rndColor.getColoring();
        solutionMatrix = new SolutionMatrix(coloring.getColors(), coloring.getNumberColors(), graphDefinition);
        SolutionWithStatus tabucolSolution = new SolutionWithStatus();
        while(true) {
            runs--;
            if(runs == 0) {
                tabucolSolution.setStatus(ColoringStatus.TIMEOUT);
                return tabucolSolution;
            }
            int conflictNumber = solutionMatrix.getConflictNumber();
            if(conflictNumber == 0) {
                tabucolSolution.setStatus(ColoringStatus.SATISFIED);
                tabucolSolution.setSolution(new GraphSolution(coloring.getColors(), k));
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
        List<Move> potentialMoves = new ArrayList<>();
        for(int newColor = 0; newColor < k; ++newColor) {
            for(Integer vertex : vertexSet) {
                Integer oldColor = colorMap.get(vertex);
                if(newColor == oldColor) continue;
                int delta = solutionMatrix.getMovePerformance(vertex, oldColor, newColor);
                if(delta == conflictNumber) {
                    return new Move(vertex, newColor);
                }

                if(tabuStructure.isInTabuMatrix(vertex, newColor)) continue;
                if(delta > max) {
                    potentialMoves.clear();
                    potentialMoves.add(new Move(vertex, newColor));
                    max = delta;
                } else if(delta == max) {
                    potentialMoves.add(new Move(vertex, newColor));
                }
            }
        }
        return potentialMoves.get(tieBreaker.nextInt(potentialMoves.size()));
    }
}
