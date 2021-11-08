package datastructures.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.Comparator;

@Data
@Builder
public class PriorityVertex implements Comparator<PriorityVertex> {
    private int priority;
    private int vertexNumber;

    @Override
    public int compare(PriorityVertex o1, PriorityVertex o2) {
        if(o1.priority == o2.priority) {
            return 0;
        }

        return o1.priority > o2.priority ? 1 : -1;
    }
}
