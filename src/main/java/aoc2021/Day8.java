package aoc2021;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/8
 * 
 * @author Paul Cormier
 *
 */
public class Day8 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day8.class);

    private static final String INPUT_TXT = "Input-Day8.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day8.txt";

    public static void main(String[] args) {

        // log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        log.trace(testLines.toString());

        log.info("The digits 1, 4, 7, or 8 appear {} times in the test input.", part1(testLines));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("The digits 1, 4, 7, or 8 appear {} times in the real input.", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("The sum of the first test line is: {}.",
                 part2(Arrays.asList("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")));

        log.info("The sum of the test output values is: {}.", part2(testLines));

        log.setLevel(Level.INFO);

        log.info("The sum of the real output values is: {}.", part2(lines));
    }

    /**
     * In the output values, how many times do digits 1, 4, 7, or 8 appear?
     * 
     * @param lines The lines containing the code values and the output values,
     *     separated by a '|' character.
     */
    private static long part1(final List<String> lines) {
        List<Integer> lengths = Arrays.asList(2, 3, 4, 7);

        log.debug(lines.toString());

        return lines.stream()
                    .map(l -> l.split(" \\| ")[1])
                    .peek(log::debug)
                    .flatMap(o -> Stream.of(o.split(" ")))
                    .filter(d -> lengths.contains(d.length()))
                    .peek(log::debug)
                    .count();
    }

    /**
     * What do you get if you add up all of the output values?
     * 
     * @param lines The lines containing the code values and the output
     *     values,
     *     separated by a '|' character.
     */
    private static long part2(final List<String> lines) {

        long sum = 0;

        List<String[]> values = lines.stream()
                                     .map(l -> l.replace(" | ", " ").split(" "))
                                     // .peek(l -> log.debug(Arrays.toString(l)))
                                     .collect(Collectors.toList());

        for (String[] line : values) {
            log.debug(Arrays.toString(line));

            // Map of input signals to values
            Map<String, Integer> signalMap = new HashMap<>();
            // Map of values to digit signals
            Map<Integer, String> digitMap = new HashMap<>();

            int failSafe = 200;

            for (int i = 0; signalMap.size() != 10 && i < line.length; i++, i %= line.length) {
                String digit = sort(line[i]);

                if (signalMap.containsKey(digit))
                    continue;

                if (digit.length() == 6) { // 6, 9, 0
                    // 0 and 6 are missing a signal from 4
                    if (digitMap.containsKey(4)) {

                        List<Character> digitChars = digit.chars().mapToObj(c -> (char) c)
                                                          .collect(Collectors.toList());
                        digitChars.retainAll(digitMap.get(4).chars().mapToObj(c -> (char) c)
                                                     .collect(Collectors.toList()));
                        if (digitChars.size() == 4) {
                            signalMap.put(digit, 9);
                            digitMap.put(9, digit);
                        } else if (digitMap.containsKey(1)) {
                            // 0 or 6
                            digitChars.retainAll(digitMap.get(1).chars().mapToObj(c -> (char) c)
                                                         .collect(Collectors.toList()));
                            if (digitChars.size() == 2) {
                                signalMap.put(digit, 0);
                                digitMap.put(0, digit);
                            } else {
                                signalMap.put(digit, 6);
                                digitMap.put(6, digit);
                            }
                        }
                    }

                } else if (digit.length() == 5) { // 2, 3, 5
                    // 5 and 3 have all the same signals as 9
                    if (digitMap.containsKey(9)) {

                        List<Character> digitChars = digit.chars().mapToObj(c -> (char) c)
                                                          .collect(Collectors.toList());
                        digitChars.retainAll(digitMap.get(9).chars().mapToObj(c -> (char) c)
                                                     .collect(Collectors.toList()));
                        if (digitChars.size() == 4) {
                            signalMap.put(digit, 2);
                            digitMap.put(2, digit);
                        } else if (digitMap.containsKey(1)) {
                            // 3 has all the signals from 1
                            digitChars.retainAll(digitMap.get(1).chars().mapToObj(c -> (char) c)
                                                         .collect(Collectors.toList()));
                            if (digitChars.size() == 2) {
                                signalMap.put(digit, 3);
                                digitMap.put(3, digit);
                            } else {
                                signalMap.put(digit, 5);
                                digitMap.put(5, digit);
                            }
                        }
                    }
                } else { // 1, 4, 7, 8
                    switch (line[i].length()) {
                        case 2:
                            signalMap.put(digit, 1);
                            digitMap.put(1, digit);
                            break;
                        case 3:
                            signalMap.put(digit, 7);
                            digitMap.put(7, digit);
                            break;
                        case 4:
                            signalMap.put(digit, 4);
                            digitMap.put(4, digit);
                            break;
                        case 7:
                            signalMap.put(digit, 8);
                            digitMap.put(8, digit);
                            break;
                    }
                }

                if (failSafe-- <= 0)
                    break;
            }
            log.debug(digitMap.toString());

            // Decode the output
            int output = signalMap.get(sort(line[10])) * 1000 +
                         signalMap.get(sort(line[11])) * 100 +
                         signalMap.get(sort(line[12])) * 10 +
                         signalMap.get(sort(line[13]));
            log.debug("{} {} {} {}: {}", line[10], line[11], line[12], line[13], output);

            sum += output;
        }

        return sum;
    }

    /**
     * Sort the characters in a string in lexicographical order.
     * 
     * @param characters The string to be sorted.
     * @return The sorted string.
     */
    private static String sort(String characters) {
        char[] charArray = characters.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }
}