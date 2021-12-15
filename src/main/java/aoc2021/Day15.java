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
 * https://adventofcode.com/2021/day/15
 * 
 * @author Paul Cormier
 *
 */
public class Day15 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day15.class);

    private static final String INPUT_TXT = "Input-Day15.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day15.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        int[][] testMap = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                   .map(l -> l.chars().map(i -> i - '0').toArray())
                                   .collect(Collectors.toList())
                                   .toArray(new int[0][0]);
        log.trace(printMap(testMap));

        log.info("The length of the path with the lowest risk level in the test data is: {}.", part1(testMap));

        log.setLevel(Level.INFO);

        // Read the real file
        int[][] map = FileUtils.readFileToStream(INPUT_TXT)
                               .map(l -> l.chars().map(i -> i - '0').toArray())
                               .collect(Collectors.toList())
                               .toArray(new int[0][0]);

        log.info("The length of the path with the lowest risk level in the real data is: {}.", part1(map));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("{}", part2(testMap));

        log.setLevel(Level.INFO);

        log.info("{}", part2(map));
    }

    /**
     * Determine the lowest total risk level of a path from the top left to the
     * bottom right of the map.
     * 
     * @param map The map of the risk levels.
     * @return The lowest total risk level.
     */
    private static int part1(final int[][] map) {

        // The maximum score is the shortest path times maximum risk
        int maxScore = (map.length + map[0].length - 2) * 9;

        return traverseFrom(0, 0, map, maxScore) - map[0][0];
    }

    private static int part2(final int[][] lines) {

        return -1;
    }

    /**
     * Start from the given coordinates and try paths from the given coordinates
     * until the max score is reached, or the bottom right corner.
     * 
     * @param x Starting horizontal coordinate
     * @param y Starting vertical coordinate
     * @param map The map of risk levels
     * @param maxScore The maximum score a path can be
     * @return The minimum score from the starting point to the end, or the maximum
     *     score parameter if reached.
     */
    private static int traverseFrom(int x, int y, int[][] map, int maxScore) {

        int score = map[y][x];

        // If we're already in the bottom right corner, that's it.
        if (y == map.length - 1 && x == map[0].length - 1) {
            log.debug("Bottom right corner score: {}", score);
            return score;
        }

        // Try going right
        int rightScore = maxScore;
        if (x + 1 < map[0].length)
            rightScore = traverseFrom(x + 1, y, map, maxScore - score);

        // Try going down
        int downScore = maxScore;
        if (y + 1 < map.length)
            downScore = traverseFrom(x, y + 1, map, maxScore - score);

        score += Math.min(rightScore, downScore);
        log.debug("From ({},{}) the minimum score is: {}", x, y, score);

        return score;
    }

    private static String printMap(int[][] map) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(Integer::toString)
                                        .collect(Collectors.joining()))
                     .collect(Collectors.joining("\n"));
    }
}