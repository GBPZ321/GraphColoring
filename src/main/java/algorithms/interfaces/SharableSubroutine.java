package algorithms.interfaces;

import datastructures.common.Shared;
import graph.solution.GraphSolution;

import java.util.Objects;

public abstract class SharableSubroutine {
    protected final Shared shared;
    protected final int k;

    public SharableSubroutine(Shared shared, int k) {
        this.shared = shared;
        this.k = k;
    }

    protected boolean lowerKFound() {
        if(Objects.isNull(shared)) return false;
        GraphSolution currentMinimum = shared.getCurrentMinimum();
        return currentMinimum.getK() < k;
    }
}
