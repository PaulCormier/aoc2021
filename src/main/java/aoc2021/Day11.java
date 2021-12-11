package aoc2021;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/11
 * 
 * @author Paul Cormier
 *
 */
public class Day11 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day11.class);

    private static final String INPUT_TXT = "Input-Day11.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day11.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        log.trace(testLines.toString());

        // Create the grid of octopuses
        int[][] testOctopuses = testLines.stream()
                                         .map(l -> l.chars()
                                                    .map(Character::getNumericValue)
                                                    .toArray())
                                         .collect(Collectors.toList())
                                         .toArray(new int[10][10]);

        log.info("After {} steps there have been {} flashes with the test data.", 10, part1(testOctopuses, 10));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        int[][] octopuses = lines.stream()
                                 .map(l -> l.chars()
                                            .map(Character::getNumericValue)
                                            .toArray())
                                 .collect(Collectors.toList())
                                 .toArray(new int[10][10]);

        log.info("After {} steps there have been {} flashes with the real data.", 100, part1(octopuses, 100));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        log.info("{}", part2(lines));
    }

    /**
     * Determine how many total flashes are there after a certain number of steps.
     * 
     * @param octopuses The grid of octopuses.
     * @param steps The number of steps to take.
     * @return The number of flashes after a certain number of steps.
     */
    private static int part1(int[][] octopuses, int steps) {
        log.debug("Before any steps:\n{}\n", printMap(octopuses));

        int flashes = 0;

        for (int step = 1; step <= steps; step++) {
            // Advance the step
            for (int i = 0; i < octopuses.length; i++) {
                for (int j = 0; j < octopuses[i].length; j++) {
                    if (++octopuses[i][j] > 9) {
                        // Process flash
                        processFlash(octopuses, i, j);
                    }

                }
            }

            // Count the flashes
            flashes += countFlashes(octopuses);

            log.debug("After step {}:\n{}\n", step, printMap(octopuses));
        }

        return flashes;
    }

    /**
     * Process a flash at a given position in the grid.
     * 
     * @param octopuses The grid of octopuses.
     * @param row The row index.
     * @param col The column index.
     */
    private static void processFlash(int[][] octopuses, int row, int col) {
        // Mark the octopus as having flashed
        octopuses[row][col] = Integer.MIN_VALUE;

        for (int i = Math.max(0, row - 1); i < Math.min(octopuses.length, row + 2); i++) {
            for (int j = Math.max(0, col - 1); j < Math.min(octopuses[i].length, col + 2); j++) {
                if (++octopuses[i][j] > 9)
                    processFlash(octopuses, i, j);
            }
        }
    }

    private static int countFlashes(int[][] octopuses) {
        int flashes = 0;
        for (int i = 0; i < octopuses.length; i++) {
            for (int j = 0; j < octopuses[i].length; j++) {
                if (octopuses[i][j] < 0) {
                    octopuses[i][j] = 0;
                    flashes++;
                }
            }
        }
        return flashes;
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

    private static String printMap(final int[][] octopuses) {
        String normalColour = "\033[38;2;200;200;200m";
        String highlightColour = "\033[38;2;0;0;0m";
        return normalColour +
               Stream.of(octopuses)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(Integer::toString)
                                        .map(s -> {
                                            return "0".equals(s)
                                                    ? highlightColour
                                                      + s
                                                      + normalColour
                                                    : s;
                                        })
                                        .collect(Collectors.joining()))
                     .collect(Collectors.joining("\n"))
               +
               highlightColour;
    }
}