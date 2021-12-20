package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/20
 * 
 * @author Paul Cormier
 *
 */
public class Day20 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day20.class);

    private static final String INPUT_LINE = "###.#....#.....##....#..#####.###..#...#.####....#..##.#.#...#..###.#####."
                                             + ".####.#.#######.#.##..#.#..##.###...###....####.####..#.#.#...##..##.#..##"
                                             + "###..###...###.....#..#.#..##....##..#...#.#........####...#.#...##....##.."
                                             + "###.#.#..##.#.####..##........##.##.#.#.#.#.#.#...#...###.####.#.######..#."
                                             + "#.##....#.##.....##.#.#.#.##...#...#.#..#..##.#######.###............####.."
                                             + ".###.#..#.###.#...#.......#.##.##...##..####.##....#####....#..#...#.#.##.#"
                                             + "......#####....#####..#..#.##...#.#....##.##..###....##.#....##.";
    private static final String INPUT_TXT = "Input-Day20.txt";

    private static final String TEST_INPUT_LINE = "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##"
                                                  + "#..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###"
                                                  + ".######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#."
                                                  + ".#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#....."
                                                  + ".#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.."
                                                  + "...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#....."
                                                  + "..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#";
    private static final String TEST_INPUT_TXT = "TestInput-Day20.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        int[][] testImage = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                     .map(l -> l.chars().map(i -> i == '.' ? 0 : 1).toArray())
                                     .collect(Collectors.toList())
                                     .toArray(new int[0][0]);
        log.debug("\n{}", printMap(testImage));

        log.info("The number of lit pixels in the test data after two iterations is: {}",
                 part1(testImage, TEST_INPUT_LINE));

        // log.setLevel(Level.INFO);

        // Read the real file
        int[][] realImage = FileUtils.readFileToStream(INPUT_TXT)
                                     .map(l -> l.chars().map(i -> i == '.' ? 0 : 1).toArray())
                                     .collect(Collectors.toList())
                                     .toArray(new int[0][0]);

        log.info("The number of lit pixels in the real data after two iterations is: {}",
                 part1(realImage, INPUT_LINE));

        // PART 2

        log.setLevel(Level.DEBUG);

        // log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        // log.info("{}", part2(lines));
    }

    /**
     * Apply the image filter to the input image twice and count the number of lit
     * pixels.
     * 
     * @param image The map of pixels in the image.
     * @param imageFilter The filter to apply to the image.
     * @return The number of lit pixels after two iterations.
     */
    private static int part1(int[][] image, String imageFilter) {

        int iterations = 2;

        // Make a bigger canvas
        int buffer = 10;
        int canvasSize = image.length + buffer * iterations;
        int[][] filteredImage = new int[canvasSize][canvasSize];

        // Copy the image to it
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                filteredImage[y + buffer / 2 * iterations][x + buffer / 2 * iterations] = image[y][x];
            }
        }

        // Run the filter a couple times
        for (int i = 0; i < iterations; i++) {
            image = filteredImage;
            filteredImage = new int[canvasSize][canvasSize];
            log.debug("\n{}", printMap(image));

            for (int y = 1; y < canvasSize - 1; y++) {
                for (int x = 1; x < canvasSize - 1; x++) {
                    // Consider the nine pixels in the image
                    AtomicInteger powers = new AtomicInteger(8);
                    int filterOffset = IntStream.of(image[y - 1][x - 1], image[y - 1][x], image[y - 1][x + 1],
                                                    image[y][x - 1], image[y][x], image[y][x + 1],
                                                    image[y + 1][x - 1], image[y + 1][x], image[y + 1][x + 1])
                                                .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
                                                .sum();
                    // New pixel
                    filteredImage[y][x] = imageFilter.charAt(filterOffset) == '.' ? 0 : 1;

                }
            }

            // Compute borders (just repeat whatever's just inside)
            int y = 0;
            for (int x = 1; x < canvasSize - 1; x++) {
                // Consider the nine pixels in the image
                // AtomicInteger powers = new AtomicInteger(8);
                int filterOffset = image[y + 1][x] * 255;
                // IntStream.of(0, 0, 0,
                // image[y][x - 1], image[y][x], image[y][x + 1],
                // image[y + 1][x - 1], image[y + 1][x], image[y + 1][x + 1])
                // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
                // .sum();
                // New pixel
                filteredImage[y][x] = imageFilter.charAt(filterOffset) == '.' ? 0 : 1;
            }
            y = canvasSize - 1;
            for (int x = 1; x < canvasSize - 1; x++) {
                // Consider the nine pixels in the image
                // AtomicInteger powers = new AtomicInteger(8);
                // int filterOffset = IntStream.of(image[y - 1][x - 1], image[y - 1][x], image[y
                // - 1][x + 1],
                // image[y][x - 1], image[y][x], image[y][x + 1],
                // 0, 0, 0)
                // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
                // .sum();

                int filterOffset = image[y - 1][x] * 255;
                // New pixel
                filteredImage[y][x] = imageFilter.charAt(filterOffset) == '.' ? 0 : 1;
            }
            int x = 0;
            for (y = 1; y < canvasSize - 1; y++) {
                // Consider the nine pixels in the image
                // AtomicInteger powers = new AtomicInteger(8);
                // int filterOffset = IntStream.of(0, image[y - 1][x], image[y - 1][x + 1],
                // 0, image[y][x], image[y][x + 1],
                // 0, image[y + 1][x], image[y + 1][x + 1])
                // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
                // .sum();

                int filterOffset = image[y][x + 1] * 255;
                // New pixel
                filteredImage[y][x] = imageFilter.charAt(filterOffset) == '.' ? 0 : 1;
            }
            x = canvasSize - 1;
            for (y = 1; y < canvasSize - 1; y++) {
                // Consider the nine pixels in the image
                // AtomicInteger powers = new AtomicInteger(8);
                // int filterOffset = IntStream.of(image[y - 1][x - 1], image[y - 1][x], 0,
                // image[y][x - 1], image[y][x], 0,
                // image[y + 1][x - 1], image[y + 1][x], 0)
                // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
                // .sum();

                int filterOffset = image[y][x - 1] * 255;
                // New pixel
                filteredImage[y][x] = imageFilter.charAt(filterOffset) == '.' ? 0 : 1;
            }

            // Compute corners
            AtomicInteger powers = new AtomicInteger(8);
            x = 0;
            y = 0;
            // int filterOffset = IntStream.of(0, 0, 0,
            // 0, image[y][x], image[y][x + 1],
            // 0, image[y + 1][x], image[y + 1][x + 1])
            // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
            // .sum();
            // New pixel
            filteredImage[y][x] = imageFilter.charAt(image[1][1] * 255) == '.' ? 0 : 1;

            powers.set(8);
            x = canvasSize - 1;
            y = 0;
            // filterOffset = IntStream.of(0, 0, 0,
            // image[y][x - 1], image[y][x], 0,
            // image[y + 1][x - 1], image[y + 1][x], 0)
            // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
            // .sum();
            // New pixel
            filteredImage[y][x] = imageFilter.charAt(image[1][x - 1] * 255) == '.' ? 0 : 1;

            powers.set(8);
            x = 0;
            y = canvasSize - 1;
            // filterOffset = IntStream.of(0, image[y - 1][x], image[y - 1][x + 1],
            // 0, image[y][x], image[y][x + 1],
            // 0, 0, 0)
            // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
            // .sum();
            // New pixel
            filteredImage[y][x] = imageFilter.charAt(image[y - 1][1] * 255) == '.' ? 0 : 1;

            powers.set(8);
            x = canvasSize - 1;
            y = canvasSize - 1;
            // filterOffset = IntStream.of(image[y - 1][x - 1], image[y - 1][x], 0,
            // image[y][x - 1], image[y][x], 0,
            // 0, 0, 0)
            // .map(bit -> bit * (int) Math.pow(2, powers.getAndDecrement()))
            // .sum();
            // New pixel
            filteredImage[y][x] = imageFilter.charAt(image[y - 1][x - 1] * 255) == '.' ? 0 : 1;

        }

        log.debug("\n{}", printMap(filteredImage));

        return Arrays.stream(filteredImage).flatMapToInt(Arrays::stream).sum();
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

    private static String printMap(int[][] map) {
        return printMap(map, "");
    }

    private static String printMap(int[][] map, String separator) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(p -> p == 0 ? "." : "#")
                                        .collect(Collectors.joining(separator)))
                     .collect(Collectors.joining("\n"));
    }
}