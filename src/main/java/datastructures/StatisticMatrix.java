package datastructures;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StatisticMatrix {
    private Matrix matrix;
    private final ReadWriteLock readWriteLock;
    private int lowestK;
    private int numVert;

    public StatisticMatrix(int numVert) {
        this.matrix = null;
        this.lowestK = Integer.MAX_VALUE;
        this.numVert = numVert;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public synchronized void addMoveToMatrix(int k, int v, int c) {
        if(k > lowestK) return;
        if(k < lowestK) {
            matrix = new Matrix(numVert, k);
            lowestK = k;
        }
        matrix.increment(v, c);
    }

    public synchronized int getMatrixValue(int k, int v, int c) {
        if(k > lowestK) return 0;
        if(k < lowestK) return 0;
        if(matrix == null) {
            matrix = new Matrix(v, k);
        }
        return matrix.getValue(v, c);
    }


}
