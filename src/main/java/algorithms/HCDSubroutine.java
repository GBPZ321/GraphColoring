package algorithms;

import datastructures.pojo.PriorityVertex;
import graph.definition.GraphWrapper;
import org.jgrapht.Graph;

import java.util.*;

public class HCDSubroutine {
    private int iterations;
    private float alpha;
    private int upperBound;
    private final Map<Integer, Integer> coloring;
    private final PriorityQueue<PriorityVertex> priority;
    private final Set<Integer> vertexSet;
    private GraphWrapper wrapper;

    public HCDSubroutine() {
        iterations = 1000;
        vertexSet = new HashSet<>();
        coloring = new HashMap<>();
        priority = new PriorityQueue<>();
        initialize();
    }

    private void initialize() {
        for(int vertex : wrapper.getGraph().vertexSet()) {
            int color = vertex - 1;
            coloring.put(vertex, color);
            priority.add(PriorityVertex.builder()
                    .priority(color)
                    .vertexNumber(vertex)
                    .build());
        }
        upperBound = wrapper.getVertexSize();
    }

    private void run() {
        while(gasStillInTank()) {
            pullColors();
        }
    }

    private void pullColors() {
        PriorityVertex vertex = priority.peek();
        if(Objects.isNull(vertex)) return;
        for(int k = 0; k < upperBound; ++k) {
            if(isFeasibleWithColorK(vertex.getVertexNumber(), k)) {
                priority.remove();
                coloring.put(vertex.getVertexNumber(), k);

            }
        }
    }

    private boolean isFeasibleWithColorK(int vertex, int k) {
        for(int adj : wrapper.getNeighborsOfV(vertex)) {
            if(coloring.get(adj).equals(k)) {
                return false;
            }
        }
        return true;
    }

    private Integer chooseNodeHavingLowestPriority() {
        return 3;
    }

    private boolean gasStillInTank() {
        return iterations > 0;
    }
}
