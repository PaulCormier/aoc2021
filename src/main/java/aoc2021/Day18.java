package aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/18
 * 
 * @author Paul Cormier
 *
 */
public class Day18 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day18.class);

    private static final String INPUT_TXT = "Input-Day18.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day18.txt";

    public static void main(String[] args) {

        // Read the test file (it has three sets of data)
        List<String> testLines = Arrays.asList(FileUtils.readFileToStream(TEST_INPUT_TXT)
                                                        .collect(Collectors.joining(" "))
                                                        .split("  "));
        log.trace(testLines.toString());

        log.debug("Test parsing");
        Arrays.stream(testLines.get(0).split(" ")).forEach(l -> log.debug("{} -> {}", l, new Pair(l)));

        // log.setLevel(Level.TRACE);
        log.trace("Test reduction");
        Arrays.stream(testLines.get(1).split(" "))
              .forEach(l -> {
                  Pair pair = new Pair(l);
                  log.trace("{}", pair);
                  while (pair.reduce(0, true) || pair.reduce(0, false))
                      log.trace("becomes {}", pair);
              });

        log.trace("Test magnitude");
        Arrays.stream(testLines.get(2).split(" "))
              .forEach(l -> log.trace("{} becomes {}.", l, new Pair(l).getMagnitude()));

        log.trace("Test sums");
        // log.info("The magnitude of the sum of the test data is: {}",
        // part1(Arrays.asList(testLines.get(3).split(" "))));
        part1(Arrays.asList(testLines.get(4).split(" ")));
        part1(Arrays.asList(testLines.get(5).split(" ")));

        log.setLevel(Level.DEBUG);
        log.info("The magnitude of the sum of the test data is: {}", part1(Arrays.asList(testLines.get(6).split(" "))));
        log.info("The magnitude of the sum of the test data is: {}", part1(Arrays.asList(testLines.get(7).split(" "))));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("The magnitude of the sum of the real data is: {}", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("the largest magnitude of any sum of two different snailfish numbers in the test data is: {}",
                 part2(Arrays.asList(testLines.get(7).split(" "))));

        log.setLevel(Level.INFO);

        log.info("the largest magnitude of any sum of two different snailfish numbers in the test data is: {}",
                 part2(lines));
    }

    /**
     * Sum the list of snailfish numbers and find the magnitude of the result.
     * 
     * @param lines The snailfish numbers to be summed.
     * @return The magnitude of the sum of the snailfish numbers.
     */
    private static int part1(final List<String> lines) {

        Pair currentPair = null;
        for (String line : lines) {
            log.debug("");
            Pair nextPair = new Pair(line);
            if (currentPair == null) {
                currentPair = nextPair;
                continue;
            }
            log.debug("  {}", currentPair);
            log.debug("+ {}", nextPair);

            Pair sum = new Pair(currentPair, nextPair);

            log.trace("{}", sum);
            while (sum.reduce(0, true) || sum.reduce(0, false))
                log.trace("becomes {}", sum);

            currentPair = sum;

            log.debug("= {}", currentPair);
        }

        log.debug("The final sum is: {}", currentPair);

        return currentPair.getMagnitude();
    }

    /**
     * Find the largest magnitude of any pair of snailfish numbers.
     * 
     * @param lines The snailfish numbers to be summed.
     * @return The magnitude of the largest sum of two snailfish numbers.
     */
    private static int part2(final List<String> lines) {

        List<Pair> sums = new ArrayList<>();

        for (String firstLine : lines) {
            for (String secondLine : lines) {
                if (firstLine.equals(secondLine))
                    continue;

                Pair firstPair = new Pair(firstLine);
                Pair secondPair = new Pair(secondLine);

                Pair sum = new Pair(firstPair, secondPair);

                log.trace("{}", sum);
                while (sum.reduce(0, true) || sum.reduce(0, false))
                    log.trace("becomes {}", sum);
                log.debug("= {}", sum);

                sums.add(sum);
            }
        }

        Pair maxSum = sums.stream().max(Comparator.comparing(Pair::getMagnitude)).get();

        log.debug("The final sum is: {}", maxSum);

        return maxSum.getMagnitude();
    }

    private static class Pair {
        int value;

        Pair left;
        Pair right;
        Pair parent;

        /**
         * Create a Pair element which only has a regular number.
         * 
         * @param value The regular number value.
         */
        Pair(Pair parent, int value) {
            this.parent = parent;
            this.value = value;
        }

        /**
         * Parse a snailfish number string to create a Pair, which contains other Pairs.
         * This Pair has no parent.
         * 
         * @param pairString The string representation of the snailfish Pair.
         */
        Pair(String pairString) {
            this(null, pairString);
        }

        /**
         * Parse a snailfish number string to create a Pair, which contains other Pairs.
         * 
         * @param parent The Pair which contains this new Pair.
         * @param pairString The string representation of the snailfish Pair.
         */
        Pair(Pair parent, String pairString) {
            this.parent = parent;
            int offset = 1;
            // Left side
            if (pairString.charAt(offset) == '[')
                this.left = new Pair(this, pairString.substring(offset));
            else
                this.left = new Pair(this, Character.getNumericValue(pairString.charAt(offset)));

            // Right side
            offset = this.left.toString().length() + 2;
            if (pairString.charAt(offset) == '[')
                this.right = new Pair(this, pairString.substring(offset));
            else
                this.right = new Pair(this, Character.getNumericValue(pairString.charAt(offset)));
        }

        /**
         * Construct a new pair from two other pairs.
         * 
         * @param left The left pair.
         * @param right The right pair.
         */
        Pair(Pair left, Pair right) {
            this.left = left;
            this.right = right;
            this.left.parent = this;
            this.right.parent = this;
        }

        boolean reduce(int level, boolean explode) {

            if (!explode && value >= 10) {
                split();
                return true;
            }

            if (!isRegularNumber()) {
                if (explode && level == 4) {
                    explode();
                    return true;
                }

                return left.reduce(level + 1, explode) || right.reduce(level + 1, explode);

            }

            return false;
        }

        /**
         * To explode a pair, the pair's left value is added to the first regular number
         * to the left of the exploding pair (if any), and the pair's right value is
         * added to the first regular number to the right of the exploding pair (if
         * any). Exploding pairs will always consist of two regular numbers. Then, the
         * entire exploding pair is replaced with the regular number 0.
         */
        void explode() {
            log.trace("Explode: {}", this);
            // Check the left side
            /*while (parentLeftRegularNumber != null) {
                if (parentLeftRegularNumber.left.isRegularNumber()) {
                    parentLeftRegularNumber.left.value += this.left.value;
                    break;
                }
                parentLeftRegularNumber = parentLeftRegularNumber.parent;
            }*/
            Pair parentLeftRegularNumber = parent;
            Pair previousPair = this;
            while (parentLeftRegularNumber != null
                   && !parentLeftRegularNumber.left.isRegularNumber()
                   && parentLeftRegularNumber.right != previousPair) {

                previousPair = parentLeftRegularNumber;
                parentLeftRegularNumber = parentLeftRegularNumber.parent;

            }
            // At the top
            if (parentLeftRegularNumber != null) {
                if (parentLeftRegularNumber.left.isRegularNumber()) {
                    parentLeftRegularNumber.left.value += this.left.value;
                } else {
                    parentLeftRegularNumber = parentLeftRegularNumber.left;
                    // Find the first right regular number
                    while (!parentLeftRegularNumber.right.isRegularNumber())
                        parentLeftRegularNumber = parentLeftRegularNumber.right;

                    parentLeftRegularNumber.right.value += this.left.value;
                }
            }

            // Find parent with a right value
            Pair parentRightRegularNumber = parent;
            previousPair = this;
            while (parentRightRegularNumber != null
                   && !parentRightRegularNumber.right.isRegularNumber()
                   && parentRightRegularNumber.left != previousPair) {

                // if (parentRightRegularNumber != null
                // && parentRightRegularNumber.parent != null
                // && parentRightRegularNumber.parent.left == parentRightRegularNumber) {
                // parentRightRegularNumber = parentRightRegularNumber.parent;
                // break;
                // }

                previousPair = parentRightRegularNumber;
                parentRightRegularNumber = parentRightRegularNumber.parent;

            }
            // At the top
            if (parentRightRegularNumber != null) {
                if (parentRightRegularNumber.right.isRegularNumber()) {
                    parentRightRegularNumber.right.value += this.right.value;
                } else {
                    parentRightRegularNumber = parentRightRegularNumber.right;
                    // Find the first left regular number
                    while (!parentRightRegularNumber.left.isRegularNumber())
                        parentRightRegularNumber = parentRightRegularNumber.left;

                    parentRightRegularNumber.left.value += this.right.value;
                }
            }

            // Replace itself with 0.
            if (this.parent.left == this)
                this.parent.left = new Pair(this.parent, 0);
            else
                this.parent.right = new Pair(this.parent, 0);
            this.parent = null;
        }

        /**
         * To split a regular number, replace it with a pair; the left element of the
         * pair should be the regular number divided by two and rounded down, while the
         * right element of the pair should be the regular number divided by two and
         * rounded up.
         */
        void split() {
            log.trace("Split: {}", this);

            Pair newPair = new Pair(parent, 0);
            newPair.left = new Pair(newPair, value / 2);
            newPair.right = new Pair(newPair, (value + 1) / 2);

            if (this.parent.left == this)
                this.parent.left = newPair;
            else
                this.parent.right = newPair;
            this.parent = null;

        }

        int getMagnitude() {
            return isRegularNumber() ? value : 3 * left.getMagnitude() + 2 * right.getMagnitude();
        }

        boolean isRegularNumber() {
            return ObjectUtils.allNull(left, right);
        }

        @Override
        public String toString() {
            return isRegularNumber() ? Integer.toString(value) : String.format("[%s,%s]", left, right);
        }
    }
}