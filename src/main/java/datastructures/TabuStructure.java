package datastructures;

import datastructures.pojo.TabuColor;

import java.util.*;

public class TabuStructure {
    private final Deque<TabuColor> forbiddenQueue;
    private final Map<Integer, Set<Integer>> tabuColorMap;
    private final int L;
    private final float alpha;

    public TabuStructure(int L, float alpha) {
        forbiddenQueue = new LinkedList<>();
        tabuColorMap = new HashMap<>();
        this.L = L;
        this.alpha = alpha;
    }

    public void insertTabuColor(int conflictNumber, int vertex, int color) {
        TabuColor newColor = new TabuColor();
        newColor.setColor(color);
        newColor.setVertex(vertex);
        forbiddenQueue.add(newColor);
        if(!tabuColorMap.containsKey(vertex)) {
            tabuColorMap.put(vertex, new HashSet<>());
        }
        tabuColorMap.get(vertex).add(color);

        float maxSize = L + conflictNumber * alpha;
        int queueSize = forbiddenQueue.size();
        while (queueSize > maxSize) {
            TabuColor remove = forbiddenQueue.removeFirst();
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
