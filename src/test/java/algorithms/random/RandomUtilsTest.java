package algorithms.random;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilsTest {

    @Test
    void generateRandom() throws Exception {
        for(int i = 0; i < 100000; i++) {
            int x = RandomUtils.generateRandom(0, 10, Collections.singletonList(1));
            System.out.println(x);
        }
    }
}