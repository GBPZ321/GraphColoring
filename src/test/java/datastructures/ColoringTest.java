package datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ColoringTest {
    private Coloring coloring;
    private Map<Integer, Integer> actualColoring;

    @BeforeEach
    void setUp() {
        actualColoring = new HashMap<>();
        actualColoring.put(1, 0);
        actualColoring.put(2, 1);
        actualColoring.put(3, 0);
        coloring = new Coloring(actualColoring);
    }

    @Test
    void getNumberColors() {
        assertEquals(2, coloring.getNumberColors());
    }

    @Test
    void getColors() {
        assertEquals(actualColoring, coloring.getColors());
    }

    @Test
    void getColorClasses() {
        assertThrows(RuntimeException.class, () -> {
            coloring.getColorClasses();
        });
    }
}