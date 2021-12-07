package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/7
 * 
 * @author Paul Cormier
 *
 */
public class Day7 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day7.class);

    private static final String INPUT_TXT = "Input-Day7.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day7.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<Integer> testLines = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                           .flatMap(l -> Stream.of(l.split(",")))
                                           .map(Integer::valueOf)
                                           .collect(Collectors.toList());
        log.trace(testLines.toString());

        part1(testLines);

        log.setLevel(Level.INFO);

        // Read the real file
        List<Integer> lines = FileUtils.readFileToStream(INPUT_TXT)
                                       .flatMap(l -> Stream.of(l.split(",")))
                                       .map(Integer::valueOf)
                                       .collect(Collectors.toList());

        part1(lines);

        // PART 2

        log.setLevel(Level.DEBUG);

        part2(testLines);

        log.setLevel(Level.INFO);

        part2(lines);

    }

    private static void part1(final List<Integer> lines) {
        int maxPosition = lines.stream().mapToInt(Integer::intValue).max().getAsInt();

        log.debug("Positions range from 0 to {}.", maxPosition);

        // Check each position
        int[] fuelConsumption = IntStream.rangeClosed(0, maxPosition)
                                         .map(i -> lines.stream().mapToInt(p -> Math.abs(p - i)).sum())
                                         .toArray();

        // Find the minimum combination
        int minPosition = Integer.MAX_VALUE;
        int minFuel = Integer.MAX_VALUE;
        for (int position = 0; position < fuelConsumption.length; position++) {
            if (fuelConsumption[position] < minFuel) {
                minFuel = fuelConsumption[position];
                minPosition = position;
            }
        }
        log.debug("Fuel comsumption: {}", Arrays.toString(fuelConsumption));

        log.info("The position which the crabs align to with the least fuel used is: {}. {} fuel used.", minPosition, minFuel);
    }

    private static void part2(final List<Integer> lines) {
        int maxPosition = lines.stream().mapToInt(Integer::intValue).max().getAsInt();

        log.debug("Positions range from 0 to {}.", maxPosition);

        // Check each position
        int[] fuelConsumption = IntStream.rangeClosed(0, maxPosition)
                                         .map(i -> lines.stream()
                                                        .peek(p -> log.debug("Move from {} to {}:", p, i))
                                                        .mapToInt(p -> IntStream.rangeClosed(1, Math.abs(p - i)).sum())
                                                        //.mapToInt(p -> (Math.abs(p - i)) / 2 * (2 * p + Math.abs(p - i))) // Math fail
                                                        .peek(f -> log.debug("{} fuel", f))
                                                        .sum())
                                         .toArray();

        // Find the minimum combination
        int minPosition = Integer.MAX_VALUE;
        int minFuel = Integer.MAX_VALUE;
        for (int position = 0; position < fuelConsumption.length; position++) {
            if (fuelConsumption[position] < minFuel) {
                minFuel = fuelConsumption[position];
                minPosition = position;
            }
        }
        log.debug("Fuel comsumption: {}", Arrays.toString(fuelConsumption));

        log.info("The position which the crabs align to with the least fuel used is: {}. {} fuel used.", minPosition, minFuel);
    }

}