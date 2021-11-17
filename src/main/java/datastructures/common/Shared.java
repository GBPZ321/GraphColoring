package datastructures.common;

import graph.solution.GraphSolution;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Shared {
    private GraphSolution solution;
    private final ReadWriteLock readWriteLock;


    public Shared(int vertices) {
        this.solution = new GraphSolution(null, vertices);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Update the shared solutions
     * @param ps - the coloring you think is lower.
     */
    public void updateSolution(GraphSolution ps) {
        readWriteLock.writeLock().lock();
        if(ps.getK() < solution.getK()) {
            this.solution = ps;
        }
        readWriteLock.writeLock().unlock();


    }

    /**
     * Get the current minimum.
     * @return - the current minimum.
     */
    public GraphSolution getCurrentMinimum() {
        readWriteLock.readLock().lock();
        try {
            return solution;
        }
        finally {
            readWriteLock.readLock().unlock();
        }
    }
}
