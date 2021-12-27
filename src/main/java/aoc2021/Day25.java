package aoc2021;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/25
 * 
 * @author Paul Cormier
 *
 */
public class Day25 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day25.class);

    private static final String INPUT_TXT = "Input-Day25.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day25.txt";

    private static final String TEST_INPUT_2_TXT = "TestInput-Day25 - 2.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        int[][] testMap = FileUtils.readFileToStream(TEST_INPUT_2_TXT)
                                   .map(l -> l.chars().map(i -> i == '.' ? 0 : (i == '>' ? 1 : -1)).toArray())
                                   .collect(Collectors.toList())
                                   .toArray(new int[0][0]);
        log.debug("Initial state:\n{}", printMap(testMap));

        log.info("{}", part1(testMap));

        // Read the test file
        testMap = FileUtils.readFileToStream(TEST_INPUT_TXT)
                           .map(l -> l.chars().map(i -> i == '.' ? 0 : (i == '>' ? 1 : -1)).toArray())
                           .collect(Collectors.toList())
                           .toArray(new int[0][0]);
        log.debug("Initial state:\n{}", printMap(testMap));

        log.info("The sea cucumbers stop moving after {} steps in the test data.", part1(testMap));

        log.setLevel(Level.INFO);

        // Read the real file
        int[][] map = FileUtils.readFileToStream(INPUT_TXT)
                               .map(l -> l.chars().map(i -> i == '.' ? 0 : (i == '>' ? 1 : -1)).toArray())
                               .collect(Collectors.toList())
                               .toArray(new int[0][0]);

        log.info("The sea cucumbers stop moving after {} steps in the real data.", part1(map));

        // PART 2
        // No part 2 for Christmas!
    }

    private static int part1(int[][] map) {

        int[][] nextStep;
        int step = 1;
        while (step < 1_000) {
            nextStep = new int[map.length][map[0].length];

            // Move the east-facing
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] > 0) {
                        if (map[i][(j + 1) % map[i].length] == 0)
                            nextStep[i][(j + 1) % map[i].length] = map[i][j];
                        else
                            nextStep[i][j] = map[i][j];
                    }
                }
            }

            // Move the south-facing
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] < 0) {
                        if (map[(i + 1) % map.length][j] >= 0 && nextStep[(i + 1) % nextStep.length][j] == 0)
                            nextStep[(i + 1) % map.length][j] = map[i][j];
                        else
                            nextStep[i][j] = map[i][j];
                    }
                }
            }
            log.debug("After {} step:\n{}", step, printMap(nextStep));

            // Check if there was a change
            if (Arrays.deepEquals(map, nextStep))
                break;

            map = nextStep;
            step++;
        }

        return step;
    }

    private static String printMap(int[][] map) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(i -> i == 0 ? "." : (i > 0 ? ">" : "v"))
                                        .collect(Collectors.joining()))
                     .collect(Collectors.joining("\n"));
    }

}