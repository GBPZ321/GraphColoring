package algorithms.movepicking;

import algorithms.interfaces.MovePickingStrategy;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.Move;

import java.util.*;

public class SolutionMatrixMinimizationMove implements MovePickingStrategy {
    private final Set<Integer> vertexSet;
    private final Map<Integer, Integer> colorMap;
    private final int k;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private static final Random tieBreaker = new Random();

    public SolutionMatrixMinimizationMove(Set<Integer> vertexSet, Map<Integer, Integer> colorMap, int k, SolutionMatrix solutionMatrix, TabuStructure tabuStructure) {
        this.vertexSet = vertexSet;
        this.colorMap = colorMap;
        this.k = k;
        this.solutionMatrix = solutionMatrix;
        this.tabuStructure = tabuStructure;
    }

    @Override
    public Move findBestMove() {
        int max = -vertexSet.size();
        int conflictNumber = solutionMatrix.getConflictNumber();
        List<Move> potentialMoves = new ArrayList<>();
        List<Move> allMoves = new ArrayList<>();
        for(int newColor = 0; newColor < k; ++newColor) {
            for(Integer vertex : vertexSet) {
                Integer oldColor = colorMap.get(vertex);
                if(newColor == oldColor) continue;
                int delta = solutionMatrix.getMovePerformance(vertex, oldColor, newColor);
                Move newMove = new Move(vertex, newColor);
                if(delta == conflictNumber) {
                    return newMove;
                }
                allMoves.add(newMove);
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
            return allMoves.get(tieBreaker.nextInt(allMoves.size()));
        }

        return potentialMoves.get(tieBreaker.nextInt(potentialMoves.size()));
    }
}
