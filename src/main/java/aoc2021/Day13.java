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
 * https://adventofcode.com/2021/day/13
 * 
 * @author Paul Cormier
 *
 */
public class Day13 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day13.class);

    private static final String INPUT_TXT = "Input-Day13.txt";

    private static final String INPUT_INSTRUCTIONS = "fold along x=655\n" +
                                                     "fold along y=447\n" +
                                                     "fold along x=327\n" +
                                                     "fold along y=223\n" +
                                                     "fold along x=163\n" +
                                                     "fold along y=111\n" +
                                                     "fold along x=81\n" +
                                                     "fold along y=55\n" +
                                                     "fold along x=40\n" +
                                                     "fold along y=27\n" +
                                                     "fold along y=13\n" +
                                                     "fold along y=6";

    private static final String TEST_INPUT_TXT = "TestInput-Day13.txt";

    private static final String TEST_INPUT_INSTRUCTIONS = "fold along y=7\n" +
                                                          "fold along x=5";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        // Create an appropriate map
        int maxX = testLines.stream()
                            .mapToInt(l -> Integer.parseInt(l.split(",")[1]))
                            .max()
                            .getAsInt();
        int maxY = testLines.stream()
                            .mapToInt(l -> Integer.parseInt(l.split(",")[0]))
                            .max()
                            .getAsInt();
        int[][] testMap = new int[maxX + 1][maxY + 1];

        testLines.stream()
                 .map(l -> l.split(","))
                 .forEach(c -> testMap[Integer.parseInt(c[1])][Integer.parseInt(c[0])] = 1);

        List<String> testInstructions = Arrays.asList(TEST_INPUT_INSTRUCTIONS.split("\n"));
        log.trace(testInstructions.toString());

        log.info("The number of dots visible after one fold in the test data are: {}", part1(testMap, testInstructions));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        // Create an appropriate map
        maxX = lines.stream()
                    .mapToInt(l -> Integer.parseInt(l.split(",")[1]))
                    .max()
                    .getAsInt();
        maxY = lines.stream()
                    .mapToInt(l -> Integer.parseInt(l.split(",")[0]))
                    .max()
                    .getAsInt();
        int[][] map = new int[maxX + 1][maxY + 1];

        lines.stream()
             .map(l -> l.split(","))
             .forEach(c -> map[Integer.parseInt(c[1])][Integer.parseInt(c[0])] = 1);

        List<String> instructions = Arrays.asList(INPUT_INSTRUCTIONS.split("\n"));
        log.trace(testInstructions.toString());

        log.info("The number of dots visible after one fold are: {}", part1(map, instructions));

        // PART 2

        log.setLevel(Level.DEBUG);

        part2(testMap, testInstructions);

        log.setLevel(Level.INFO);

        part2(map, instructions);
    }

    /**
     * Determine how many dots are visible after following the first
     * instruction.
     * 
     * @param map
     *            The map of dots.
     * @param lines
     *            The lines of the instructions.
     * @return The number of dots on the map which are still visible after
     *         following the first instruction.
     */
    private static int part1(int[][] map, final List<String> lines) {

        log.debug("\n{}", printMap(map));

        String[] firstInstruction = lines.get(0).split("=");
        boolean horizontal = firstInstruction[0].endsWith("y");
        int line = Integer.parseInt(firstInstruction[1]);

        map = foldMap(map, line, horizontal);

        log.debug("After fold:\n{}", printMap(map));

        return (int) Stream.of(map).mapToLong(row -> Arrays.stream(row).filter(i -> i > 0).count()).sum();
    }

    private static void part2(int[][] map, final List<String> lines) {

        int count = 1;

        for (String instruction : lines) {

            String[] instructionParts = instruction.split("=");
            boolean horizontal = instructionParts[0].endsWith("y");
            int line = Integer.parseInt(instructionParts[1]);

            map = foldMap(map, line, horizontal);

            log.debug("Map after {} folds:\n{}", count++, printMap(map));
        }

        log.info("The final map:\n{}", printMap(map));
    }

    /**
     * Given a map, "fold" it along the specified line, horizontally or
     * vertically.
     * 
     * @param map
     *            The input map of points
     * @param line
     *            The index on the map to fold on
     * @param horizontal
     *            Whether this line is a row (horizontal) or column (vertical)
     * @return A new map containing the results of "folding" the old map.
     */
    private static int[][] foldMap(int[][] map, int line, boolean horizontal) {

        // Create the smaller map
        // then overlap the folded segment
        int[][] newMap;
        if (horizontal) {
            newMap = Arrays.copyOf(map, (map.length - 1) / 2);

            // Update each row moving away from the fold
            for (int i = 1; line - i >= 0; i++) {
                for (int j = 0; j < newMap[line - i].length; j++) {
                    newMap[line - i][j] += map[line + i][j];
                }
            }
        } else {
            newMap = Stream.of(map)
                           .map(row -> Arrays.copyOf(row, (map[0].length - 1) / 2))
                           .collect(Collectors.toList())
                           .toArray(new int[map.length][(map[0].length - 1) / 2]);

            // Update each column moving away from the fold
            for (int i = 1; line - i >= 0; i++) {
                for (int j = 0; j < newMap.length; j++) {
                    newMap[j][line - i] += map[j][line + i];
                }
            }
        }

        return newMap;
    }

    private static String printMap(int[][] map) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(i -> i > 0 ? "#" : ".")
                                        .collect(Collectors.joining()))
                     .collect(Collectors.joining("\n"));
    }
}