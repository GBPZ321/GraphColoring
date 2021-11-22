package algorithms;

import datastructures.common.Shared;
import graph.solution.GraphSolution;

import java.util.Objects;

public abstract class BaseCooperative {
    protected Shared shared;
    protected boolean sharedFinish = false;

    public BaseCooperative(Shared shared) {
        this.shared = shared;
    }

    public BaseCooperative() {
    }

    public boolean isGlobalKLower(int k) {
        if(Objects.isNull(shared)) {
            return false;
        }
        return shared.getCurrentMinimum().getK() < k;
    }

    protected void updateSolution(GraphSolution solution) {
        if(!Objects.isNull(shared)) {
            shared.updateSolution(solution);
        }
    }

    protected boolean isFinished(int k) {
        System.out.println("Checking finish status");
        if(Objects.isNull(shared)) {
            return true;
        }

        System.out.println("Not null");

        if(!this.sharedFinish) {
            this.sharedFinish = true;
        }

        System.out.println("Not already finished");

        shared.updateTimeout();

        shared.allTimedout();
        int globalK = shared.getCurrentMinimum().getK(); // get global min.
        if(globalK < k) {
            System.out.println("new life");
            return false;
        } else {
            System.out.println("No new life");
            return true;
        }
    }
}
