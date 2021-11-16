package algorithms.partialcol;

import graph.definition.GraphWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class PartialColStartingColoring {
    private final Integer k;
    private final GraphWrapper graph;

    public PartialColStartingColoring(GraphWrapper graph, int k) {
        this.graph = graph;
        this.k = k;
    }

    public PartialColStarterKit getColoring() {
        Map<Integer, Integer> coloring = new HashMap<>();
        Set<Integer> u = new HashSet<>();
        int numProcessed = 0;
        for(Integer vertex : graph.getVertices()) {
            if(numProcessed < k) {
                int coloringForVertex = (vertex - 1) % k;
                coloring.put(vertex, coloringForVertex);
            } else {
                boolean ableToSet = false;
                for(int kTest = 0; kTest < k; kTest++) {
                    if(canChangeVtoC(coloring, vertex, kTest)) {
                        coloring.put(vertex, kTest);
                        ableToSet = true;
                        break;
                    }
                }
                if(!ableToSet) {
                    u.add(vertex);
                }
            }
            numProcessed++;
        }
        return new PartialColStarterKit(coloring, u);
    }

    private boolean canChangeVtoC(Map<Integer, Integer> coloring, int v, int c) {
        for(int n : graph.getNeighborsOfV(v)) {
            if(!coloring.containsKey(n)) continue;
            if(c == coloring.get(n)) {
                return false;
            }
        }
        return true;
    }

}
