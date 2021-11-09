package datastructures.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple class for providing a wrapper around a coloring move.
 * That is to say, I am moving vertex X to color Y.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    private Integer vertex;
    private Integer color;
}
