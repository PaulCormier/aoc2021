package aoc2021;

import java.util.List;
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
public class Day13{

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day13.class);

    private static final String INPUT_TXT = "Input-Day13.txt";

    private static final String INPUT_INSTRUCTIONS = "fold along x=655\n"+
"fold along y=447\n"+
"fold along x=327\n"+
"fold along y=223\n"+
"fold along x=163\n"+
"fold along y=111\n"+
"fold along x=81\n"+
"fold along y=55\n"+
"fold along x=40\n"+
"fold along y=27\n"+
"fold along y=13\n"+
"fold along y=6";

    private static final String TEST_INPUT_TXT = "TestInput-Day13.txt";

    private static final String TEST_INPUT_INSTRUCTIONS = "fold along y=7\n"+
"fold along x=5";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        int[][] testMap = new int[20][20];
        FileUtils.readFileToStream(TEST_INPUT_TXT)
        .map(l->l.split(","))
        .forEach(c->c);
        List<String> testLines = TEST_INPUT_INSTRUCTIONS.split("\n");
        log.trace(testLines.toString());

        log.info("{}", part1(testLines));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("{}", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        log.info("{}", part2(lines));
    }

    private static int part1(final List<String> lines) {

        return -1;
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

    private static String printMap(int[][] map) {
        return Stream.of(map)
                     .map(l -> IntStream.of(l)
                                        .mapToObj(i->i>0? "#" : ".")
                                        .collect(Collectors.joining()))
                     .collect(Collectors.joining("\n"));
    }
}