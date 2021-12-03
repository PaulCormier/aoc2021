package aoc2021;

import java.util.List;

/**
 * https://adventofcode.com/2021/day/3
 * 
 * @author Paul Cormier
 *
 */
public class Day3 {

    private static final String INPUT_TXT = "Input-Day3.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day3.txt";

    public static void main(String[] args) {

        // Read the test file
        List<String> testReadings = FileUtils.readFile(TEST_INPUT_TXT);

        int testPowerConsuption = calculatePowerConsumption(testReadings);

        System.out.printf("The power consuption of the submarine is: %d%n", testPowerConsuption);

        List<String> readings = FileUtils.readFile(INPUT_TXT);

        int powerConsuption = calculatePowerConsumption(readings);

        System.out.printf("The power consuption of the submarine is: %d%n", powerConsuption);

    }

    private static int calculatePowerConsumption(List<String> readings) {
        // Count the occurrences of the digits
        int[] occurrences = new int[readings.get(0).length()];
        for (String reading : readings) {
            for (int i = 0; i < occurrences.length; i++)
                occurrences[i] += reading.charAt(i) == '1' ? 1 : 0;
        }

        // Calculate rates
        int gammaRate = 0;
        int epsilonRate = 0;
        int halfLength = readings.size() / 2;

        // Compute each digit, then shift them all up
        for (int i = 0; i < occurrences.length; i++) {
            gammaRate <<= 1;
            epsilonRate <<= 1;
            gammaRate += occurrences[i] > halfLength ? 1 : 0;
            epsilonRate += occurrences[i] < halfLength ? 1 : 0;
        }

        System.out.printf("The gamma rate of the submarie is: %d, and the epsilon rate is: %d.%n", gammaRate, epsilonRate);

        return gammaRate * epsilonRate;
    }

}