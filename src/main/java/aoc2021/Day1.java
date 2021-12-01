package aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://adventofcode.com/2021/day/1
 * 
 * @author Paul Cormier
 *
 */
public class Day1 {

    private static final String DAY1_INPUT_TXT = "Input-Day1.txt";

    private static final String DAY1_TEST_INPUT_TXT = "TestInput-Day1.txt";

    /**
     * How many measurements are larger than the previous measurement?
     * 
     * @param values
     *            The measurements to evaluate.
     * @return The number of measurements that are larger than the previous.
     */
    private static int countIncreases(List<Integer> values) {
        int increases = 0;
        int lastValue = Integer.MAX_VALUE;
        for (int value : values) {
            if (value > lastValue)
                increases++;

            lastValue = value;
        }

        return increases;
    }

    public static void main(String[] args) {

        // Read the test file
        List<Integer> testReadings = FileUtils.readFileToStream(DAY1_TEST_INPUT_TXT)
                                              .map(Integer::valueOf)
                                              .collect(Collectors.toList());

        // Count the measurements
        int testIncreases = countIncreases(testReadings);

        // There should be 7 increases
        System.out.printf("Expected 7 increases. There were %d increases.%n", testIncreases);

        // Read the input file
        List<Integer> readings = FileUtils.readFileToStream(DAY1_INPUT_TXT)
                                          .map(Integer::valueOf)
                                          .collect(Collectors.toList());

        // Count the measurements
        int increases = countIncreases(readings);

        // Real number of increases
        System.out.printf("There were %d increases in the real data.%n", increases);

        // PART 2

        // Compute 3-value sliding sums
        List<Integer> summedTestReadings = new ArrayList<>();
        Integer[] testReadingArray = testReadings.toArray(new Integer[0]);
        for (int i = 2; i < testReadingArray.length; i++) {
            summedTestReadings.add(testReadingArray[i] + testReadingArray[i - 1] + testReadingArray[i - 2]);
        }

        testIncreases = countIncreases(summedTestReadings);

        // There should be 5 increases
        System.out.printf("Expected 5 increases of sums. There were %d increases.%n", testIncreases);

        // Compute 3-value sliding sums
        List<Integer> summedReadings = new ArrayList<>();
        Integer[] readingArray = readings.toArray(new Integer[0]);
        for (int i = 2; i < readingArray.length; i++) {
            summedReadings.add(readingArray[i] + readingArray[i - 1] + readingArray[i - 2]);
        }

        increases = countIncreases(summedReadings);

        // Real number of increases
        System.out.printf("There were %d increases in the sums in the real data.%n", increases);
    }
}