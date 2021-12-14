package aoc2021;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/14
 * 
 * @author Paul Cormier
 *
 */
public class Day14 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day14.class);

    private static final String INPUT_TXT = "Input-Day14.txt";
    private static final String POLYMER_TEMPLATE = "CPSSSFCFOFVFNVPKBFVN";

    private static final String TEST_INPUT_TXT = "TestInput-Day14.txt";
    private static final String TEST_POLYMER_TEMPLATE = "NNCB";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        Map<String, String> testRules = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                                 .map(l -> l.split(" -> "))
                                                 .collect(Collectors.toMap(r -> r[0], r -> r[1]));

        log.info("{}", part1(testRules, TEST_POLYMER_TEMPLATE));

        log.setLevel(Level.INFO);

        // Read the real file
        Map<String, String> rules = FileUtils.readFileToStream(INPUT_TXT)
                                             .map(l -> l.split(" -> "))
                                             .collect(Collectors.toMap(r -> r[0], r -> r[1]));

        log.info("{}", part1(rules, POLYMER_TEMPLATE));

        // PART 2

        log.setLevel(Level.DEBUG);

        //        log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        //        log.info("{}", part2(lines));
    }

    /**
     * Apply the insertion rules to the polymer template 10 times, and find the
     * difference between the most and least common elements.
     * 
     * @param insertionRules
     *            The map of insertion rules to apply.
     * @param polymerTemplate
     *            The starting point for the polymer.
     * @return The difference between the most and least common elements.
     */
    private static int part1(final Map<String, String> insertionRules, String polymerTemplate) {

        log.debug(polymerTemplate);

        int itterations = 10;

        for (int n = 1; n <= itterations; n++) {
            char[] chars = polymerTemplate.toCharArray();
            char[] newPolymer = new char[chars.length * 2 - 1];
            for (int i = 0; i < chars.length - 1; i++) {
                newPolymer[2 * i] = chars[i];
                newPolymer[2 * i + 1] = insertionRules.get(new String(new char[] { chars[i], chars[i + 1] })).charAt(0);
            }
            // Add the last character
            newPolymer[newPolymer.length - 1] = chars[chars.length - 1];
            polymerTemplate = new String(newPolymer);

            log.debug("After step {}: {}", n, polymerTemplate);
        }

        // Count the occurrences of the characters
        Map<Character, Integer> counts = new HashMap<>();
        polymerTemplate.chars().forEach(c -> counts.put((char) c, counts.getOrDefault((char) c, 0) + 1));

        int max = counts.entrySet().stream().mapToInt(Entry::getValue).max().getAsInt();
        int min = counts.entrySet().stream().mapToInt(Entry::getValue).min().getAsInt();

        return max - min;
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

}