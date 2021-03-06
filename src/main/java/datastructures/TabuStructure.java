package datastructures;

import datastructures.pojo.Move;

import java.util.*;

public class TabuStructure {
    private final Deque<Move> forbiddenQueue;
    private final Map<Integer, Set<Integer>> tabuColorMap;
    private final int L;
    private final double alpha;

    public TabuStructure(int L, double alpha) {
        forbiddenQueue = new LinkedList<>();
        tabuColorMap = new HashMap<>();
        this.L = L;
        this.alpha = alpha;
    }

    public void insertTabuColor(int conflictNumber, int vertex, int color) {
        Move newColor = new Move();
        newColor.setColor(color);
        newColor.setVertex(vertex);
        forbiddenQueue.add(newColor);
        if(!tabuColorMap.containsKey(vertex)) {
            tabuColorMap.put(vertex, new HashSet<>());
        }
        tabuColorMap.get(vertex).add(color);

        double maxSize = L + conflictNumber * alpha;
        int queueSize = forbiddenQueue.size();
        while (queueSize > maxSize) {
            Move remove = forbiddenQueue.removeFirst();
            tabuColorMap.get(remove.getVertex()).remove(remove.getColor());
            queueSize--;
        }
    }

    public boolean isInTabuMatrix(int vertex, int color) {
        Set<Integer> colorConfirmation = tabuColorMap.get(vertex);
        if(Objects.isNull(colorConfirmation)) return false;
        return colorConfirmation.contains(color);
    }
}
