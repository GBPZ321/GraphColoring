package algorithms.partialcol;

import algorithms.interfaces.MovePickingStrategy;
import algorithms.interfaces.Subroutine;
import algorithms.movepicking.PartialColFeasibleMove;
import datastructures.pojo.SolutionWithStatus;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.Move;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.*;

public class PartialColSubroutine implements Subroutine {
    private final GraphWrapper graphWrapper;
    private final int k;
    private final int iterations;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private int previousTimesEncountered;
    private int previousSz;
    private final Set<Integer> unsolved;
    private final Map<Integer, Integer> coloring;
    private final MovePickingStrategy movePickingStrategy;

    public PartialColSubroutine(GraphDefinition graphDefinition, int k, float alpha, int l, int iterationTimeout) {
        this.graphWrapper = graphDefinition.getGraphWrapper();
        this.k = k;
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
        this.previousTimesEncountered = 0;
        this.previousSz = 0;
        PartialColStartingColoring startingColoring = new PartialColStartingColoring(graphWrapper, k);
        PartialColStarterKit starterKit = startingColoring.getColoring();
        this.coloring = starterKit.getStartingColorings();
        this.unsolved = starterKit.getU();
        this.solutionMatrix = new SolutionMatrix(coloring, k, graphWrapper);
        this.movePickingStrategy = new PartialColFeasibleMove(unsolved, k, solutionMatrix, tabuStructure);
    }

    public SolutionWithStatus findSolution() {
        int runs = iterations;

        SolutionWithStatus solutionWithStatus = new SolutionWithStatus();
        while(true) {
            runs--;
            if(runs == 0) {
                solutionWithStatus.setStatus(ColoringStatus.TIMEOUT);
                return solutionWithStatus;
            }
            if(unsolved.isEmpty()) {
                solutionWithStatus.setStatus(ColoringStatus.SATISFIED);
                solutionWithStatus.setSolution(new GraphSolution(coloring, k));
                return solutionWithStatus;
            }
            previousSz = unsolved.size();
            findBestMoveAndUpdateMatrices(coloring, unsolved);
            if(previousSz == unsolved.size()) {
                previousTimesEncountered++;
            } else {
                previousTimesEncountered = 0;
            }
        }

    }

    private void findBestMoveAndUpdateMatrices(Map<Integer, Integer> coloring, Set<Integer> unsolved) {
        Move bestMove = movePickingStrategy.findBestMove();
        tabuStructure.insertTabuColor(previousTimesEncountered, bestMove.getVertex(), bestMove.getColor());
        coloring.put(bestMove.getVertex(), bestMove.getColor());
        solutionMatrix.updateCForPartialCol(bestMove.getVertex(), bestMove.getColor());
        List<Integer> neighborsOfV = graphWrapper.getNeighborsOfV(bestMove.getVertex());
        for(Integer n : neighborsOfV) {
            if(!coloring.containsKey(n)) continue;
            if(coloring.get(n).equals(bestMove.getColor())) {
                coloring.remove(n);
                unsolved.add(n);
            }
        }
        unsolved.remove(bestMove.getVertex());
    }

}
