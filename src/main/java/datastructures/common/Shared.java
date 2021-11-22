package datastructures.common;

import graph.solution.GraphSolution;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Shared {
    private GraphSolution solution;
    private final ReadWriteLock readWriteLock;
    private final int totalThreads;
    private int totalTimedOut;

    public Shared(int vertices, int totalThreads) {
        this.solution = new GraphSolution(null, vertices);
        this.readWriteLock = new ReentrantReadWriteLock();
        this.totalThreads = totalThreads;
        this.totalTimedOut = 0;
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

    public synchronized void updateTimeout() {
        totalTimedOut++;
    }

    public synchronized void allTimedout() {
        while(totalThreads != totalTimedOut) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        notifyAll();
        return;
    }



}
