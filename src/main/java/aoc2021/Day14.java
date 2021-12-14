package aoc2021;

import java.util.ArrayList;
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

        log.info("After 10 iterations, the difference between the maximum and minimum occurrences of the characters in the test data is: {}.",
                 part1(testRules, TEST_POLYMER_TEMPLATE));

        log.setLevel(Level.INFO);

        // Read the real file
        Map<String, String> rules = FileUtils.readFileToStream(INPUT_TXT)
                                             .map(l -> l.split(" -> "))
                                             .collect(Collectors.toMap(r -> r[0], r -> r[1]));

        log.info("After 10 iterations, the difference between the maximum and minimum occurrences of the characters in the real data is: {}.",
                 part1(rules, POLYMER_TEMPLATE));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("After 40 iterations, the difference between the maximum and minimum occurrences of the characters in the test data is: {}.",
                 part2(testRules, TEST_POLYMER_TEMPLATE));

        log.setLevel(Level.INFO);

        //        log.info("After 40 iterations, the difference between the maximum and minimum occurrences of the characters in the real data is: {}.", part2(lines));
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

        for (int n = 1; n <= 10; n++) {
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

    /**
     * Determine the number of times each character will appear in the final
     * string after 40 iterations, and return the difference between the maximum
     * and the minimum.
     * 
     * @param insertionRules
     *            The mapping of character pairs to an insertion character.
     * @param polymerTemplate
     *            The initial string of characters to apply the insertion rules
     *            to.
     * @return The difference between the maximum and the minimum occurrences of
     *         the individual characters.
     */
    private static long part2(final Map<String, String> insertionRules, String polymerTemplate) {

        // Try creating a graph of the pairs, and traversing it for 40 steps deep
        Map<String, Node> nodeMap = insertionRules.keySet().stream().map(Node::new).collect(Collectors.toMap(n -> n.value, n -> n));
        insertionRules.entrySet().forEach(e -> {
            String key = e.getKey();
            String common = e.getValue();
            Node root = nodeMap.computeIfAbsent(key, Node::new);
            root.common = common;
            root.left = nodeMap.computeIfAbsent(key.charAt(0) + common, Node::new);
            root.right = nodeMap.computeIfAbsent(common + key.charAt(1), Node::new);
        });

        log.debug("Node map:\n{}", nodeMap.values().stream().map(Node::toString).collect(Collectors.joining("\n")));

        // For each starting pair, traverse the graph and count the instances of each character.
        Map<Character, Long> counts = new HashMap<>();

        // Start with the last character already in the map.
        counts.put(polymerTemplate.charAt(polymerTemplate.length() - 1), 1L);

        char[] chars = polymerTemplate.toCharArray();
        List<Node> startingNodes = new ArrayList<>();
        for (int i = 0; i < chars.length - 1; i++) {
            startingNodes.add(nodeMap.get(new String(new char[] { chars[i], chars[i + 1] })));
        }

        log.debug("Starting nodes: {}", startingNodes.stream().map(n -> n.value).collect(Collectors.joining(", ")));

        startingNodes.stream()
                     .flatMap(n -> countChars(n, 39).entrySet().stream())
                     .forEach(e -> counts.merge(e.getKey(), e.getValue(), Math::addExact));

        log.debug("Counts:\n{}", counts);

        long max = counts.entrySet().stream().mapToLong(Entry::getValue).max().getAsLong();
        long min = counts.entrySet().stream().mapToLong(Entry::getValue).min().getAsLong();

        return max - min;
    }

    private static Map<Character, Long> countChars(Node fromNode, int depth) {

        Map<Character, Long> counts = new HashMap<>();

        if (depth > 0) {
            countChars(fromNode.left, depth - 1).entrySet().forEach(e -> counts.merge(e.getKey(), e.getValue(), Math::addExact));
            countChars(fromNode.right, depth - 1).entrySet().forEach(e -> counts.merge(e.getKey(), e.getValue(), Math::addExact));
        } else if (depth == 0) {

            char leftChar = fromNode.value.charAt(0);
            char commonChar = fromNode.common.charAt(0);
            if (leftChar == commonChar) {
                counts.put(leftChar, 2L);
            } else {
                counts.put(leftChar, 1L);
                counts.put(commonChar, 1L);
            }
        }

        return counts;
    }

    private static class Node {
        String value;
        Node left;
        Node right;
        String common;

        Node(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s | %s", this.value, left.value, right.value);
        }
    }
}