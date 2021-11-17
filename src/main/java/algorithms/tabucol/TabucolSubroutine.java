package algorithms.tabucol;

import algorithms.interfaces.MovePickingStrategy;
import algorithms.interfaces.Subroutine;
import algorithms.movepicking.SolutionMatrixMinimizationMove;
import datastructures.pojo.SolutionWithStatus;
import algorithms.random.SimpleOrderedColoring;
import datastructures.pojo.Move;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.util.*;

public class TabucolSubroutine implements Subroutine {
    private final GraphWrapper graphWrapper;
    private final int k;
    private final int iterations;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private final Map<Integer, Integer> startingColoring;
    private final MovePickingStrategy strategy;

    public TabucolSubroutine(GraphDefinition graphDefinition, int k, float alpha, int l, int iterationTimeout) {
        this.graphWrapper = graphDefinition.getGraphWrapper();
        this.k = k;
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
        SimpleOrderedColoring rndColor = new SimpleOrderedColoring(graphDefinition.getGraphWrapper().getGraph(), k);
        VertexColoringAlgorithm.Coloring<Integer> coloring = rndColor.getColoring();
        this.startingColoring = coloring.getColors();
        this.solutionMatrix = new SolutionMatrix(startingColoring, k, graphWrapper);
        this.strategy = new SolutionMatrixMinimizationMove(graphWrapper.getVertices(), startingColoring, k, solutionMatrix, tabuStructure);
    }

    public SolutionWithStatus findSolution() {
        int runs = iterations;

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
                tabucolSolution.setSolution(new GraphSolution(startingColoring, k));
                return tabucolSolution;
            }
            findBestMoveAndUpdateMatrices(conflictNumber);
        }
    }

    private void findBestMoveAndUpdateMatrices(int conflictNumber) {
        Move move = strategy.findBestMove();
        Integer v = move.getVertex();
        Integer oldColor = startingColoring.get(v);
        Integer replacementColor = move.getColor();
        startingColoring.put(v, replacementColor);
        solutionMatrix.updateSolution(v, oldColor, replacementColor);
        tabuStructure.insertTabuColor(conflictNumber, v, replacementColor);
    }
}
