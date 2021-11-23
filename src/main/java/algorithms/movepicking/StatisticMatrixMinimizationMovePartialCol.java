package algorithms.movepicking;

import algorithms.interfaces.MovePickingStrategy;
import datastructures.SolutionMatrix;
import datastructures.StatisticMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StatisticMatrixMinimizationMovePartialCol implements MovePickingStrategy {
    private final Set<Integer> unsolved;
    private final int k;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private final StatisticMatrix statisticMatrix;

    public StatisticMatrixMinimizationMovePartialCol(Set<Integer> unsolved, int k, SolutionMatrix solutionMatrix, TabuStructure tabuStructure, StatisticMatrix matrix) {
        this.unsolved = unsolved;
        this.k = k;
        this.solutionMatrix = solutionMatrix;
        this.tabuStructure = tabuStructure;
        this.statisticMatrix = matrix;
    }

    @Override
    public Move findBestMove() {
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

        if(!bestMoves.isEmpty()) {
            return findMoveWithLowestStatisticMatrixCost(bestMoves);
        } else {
            return findMoveWithLowestStatisticMatrixCost(moves);
        }
    }

    private Move findMoveWithLowestStatisticMatrixCost(List<Move> moves) {
        int min = Integer.MAX_VALUE;
        Move targetMove = null;
        for(Move move : moves) {
            int matrixValue = statisticMatrix.getMatrixValue(k, move.getVertex() - 1, move.getColor());
            if(matrixValue < min) {
                min = matrixValue;
                targetMove = move;
            }
        }
        statisticMatrix.addMoveToMatrix(k, targetMove.getVertex() - 1, targetMove.getColor());
        return targetMove;
    }
}
