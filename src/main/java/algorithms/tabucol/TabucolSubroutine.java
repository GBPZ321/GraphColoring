package algorithms.tabucol;

import algorithms.interfaces.MovePickingStrategy;
import algorithms.interfaces.SeedingStrategy;
import algorithms.interfaces.SharableSubroutine;
import algorithms.interfaces.Subroutine;
import algorithms.movepicking.SolutionMatrixMinimizationMove;
import algorithms.movepicking.StatisticMatrixMinimizationMove;
import datastructures.StatisticMatrix;
import datastructures.common.Shared;
import datastructures.pojo.SolutionWithStatus;
import datastructures.pojo.Move;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.ColoringStatus;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.*;

public class TabucolSubroutine extends SharableSubroutine implements Subroutine {
    private final GraphWrapper graphWrapper;
    private final int iterations;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private final Map<Integer, Integer> startingColoring;
    private final MovePickingStrategy movePickingStrategy;

    public TabucolSubroutine(GraphDefinition graphDefinition, int k, double alpha, int l, int iterationTimeout, SeedingStrategy seedingStrategy, Shared shared) {
        super(shared, k);
        this.graphWrapper = graphDefinition.getGraphWrapper();
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
        this.startingColoring = seedingStrategy.getStartingColoring();
        this.solutionMatrix = new SolutionMatrix(startingColoring, k, graphWrapper);
        this.movePickingStrategy = new SolutionMatrixMinimizationMove(graphWrapper.getVertices(), startingColoring, k, solutionMatrix, tabuStructure);
    }

    public TabucolSubroutine(GraphDefinition graphDefinition, int k, double alpha, int l, int iterationTimeout, SeedingStrategy seedingStrategy, StatisticMatrix matrix, Shared shared) {
        super(shared, k);
        this.graphWrapper = graphDefinition.getGraphWrapper();
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
        this.startingColoring = seedingStrategy.getStartingColoring();
        this.solutionMatrix = new SolutionMatrix(startingColoring, k, graphWrapper);
        this.movePickingStrategy = new StatisticMatrixMinimizationMove(graphWrapper.getVertices(), startingColoring, k, solutionMatrix, tabuStructure, matrix);
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
            if(runs % 100 == 0 && lowerKFound()) {
                System.out.println("Shortcut found");
                tabucolSolution.setStatus(ColoringStatus.SATISFIED);
                tabucolSolution.setSolution(shared.getCurrentMinimum());
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
        Move move = movePickingStrategy.findBestMove();
        Integer v = move.getVertex();
        Integer oldColor = startingColoring.get(v);
        Integer replacementColor = move.getColor();
        startingColoring.put(v, replacementColor);
        solutionMatrix.updateSolution(v, oldColor, replacementColor);
        tabuStructure.insertTabuColor(conflictNumber, v, replacementColor);
    }
}
