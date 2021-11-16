package algorithms.partialcol;

import algorithms.common.SolutionWithStatus;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.ColoringStatus;
import datastructures.pojo.Move;
import graph.definition.GraphDefinition;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.util.*;

public class PartialColSubroutine {
    private final GraphDefinition graphDefinition;
    private final int k;
    private final int iterations;
    private SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private final Random tieBreaker;
    private int previousTimesEncountered;
    private int previousSz;

    public PartialColSubroutine(GraphDefinition graphDefinition, int k, float alpha, int l, int iterationTimeout) {
        this.graphDefinition = graphDefinition;
        this.k = k;
        this.iterations = iterationTimeout;
        this.tabuStructure = new TabuStructure(l, alpha);
        this.tieBreaker = new Random();
        this.previousTimesEncountered = 0;
        this.previousSz = 0;
    }

    public SolutionWithStatus findSolution() {
        int runs = iterations;
        PartialColStartingColoring startingColoring = new PartialColStartingColoring(graphDefinition.getGraphWrapper(), k);
        PartialColStarterKit starterKit = startingColoring.getColoring();
        Map<Integer, Integer> coloring = starterKit.getStartingColorings();
        Set<Integer> unsolved = starterKit.getU();
        solutionMatrix = new SolutionMatrix(coloring, k, graphDefinition);
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
        int max = Integer.MAX_VALUE;
        List<Move> bestMoves = new ArrayList<>();
        List<Move> moves = new ArrayList<>();

        for(Integer vertex : unsolved) {
            for(int kTest = 0; kTest < k; ++kTest) {
                Move move = new Move(vertex, kTest);
                moves.add(move);
                int costOfMove = unsolved.size() + solutionMatrix.getMatrixEntry(vertex - 1, kTest) - 1;
                if(costOfMove == 0) {
                    bestMoves.add(move);
                    break;
                }
                if(tabuStructure.isInTabuMatrix(vertex, kTest) ) {
                    continue;
                }
                if(costOfMove == max) {
                    bestMoves.add(move);
                } else if(costOfMove < max) {
                    bestMoves.clear();
                    bestMoves.add(move);
                    max = costOfMove;
                }
            }
        }

        Move bestMove = null;
        if(!bestMoves.isEmpty()) {
            bestMove = bestMoves.get(tieBreaker.nextInt(bestMoves.size()));
        } else {
            bestMove = moves.get(tieBreaker.nextInt(moves.size()));
        }
        tabuStructure.insertTabuColor(previousTimesEncountered, bestMove.getVertex(), bestMove.getColor());
        coloring.put(bestMove.getVertex(), bestMove.getColor());
        solutionMatrix.updateCForPartialCol(bestMove.getVertex(), bestMove.getColor());
        List<Integer> neighborsOfV = graphDefinition.getGraphWrapper().getNeighborsOfV(bestMove.getVertex());
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
