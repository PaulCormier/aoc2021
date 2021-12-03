package aoc2021;

import java.util.ArrayList;
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

        System.out.printf("The power consumption of the test submarine is: %d%n", testPowerConsuption);

        List<String> readings = FileUtils.readFile(INPUT_TXT);

        int powerConsuption = calculatePowerConsumption(readings);

        System.out.printf("The power consumption of the submarine is: %d%n", powerConsuption);

        // PART 2

        System.out.printf("The life support rating of the test submarine is: %d%n", caluculateLifeSupportRating(testReadings));
        System.out.printf("The life support rating of the submarine is: %d%n", caluculateLifeSupportRating(readings));

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

        System.out.printf("The gamma rate of the submarine is: %d, and the epsilon rate is: %d.%n", gammaRate, epsilonRate);

        return gammaRate * epsilonRate;
    }

    private static int caluculateLifeSupportRating(List<String> readings) {

        List<String> o2Readings = new ArrayList<>(readings);
        List<String> co2Readings = new ArrayList<>(readings);

        int digit = 0;
        int maxDigit = readings.get(0).length();
        while ((o2Readings.size() > 1 || co2Readings.size() > 1) && digit < maxDigit) {
            // Count the occurrences of the digits
            int occurrences = 0;
            for (String reading : o2Readings) {
                occurrences += reading.charAt(digit) == '1' ? 1 : 0;
            }
            final int o2Occurrences = occurrences;

            occurrences = 0;
            for (String reading : co2Readings) {
                occurrences += reading.charAt(digit) == '1' ? 1 : 0;
            }
            final int co2Occurrences = occurrences;

            // Filter out the appropriate readings
            final int checkDigit = digit;
            if (o2Readings.size() > 1)
                o2Readings.removeIf(r -> r.charAt(checkDigit) == (o2Occurrences >= o2Readings.size() / 2. ? '0' : '1'));
            if (co2Readings.size() > 1)
                co2Readings.removeIf(r -> r.charAt(checkDigit) == (co2Occurrences >= co2Readings.size() / 2. ? '1' : '0'));

            //            System.err.println("Round " + (digit + 1));
            //            System.err.println("O2 readings: " + o2Readings);
            //            System.err.println("CO2 readings: " + co2Readings);
            //            System.err.println();

            digit++;
        }

        int o2Rating = Integer.parseInt(o2Readings.get(0), 2);
        int co2Rating = Integer.parseInt(co2Readings.get(0), 2);

        System.out.printf("The O2 rating is: %d, and the CO2 rating is: %d.%n", o2Rating, co2Rating);

        return o2Rating * co2Rating;
    }

}