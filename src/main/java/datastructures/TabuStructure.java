package datastructures;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class TabuStructure {
    private final Deque<TabuColor> forbiddenQueue;
    private final Map<Integer, Map<Integer, Boolean>> tabuColorMap;
    private final int L;
    private final int alpha;

    public TabuStructure(int L, int alpha) {
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
            tabuColorMap.put(vertex, new HashMap<>());
        }
        tabuColorMap.get(vertex).put(color, true);

        int maxSize = L + conflictNumber * alpha;
        while (forbiddenQueue.size() > maxSize) {
            TabuColor remove = forbiddenQueue.removeLast();
            tabuColorMap.get(remove.getVertex()).remove(remove.getColor());
        }
    }

    public boolean isInTabuMatrix(int vertex, int color) {
        Map<Integer, Boolean> colorConfirmation = tabuColorMap.get(vertex);
        if(Objects.isNull(colorConfirmation)) return false;
        Boolean isPresent = colorConfirmation.get(color);
        if(Objects.isNull(isPresent)) return false;
        return isPresent;
    }
}
