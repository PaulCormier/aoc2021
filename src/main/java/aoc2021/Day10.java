package aoc2021;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/10
 * 
 * @author Paul Cormier
 *
 */
public class Day10 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day10.class);

    private static final String INPUT_TXT = "Input-Day10.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day10.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
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

    /**
     * Go through each line and find the score for the first illegal character.
     * Then return the sum of those scores.
     * 
     * @param lines
     *            The input lines.
     * @return The sum of the syntax error scores.
     */
    private static int part1(final List<String> lines) {
        Map<Character, Integer> scores = Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);
        Map<Character, Character> matchingChars = Map.of(')', '(', ']', '[', '}', '{', '>', '<');

        return lines.stream()
                    .mapToInt(line -> {
                        final Stack<Character> chars = new Stack<>();
                        for (char character : line.toCharArray()) {
                            switch (character) {
                                case '(':
                                case '[':
                                case '{':
                                case '<':
                                    chars.push(character);
                                    break;

                                case ')':
                                case ']':
                                case '}':
                                case '>':
                                    if (!chars.pop().equals(matchingChars.get(character)))
                                        return scores.get(character);
                            }
                        }
                        return 0;
                    }).sum();

    }

    private static int part2(final List<String> lines) {

        return -1;
    }

}