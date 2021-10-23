package utility;

import java.util.Random;

public class RandomUtility {
    public static int getRandomNumberInRangeInclusive(Random r, int low, int high) {
        return r.nextInt(high - low) + low;
    }
}
