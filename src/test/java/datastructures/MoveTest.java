package datastructures;

import datastructures.pojo.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoveTest {
    private Move move;

    @BeforeEach
    void setUp() {
        move = new Move();
    }

    @Test
    void getVertex() {
        move.setVertex(1);
    }

    @Test
    void getColor() {
    }

    @Test
    void setVertex() {
    }

    @Test
    void setColor() {
    }
}