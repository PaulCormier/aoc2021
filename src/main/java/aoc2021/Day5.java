package aoc2021;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/5
 * 
 * @author Paul Cormier
 *
 */
public class Day5 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day5.class);

    private static final String INPUT_TXT = "Input-Day5.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day5.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<Line> testLines = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                        .map(Line::new)
                                        .collect(Collectors.toList());
        log.trace(testLines.toString());

        int testCount = part1(testLines, 10);
        log.info("There are {} overlapping lines in the test data.", testCount);

        log.setLevel(Level.INFO);

        // Read the test file
        List<Line> lines = FileUtils.readFileToStream(INPUT_TXT)
                                    .map(Line::new)
                                    .collect(Collectors.toList());

        int realCount = part1(lines, 1000);
        log.info("There are {} overlapping lines in the real data.", realCount);

        // PART 2

        log.setLevel(Level.DEBUG);
        testCount = part2(testLines, 10);
        log.info("There are {} overlapping lines in the test data.", testCount);

        log.setLevel(Level.INFO);
        realCount = part2(lines, 1000);
        log.info("There are {} overlapping lines in the real data.", realCount);
    }

    /**
     * Read the lines, and plot the horizontal and vertical lines. Then determine
     * how many points overlap at least 2 times.
     * 
     * @param lines The coordinate pairs describing the lines.
     * @return The number of points over which more than two lines overlap.
     */
    private static int part1(final List<Line> lines, int gridSize) {
        int[][] grid = new int[gridSize][gridSize];

        // Only consider horizontal and vertical lines
        List<Line> importantLines = lines.stream()
                                         .filter(l -> l.x1 == l.x2 || l.y1 == l.y2)
                                         .collect(Collectors.toList());

        // Plot the lines
        for (Line line : importantLines) {
            log.debug(line.toString());

            for (int x = line.x1; x <= line.x2; x++) {
                for (int y = line.y1; y <= line.y2; y++) {
                    grid[y][x]++;
                }
            }

            if (log.isDebugEnabled())
                log.debug("\n" + printGrid(grid));
        }

        // Count overlaps
        int overlaps = countOverlaps(grid);

        return overlaps;
    }

    /**
     * Read the lines, and plot all the lines. Then determine
     * how many points overlap at least 2 times.
     * 
     * @param lines The coordinate pairs describing the lines.
     * @return The number of points over which more than two lines overlap.
     */
    private static int part2(final List<Line> lines, int gridSize) {
        int[][] grid = new int[gridSize][gridSize];

        // Only consider orthogonal lines
        List<Line> orthogonalLines = lines.stream()
                                          .filter(l -> l.x1 == l.x2 || l.y1 == l.y2)
                                          .collect(Collectors.toList());

        // Plot the orthogonal lines
        for (Line line : orthogonalLines) {
            // System.err.println(line);

            for (int x = line.x1; x <= line.x2; x++) {
                for (int y = line.y1; y <= line.y2; y++) {
                    grid[y][x]++;
                }
            }
            if (log.isDebugEnabled())
                log.debug("\n" + printGrid(grid));
        }

        // Only consider diagonal lines
        List<Line> diagonalLines = lines.stream()
                                        .filter(l -> l.x1 != l.x2 && l.y1 != l.y2)
                                        .collect(Collectors.toList());

        // Plot the diagonal lines
        for (Line line : diagonalLines) {
            log.trace(line.toString());

            for (int x = line.x1, y = line.y1; //
                    x <= line.x2 && ((line.y1 < line.y2 && y <= line.y2)
                                     || (line.y1 > line.y2 && y >= line.y2)); //
                    x++, y += (line.y1 < line.y2) ? 1 : -1) {
                grid[y][x]++;
            }
            if (log.isDebugEnabled())
                log.debug("\n" + printGrid(grid));
        }

        // Count overlaps
        int overlaps = countOverlaps(grid);

        return overlaps;
    }

    private static int countOverlaps(int[][] grid) {
        int overlaps = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] > 1)
                    overlaps++;
            }
        }
        return overlaps;
    }

    private static String printGrid(int[][] grid) {
        StringBuilder gridString = new StringBuilder();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                gridString.append(String.format("%s", grid[y][x] > 0 ? grid[y][x] : "."));
            }
            gridString.append("\n");
        }

        return gridString.toString();
    }

    /**
     * A line from (x1,y1) to (x2,y2).
     */
    private static class Line {
        int x1;
        int y1;
        int x2;
        int y2;

        Line(String lineInput) {
            String[] pairs = lineInput.split(" -> ");
            x1 = Integer.parseInt(pairs[0].split(",")[0]);
            y1 = Integer.parseInt(pairs[0].split(",")[1]);
            x2 = Integer.parseInt(pairs[1].split(",")[0]);
            y2 = Integer.parseInt(pairs[1].split(",")[1]);

            // Normalize them so that (x1,y1) is the smaller pair
            if (x1 > x2 || (x1 == x2 && y1 > y2)) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
        }

        @Override
        public String toString() {
            return String.format("(%d,%d) -> (%d,%d)", x1, y1, x2, y2);
        }
    }

}