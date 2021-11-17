package algorithms;

import algorithms.interfaces.ColoringHeuristic;
import datastructures.pojo.PriorityVertex;
import graph.definition.GraphWrapper;
import graph.solution.GraphSolution;

import java.util.*;

public class HCDHeuristic implements ColoringHeuristic {
    private static final int STARTING_ITERATIONS = 1000;
    private int iterations;
    private int upperBound;
    private final Map<Integer, Integer> coloring;
    private final PriorityQueue<PriorityVertex> vPrimeQueue;
    private final Map<Integer, Float> priorityMap;
    private Map<Integer, Integer> finalColoring;
    private final GraphWrapper wrapper;

    public HCDHeuristic(GraphWrapper graphWrapper) {
        iterations = STARTING_ITERATIONS;
        coloring = new HashMap<>();
        vPrimeQueue = new PriorityQueue<>();
        priorityMap = new HashMap<>();
        wrapper = graphWrapper;
        initialize();
    }

    private void initialize() {
        for(int vertex : wrapper.getGraph().vertexSet()) {
            coloring.put(vertex, vertex); // Coloring has to be one based here.
            priorityMap.put(vertex, (float) vertex);
            PriorityVertex priorityVertex = PriorityVertex.builder()
                    .priority(vertex)
                    .vertexNumber(vertex)
                    .build();
            vPrimeQueue.add(priorityVertex);
        }
        upperBound = wrapper.getVertexSize();
    }

    private void run() {
        while(notFinished()) {
            iterations--;
            pullColors();
        }
    }

    private void pullColors() {
        PriorityVertex vertex = vPrimeQueue.peek(); // Choose the node i in V' having the highest priority
        if(Objects.isNull(vertex)) return;
        for(int k = 1; k <= upperBound; ++k) { //Assign to the node i chosen, the lowest admissible color c
            if(isFeasibleWithColorK(vertex.getVertexNumber(), k)) {
                coloring.put(vertex.getVertexNumber(), k);
                priorityMap.put(vertex.getVertexNumber(), (float) k); // pi = c
                break;
            }
        }
        vPrimeQueue.remove(); // V' = V' \ {i}
        if(vPrimeQueue.isEmpty()) {
            updateUpperBound();
            pushColors();
        }
    }

    private void updateUpperBound() {
        int oldUpperBound = upperBound;
        Optional<Integer> max = coloring.values().stream().max(Integer::compareTo);
        max.ifPresent(integer -> upperBound = integer);
        finalColoring = new HashMap<>(coloring);
        if(oldUpperBound != upperBound) {
            iterations = STARTING_ITERATIONS;
        }
    }

    private void pushColors() {
        boolean changed = false;
        for(int vertex : wrapper.getVertices()) {
            int highestFound = coloring.get(vertex);
            for(int i = highestFound + 1; i <= upperBound; ++i) {
                if(isFeasibleWithColorK(vertex, i)) {
                    changed = true;
                    coloring.put(vertex, i);
                    priorityMap.put(vertex, 1.0f / i);
                }
            }
        }
        if(!changed) {
            updateUpperBound();
            popColors();
        } else {
            vPrimeEqualsV();
        }
    }

    //V' = V
    private void vPrimeEqualsV() {
        vPrimeQueue.clear();
        priorityMap.forEach((key, value) -> vPrimeQueue.add(PriorityVertex.builder()
                .vertexNumber(key)
                .priority(value)
                .build()));
    }

    private void popColors() {
        for(int vertex : wrapper.getVertices()) {
            if(coloring.get(vertex) == 1) {
                coloring.put(vertex, this.upperBound + 1);
                priorityMap.put(vertex, 1f/coloring.get(vertex));
            } else {
                priorityMap.put(vertex, Float.valueOf(coloring.get(vertex)));
            }
        }
        vPrimeEqualsV();
    }

    private boolean isFeasibleWithColorK(int vertex, int k) {
        for(int adj : wrapper.getNeighborsOfV(vertex)) {
            if(coloring.get(adj).equals(k)) {
                return false;
            }
        }
        return true;
    }

    private boolean notFinished() {
        return iterations > 0;
    }

    @Override
    public GraphSolution getColoring() {
        this.run();
        return new GraphSolution(finalColoring, upperBound);
    }

    @Override
    public String getName() {
        return "HCD";
    }
}
