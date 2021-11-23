package algorithms.movepicking;

import algorithms.interfaces.MovePickingStrategy;
import datastructures.SolutionMatrix;
import datastructures.StatisticMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.Move;
import datastructures.pojo.MoveWithCost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatisticMatrixMinimizationMove implements MovePickingStrategy {
    private final Set<Integer> vertexSet;
    private final Map<Integer, Integer> colorMap;
    private final int k;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private final StatisticMatrix statisticMatrix;
    public StatisticMatrixMinimizationMove(Set<Integer> vertexSet, Map<Integer, Integer> colorMap, int k, SolutionMatrix solutionMatrix, TabuStructure tabuStructure, StatisticMatrix statisticMatrix) {
        this.vertexSet = vertexSet;
        this.colorMap = colorMap;
        this.k = k;
        this.solutionMatrix = solutionMatrix;
        this.tabuStructure = tabuStructure;
        this.statisticMatrix = statisticMatrix;
    }


    @Override
    public Move findBestMove() {
        int max = -vertexSet.size();
        int conflictNumber = solutionMatrix.getConflictNumber();
        List<Move> potentialMoves = new ArrayList<>();
        List<MoveWithCost> allMoves = new ArrayList<>();
        for(int newColor = 0; newColor < k; ++newColor) {
            for(Integer vertex : vertexSet) {
                Integer oldColor = colorMap.get(vertex);
                if(newColor == oldColor) continue;
                int delta = solutionMatrix.getMovePerformance(vertex, oldColor, newColor);
                Move newMove = new Move(vertex, newColor);
                if(delta == conflictNumber) {
                    return newMove;
                }
                allMoves.add(new MoveWithCost(newMove, delta));
                if(tabuStructure.isInTabuMatrix(vertex, newColor)) continue;
                if(delta > max) {
                    potentialMoves.clear();
                    potentialMoves.add(newMove);
                    max = delta;
                } else if(delta == max) {
                    potentialMoves.add(newMove);
                }
            }
        }

        if(potentialMoves.isEmpty()) {
            List<Move> newLowerBoundForMoves = new ArrayList<>();
            int min = -vertexSet.size();
            for(MoveWithCost moveWithCost : allMoves) {
                if(moveWithCost.getCost() > min) {
                    newLowerBoundForMoves.clear();
                    newLowerBoundForMoves.add(moveWithCost.getMove());
                } else if(moveWithCost.getCost() == min){
                    newLowerBoundForMoves.add(moveWithCost.getMove());
                }
            }
            return findMoveWithLowestStatisticMatrixCost(newLowerBoundForMoves);
        }

        return findMoveWithLowestStatisticMatrixCost(potentialMoves);
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
