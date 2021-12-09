package aoc2021;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/9
 * 
 * @author Paul Cormier
 *
 */
public class Day9 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day9.class);

    private static final String INPUT_TXT = "Input-Day9.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day9.txt";

    public static void main(String[] args) {

        log.setLevel(Level.TRACE);

        // Read the test file
        int[][] testHeightMap = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                         .map(l -> l.chars().map(i -> i - '0').toArray())
                                         .collect(Collectors.toList())
                                         .toArray(new int[0][0]);

        log.trace("\n{}", printMap(testHeightMap));

        log.info("The sum of the risk levels for the test data is: {}", part1(testHeightMap));

        log.setLevel(Level.INFO);

        // Read the real file
        int[][] heightMap = FileUtils.readFileToStream(INPUT_TXT)
                                     .map(l -> l.chars().map(i -> i - '0').toArray())
                                     .collect(Collectors.toList())
                                     .toArray(new int[0][0]);

        log.info("The sum of the risk levels for the real data is: {}", part1(heightMap));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.debug("Basin map:\n{}", printMap(mapBasins(testHeightMap)));

        log.info("The product of the size of the three biggest basins for the test data is: {}", part2(testHeightMap));

        //        log.setLevel(Level.INFO);
        log.debug("Basin map:\n{}", printMap(mapBasins(heightMap)));

        log.info("The product of the size of the three biggest basins for the real data is: {}", part2(heightMap));
    }

    /**
     * What is the sum of the risk levels of all low points on your heightmap?
     * 
     * @param heightMap
     *            A 2d array of the heights at certain points.
     * @return The sum of the local minimums (plus 1) of the height map.
     */
    private static int part1(final int[][] heightMap) {

        List<Integer> minimums = new ArrayList<>();

        for (int i = 0; i < heightMap.length; i++) {
            for (int j = 0; j < heightMap[i].length; j++) {
                int height = heightMap[i][j];
                // Up
                if (i > 0 && height >= heightMap[i - 1][j])
                    continue;

                // Down
                if (i < heightMap.length - 1 && height >= heightMap[i + 1][j])
                    continue;

                // Left
                if (j > 0 && height >= heightMap[i][j - 1])
                    continue;

                // Right
                if (j < heightMap[i].length - 1 && height >= heightMap[i][j + 1])
                    continue;

                minimums.add(height);

                log.debug("[{}][{}]: {}", i, j, height);
            }
        }
        log.debug("Minimums: {}", minimums);

        return minimums.stream().collect(Collectors.summingInt(Integer::intValue)) + minimums.size();
    }

    /**
     * What do you get if you multiply together the sizes of the three largest
     * basins?
     * 
     * @param heightMap
     *            A 2d array of the heights at certain points.
     * @return The product of the size of the three largest basins.
     */
    private static int part2(final int[][] heightMap) {

        List<Integer> basins = new ArrayList<>();

        int[][] basinMap = mapBasins(heightMap);

        for (int i = 0; i < heightMap.length; i++) {
            for (int j = 0; j < heightMap[i].length; j++) {
                int height = heightMap[i][j];

                // Ignore 9s
                if (height == 9)
                    continue;

                // Up
                if (i > 0 && height >= heightMap[i - 1][j])
                    continue;

                // Down
                if (i < heightMap.length - 1 && height >= heightMap[i + 1][j])
                    continue;

                // Left
                if (j > 0 && height >= heightMap[i][j - 1])
                    continue;

                // Right
                if (j < heightMap[i].length - 1 && height >= heightMap[i][j + 1])
                    continue;

                // Find the size of the basin
                int basinSize = findBasinSize(i, j, basinMap);
                basins.add(basinSize);

                log.debug("[{}][{}]: {} / {}", i, j, height, basinSize);
            }
        }
        log.debug("Basins: {}", basins);

        return basins.stream()
                     .sorted(Comparator.reverseOrder())
                     .limit(3)
                     .peek(i -> log.debug(i.toString()))
                     .mapToInt(Integer::intValue)
                     .reduce(1, Math::multiplyExact);
    }

    /**
     * Find the size of the basin starting at the given location.
     * 
     * @param row
     *            The index of the row where the local minimum is.
     * @param column
     *            The index of the column where the local minimum is.
     * @param basinMap
     *            The basin map to scan.
     * @return The count of the size of the basin in which the local minimum is
     *         found.
     */
    private static int findBasinSize(int row, int column, int[][] basinMap) {

        int basinNumber = basinMap[row][column];

        return Stream.of(basinMap)
                     .map(l -> IntStream.of(l)
                                        .filter(i -> i == basinNumber)
                                        .count())
                     .collect(Collectors.summingInt(Long::intValue));

    }

    /**
     * Map out the basins in the height map.
     * 
     * @param heights
     *            The height map to scan for basins.
     * @return A map of the basins
     */
    private static int[][] mapBasins(int[][] heights) {
        int[][] basins = new int[heights.length][heights[0].length];

        int basinNumber = 0;
        for (int i = 0; i < heights.length; i++) {
            for (int j = 0; j < heights[i].length; j++) {
                if (heights[i][j] == 9) {
                    basins[i][j] = -1;
                    continue;
                }

                // Check if this is next to an existing basin 

                // Left
                if (j > 0 && basins[i][j - 1] != -1) {
                    basins[i][j] = basins[i][j - 1];
                    continue;
                }

                // Up
                if (i > 0 && basins[i - 1][j] != -1) {
                    basins[i][j] = basins[i - 1][j];
                    continue;
                }

                // It's a new basin
                basins[i][j] = basinNumber++;

            }
        }

        // Join the contiguous basins
        for (int i = 0; i < basins.length - 1; i++) {
            for (int j = 0; j < basins[i].length; j++) {
                int currentBasin = basins[i][j];
                if (currentBasin == -1)
                    continue;

                int contiguousBasin = basins[i + 1][j];
                if (contiguousBasin != -1 && contiguousBasin != currentBasin) {
                    // Replace all
                    for (int i2 = 0; i2 < basins.length; i2++) {
                        for (int j2 = 0; j2 < basins[i2].length; j2++) {
                            if (basins[i2][j2] == contiguousBasin)
                                basins[i2][j2] = currentBasin;
                        }
                    }
                }
            }
        }

        return basins;
    }

    private static String printMap(int[][] map) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(Integer::toString)
                                        .map(s -> "-1".equals(s) ? "x" : s)
                                        .collect(Collectors.joining(",")))
                     .collect(Collectors.joining("\n"));
    }

}