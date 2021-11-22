package algorithms.random;

import algorithms.genetic.Chromosome;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomUtils {
    private static final Random rand = new Random();

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int generateRandom(int start, int end, List<Integer> excludeRows) {
        int range = end - start;

        int random = rand.nextInt(range);
        while(excludeRows.contains(random)) {
            random = rand.nextInt(range);
        }

        return random;
    }

    public static List<Chromosome> pickRandom(List<Chromosome> list, int n) {
        if (n > list.size()) {
            throw new IllegalArgumentException("not enough elements");
        }
        Random random = new Random();
        return IntStream
                .generate(() -> random.nextInt(list.size()))
                .distinct()
                .limit(n)
                .mapToObj(list::get)
                .collect(Collectors.toList());
    }

    public static List<Double> getNRandomDoublesInRange(double low, double high, int n) {
        return
                rand.doubles(low, high)
                        .distinct()
                        .limit(n)
                        .boxed()
                        .collect(Collectors.toList());
    }

    public static List<Integer> getNRandomIntsInRange(int low, int high, int n) {
        return
                rand.ints(low, high)
                        .limit(n)
                        .boxed()
                        .collect(Collectors.toList());
    }
}
