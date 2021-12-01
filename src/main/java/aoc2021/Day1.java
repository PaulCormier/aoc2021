package aoc2021;

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

        // There should be 7 increases
        System.out.printf("There were %d increases in the real data.%n", increases);
    }
}