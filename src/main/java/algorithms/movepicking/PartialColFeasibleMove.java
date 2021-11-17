package algorithms.movepicking;

import algorithms.interfaces.MovePickingStrategy;
import datastructures.SolutionMatrix;
import datastructures.TabuStructure;
import datastructures.pojo.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PartialColFeasibleMove implements MovePickingStrategy {
    private final Set<Integer> unsolved;
    private final int k;
    private final SolutionMatrix solutionMatrix;
    private final TabuStructure tabuStructure;
    private static final Random tieBreaker = new Random();

    public PartialColFeasibleMove(Set<Integer> unsolved, int k, SolutionMatrix solutionMatrix, TabuStructure tabuStructure) {
        this.unsolved = unsolved;
        this.k = k;
        this.solutionMatrix = solutionMatrix;
        this.tabuStructure = tabuStructure;
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

        Move bestMove = null;
        if(!bestMoves.isEmpty()) {
            bestMove = bestMoves.get(tieBreaker.nextInt(bestMoves.size()));
        } else {
            bestMove = moves.get(tieBreaker.nextInt(moves.size()));
        }
        return bestMove;
    }
}
