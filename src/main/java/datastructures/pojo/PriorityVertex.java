package datastructures.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * Vertex with the notion of priority.
 */
@Data
@Builder
public class PriorityVertex implements Comparable<PriorityVertex> {
    private float priority;
    private int vertexNumber;

    @Override
    public int compareTo(PriorityVertex o2) {
        PriorityVertex o1 = this;
        if(o1.priority == o2.priority) {
            return 0;
        }

        return o1.priority > o2.priority ? 1 : -1;
    }
}
