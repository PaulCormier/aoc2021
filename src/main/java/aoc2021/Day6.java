package aoc2021;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/6
 * 
 * @author Paul Cormier
 *
 */
public class Day6 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day6.class);

    private static final String INPUT_TXT = "Input-Day6.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day6.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<Integer> testLines = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                           .flatMap(l -> Stream.of(l.split(",")))
                                           .map(Integer::valueOf)
                                           .collect(Collectors.toList());
        log.trace(testLines.toString());

        log.info("After 80 days there are {} fish.", part1(testLines));

        log.setLevel(Level.INFO);

        // Read the real file
        List<Integer> lines = FileUtils.readFileToStream(INPUT_TXT)
                                       .flatMap(l -> Stream.of(l.split(",")))
                                       .map(Integer::valueOf)
                                       .collect(Collectors.toList());

        log.info("After 80 days there are {} fish.", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        part2(testLines);

        //        log.info("");

        log.setLevel(Level.INFO);

        part2(lines);

        //        log.info("");
    }

    private static int part1(final List<Integer> lines) {
        // Create the list of Lanternfish
        List<Lanternfish> fish = lines.stream()
                                      .map(Lanternfish::new)
                                      .collect(Collectors.toList());

        log.debug("Initial State: {}", fish);

        // Run through 80 days
        for (int day = 1; day <= 80; day++) {

            // Advance the timer for each fish, and count the number of new fish
            int newFish = (int) fish.stream()
                                    .filter(Lanternfish::day)
                                    .count();

            // Add the number of new fish
            IntStream.range(0, newFish)
                     .mapToObj(i -> new Lanternfish())
                     .forEach(fish::add);

            if (day <= 18)
                log.debug("After {} days: {}", String.format("%2d", day), fish);
        }

        return fish.size();
    }

    private static void part2(final List<Integer> lines) {

    }

    private static class Lanternfish {
        int timer;

        Lanternfish() {
            this(8);
        }

        Lanternfish(int timer) {
            this.timer = timer;
        }

        /**
         * Decrement the timer by one, or reset it to 6 if it has reached 0.
         * 
         * @return Whether or not the timer had reached 0.
         */
        boolean day() {
            if (timer == 0) {
                this.timer = 6;
                return true;
            } else {
                this.timer--;
                return false;
            }
        }

        @Override
        public String toString() {
            return Integer.toString(timer);
        }
    }

}