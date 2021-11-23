package datastructures.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveWithCost {
    private Move move;
    private int cost;
}
