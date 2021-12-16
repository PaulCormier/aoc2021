package aoc2021;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
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

        log.info("The lowest total risk of any path in the test data is: {}.", part1(testMap));

        log.setLevel(Level.INFO);

        // Read the real file
        int[][] map = FileUtils.readFileToStream(INPUT_TXT)
                               .map(l -> l.chars().map(i -> i - '0').toArray())
                               .collect(Collectors.toList())
                               .toArray(new int[0][0]);

        log.info("The lowest total risk of any path in the real data is: {}.", part1(map));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("The lowest total risk of any path in the bigger test data is: {}.", part2(testMap));

        log.setLevel(Level.DEBUG);
        // log.setLevel(Level.INFO);

        log.info("The lowest total risk of any path in the bigger real data is: {}.", part2(map));
    }

    /**
     * Determine the lowest total risk level of a path from the top left to the
     * bottom right of the map.
     * 
     * @param map
     *     The map of the risk levels.
     * @return The lowest total risk level.
     */
    private static int part1(final int[][] map) {

        // The maximum score is the shortest path times maximum risk
        // int maxScore = (map.length + map[0].length - 2) * 9;

        return traverseFrom(0, 0, map, new int[map.length][map[0].length]) - map[0][0];
    }

    /**
     * Determine the lowest total risk level of a path from the top left to the
     * bottom right of the map when tiled 5 times in each direction.
     * 
     * @param map
     *     The map of the risk levels.
     * @return The lowest total risk level.
     */
    private static int part2(final int[][] map) {

        // log.debug("Old map:\n{}", printMap(map));

        // Grow the map
        int[][] bigMap = Stream.of(Arrays.copyOf(map, map.length * 5))
                               .map(r -> Arrays.copyOf(ObjectUtils.defaultIfNull(r, new int[map[0].length]),
                                                       map[0].length * 5))
                               .collect(Collectors.toList())
                               .toArray(new int[map.length * 5][map[0].length * 5]);

        // Copy and increment the original map
        int[] newValues = IntStream.rangeClosed(0, 34)
                                   .map(n -> (n - 1) % 9 + 1)
                                   .toArray();
        // log.debug("Number map: {}", newValues);

        for (int gridRow = 0; gridRow < 5; gridRow++) {
            for (int gridCol = 0; gridCol < 5; gridCol++) {
                for (int row = 0; row < map.length; row++) {
                    for (int col = 0; col < map[row].length; col++) {
                        bigMap[gridRow * map.length + row][gridCol * map[row].length
                                                           + col] = newValues[map[row][col] + gridRow + gridCol];
                    }
                }
            }
        }

        // log.debug("New map:\n{}", printMap(bigMap));

        // log.setLevel(Level.INFO);
        int[][] minScores = new int[bigMap.length][bigMap[0].length];
        int minScore = traverseFrom(0, 0, bigMap, 0, minScores, new Stack<int[]>())
                       - bigMap[0][0];
        log.debug(printMap(minScores, ","));

        return minScore;

    }

    /**
     * Start from the given coordinates and try paths from the given coordinates
     * until the max score is reached, or the bottom right corner.
     * 
     * @param x
     *     Starting horizontal coordinate
     * @param y
     *     Starting vertical coordinate
     * @param map
     *     The map of risk levels
     * @param maxScore
     *     The maximum score a path can be
     * @return The minimum score from the starting point to the end, or the
     *     maximum score parameter if reached.
     */
    private static int traverseFrom(int x, int y, int[][] map, int[][] maxScore) {

        int score = map[y][x];

        // If we're already in the bottom right corner, that's it.
        if (y == map.length - 1 && x == map[0].length - 1) {
            log.debug("Bottom right corner score: {}", score);
            return score;
        }

        // Try going right
        int rightScore = Integer.MAX_VALUE;
        if (x + 1 < map[y].length)
            rightScore = maxScore[y][x + 1] == 0 ? traverseFrom(x + 1, y, map, maxScore) : maxScore[y][x + 1];

        // Try going down
        int downScore = Integer.MAX_VALUE;
        if (y + 1 < map.length)
            downScore = maxScore[y + 1][x] == 0 ? traverseFrom(x, y + 1, map, maxScore) : maxScore[y + 1][x];

        score += Math.min(rightScore, downScore);
        log.debug("From ({},{}) the minimum score is: {}", x, y, score);

        maxScore[y][x] = score;

        return score;
    }

    /**
     * RESULTS IN A STACK OVERFLOW!
     * 
     * Start from the given coordinates and try paths from the given coordinates
     * until the max score is reached, or the bottom right corner.
     * 
     * @param x
     *     Starting horizontal coordinate
     * @param y
     *     Starting vertical coordinate
     * @param map
     *     The map of risk levels
     * @param minScores
     *     The maximum score a path can be
     * @param visited
     *     A stack of previously visited locations.
     * @return The minimum score from the starting point to the end, or the
     *     maximum score parameter if reached.
     */
    private static int traverseFrom(int x, int y, int[][] map, int[][] maxScore, Stack<int[]> visited) {

        int[] currentLocation = new int[] { x, y };
        // if (visited.contains(currentLocation))
        // return Integer.MAX_VALUE;

        int score = map[y][x];
        visited.push(currentLocation);

        // If we're already in the bottom right corner, that's it.
        if (y == map.length - 1 && x == map[0].length - 1) {
            // log.debug("Bottom right corner score: {}", score);
            return score;
        }

        // Try going right
        int rightScore = Integer.MAX_VALUE;
        if (x + 1 < map[y].length && !visited.contains(new int[] { x + 1, y }))
            rightScore = maxScore[y][x + 1] == 0 ? traverseFrom(x + 1, y, map, maxScore, visited) : maxScore[y][x + 1];

        // Try going down
        int downScore = Integer.MAX_VALUE;
        if (y + 1 < map.length && !visited.contains(new int[] { x, y + 1 }))
            downScore = maxScore[y + 1][x] == 0 ? traverseFrom(x, y + 1, map, maxScore, visited) : maxScore[y + 1][x];

        // What about up, or left?
        int upScore = Integer.MAX_VALUE;
        if (y - 1 > 0 && !visited.contains(new int[] { x, y - 1 }))
            upScore = maxScore[y - 1][x] == 0 ? traverseFrom(x, y - 1, map, maxScore, visited) : maxScore[y - 1][x];

        int leftScore = Integer.MAX_VALUE;
        if (x - 1 > 0 && !visited.contains(new int[] { x - 1, y }))
            leftScore = maxScore[y][x - 1] == 0 ? traverseFrom(x - 1, y, map, maxScore, visited) : maxScore[y][x - 1];

        score += Math.min(Math.min(rightScore, downScore), Math.min(upScore, leftScore));
        // log.debug("From ({},{}) the minimum score is: {}", x, y, score);

        maxScore[y][x] = score;
        visited.pop();

        return score;
    }

    private static int traverseFrom(int x, int y, int[][] map, int scoreSoFar, int[][] minScores,
                                    Stack<int[]> visited) {

        // int maxScore = (map.length + map[0].length - 2) * 9;
        // if (scoreSoFar > (map.length + map[0].length - 2) * 9) {
        // if (scoreSoFar > 3000) {
        // log.debug("Too far! Score: {}", scoreSoFar);
        // return 1_000_000;
        // }

        int[] currentLocation = new int[] { x, y };
        // if (visited.contains(currentLocation))
        // return Integer.MAX_VALUE;

        int score = map[y][x];
        visited.push(currentLocation);

        // If we're already in the bottom right corner, that's it.
        if (y == map.length - 1 && x == map[0].length - 1) {
            log.debug("Bottom right corner score: {}", score + scoreSoFar);
            log.debug("Path: {}", visited.stream().map(Arrays::toString).collect(Collectors.joining("->")));
            return score;
        }

        // Try going right
        int rightScore = 1_000_000;
        if (x + 1 < map[y].length && !visited.contains(new int[] { x + 1, y }))
            rightScore = minScores[y][x + 1] == 0 ? traverseFrom(x + 1, y, map, scoreSoFar + score, minScores, visited)
                    : minScores[y][x + 1];

        // Try going down
        int downScore = 1_000_000;
        if (y + 1 < map.length && !visited.contains(new int[] { x, y + 1 }))
            downScore = minScores[y + 1][x] == 0 ? traverseFrom(x, y + 1, map, scoreSoFar + score, minScores, visited)
                    : minScores[y + 1][x];

        // What about up, or left?
        int upScore = 1_000_000;
        if (y - 1 > 0 && !visited.contains(new int[] { x, y - 1 }))
            upScore = minScores[y - 1][x] == 0 ? traverseFrom(x, y - 1, map, scoreSoFar +
                                                                             score,
                                                              minScores, visited)
                    : minScores[y - 1][x];

        int leftScore = 1_000_000;
        // if (x - 1 > 0 && !visited.contains(new int[] { x - 1, y }))
        // leftScore = minScores[y][x - 1] == 0 ? traverseFrom(x - 1, y, map, scoreSoFar
        // + score, minScores, visited)
        // : minScores[y][x - 1];

        score += Math.min(Math.min(rightScore, downScore), Math.min(upScore, leftScore));
        // log.debug("From ({},{}) the minimum score is: {}", x, y, score);

        minScores[y][x] = score;
        visited.pop();

        return score;
    }

    /** Also generates a stack overflow. */
    private static int traverseFrom(int x, int y, int[][] map, int[][] minScores, Stack<int[]> visited, int depth) {

        // int maxScore = (map.length + map[0].length - 2) * 9;
        // if (scoreSoFar > (map.length + map[0].length - 2) * 9) {
        // if (scoreSoFar > 3000) {
        // log.debug("Too far! Score: {}", scoreSoFar);
        // return 1_000_000;
        // }

        int[] currentLocation = new int[] { x, y };

        int score = map[y][x];
        visited.push(currentLocation);

        // If we're already in the bottom right corner, that's it.
        if (y == map.length - 1 && x == map[0].length - 1) {
            log.debug("Path: {}", visited.stream().map(Arrays::toString).collect(Collectors.joining("->")));
            return score;
        }
        if(depth==0) {
            return score;
        }

        // Try going right
        int rightScore = 1_000_000;
        if (x + 1 < map[y].length && !visited.contains(new int[] { x + 1, y }))
            rightScore =  traverseFrom(x + 1, y, map, minScores, visited, depth - 1);

        // Try going down
        int downScore = 1_000_000;
        if (y + 1 < map.length && !visited.contains(new int[] { x, y + 1 }))
            downScore =  traverseFrom(x, y + 1, map, minScores, visited, depth - 1);

        // What about up, or left?
        int upScore = 1_000_000;
        if (y - 1 > 0 && !visited.contains(new int[] { x, y - 1 }))
            upScore =  traverseFrom(x, y - 1, map, minScores, visited, depth - 1);

        int leftScore = 1_000_000;
        if (x - 1 > 0 && !visited.contains(new int[] { x - 1, y }))
            leftScore = traverseFrom(x - 1, y, map, minScores, visited, depth - 1);

        score += Math.min(Math.min(rightScore, downScore), Math.min(upScore, leftScore));
        // log.debug("From ({},{}) the minimum score is: {}", x, y, score);

        minScores[y][x] = score;
        visited.pop();

        return score;
    }

    private static String printMap(int[][] map) {
        return printMap(map, "");
    }

    private static String printMap(int[][] map, String separator) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(Integer::toString)
                                        .collect(Collectors.joining(separator)))
                     .collect(Collectors.joining("\n"));
    }
}