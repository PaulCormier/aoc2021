package aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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

        log.setLevel(Level.INFO);

        part2(lines);
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

            if (day <= 28)
                log.debug("After {} days: ({}) {}", String.format("%2d", day), fish.size(), fish);
        }

        return fish.size();
    }

    private static void part2(final List<Integer> lines) {

        // Count of fish at each timer level
        long[] fishPopulation = new long[9];

        lines.stream()
             .collect(Collectors.groupingBy(n -> n, Collectors.counting()))
             .entrySet()
             .stream()
             .forEach(e -> fishPopulation[e.getKey()] = e.getValue());

        log.debug("Initial state: {}", fishPopulation);

        // Run through the days
        int days = 256;
        for (int day = 1; day <= days; day++) {

            // The number of new fish this day.
            long newFish = fishPopulation[0];

            // Increase the number of fish which are resetting on 6 next 
            fishPopulation[7] += newFish;

            // Move each group down one
            for (int i = 1; i < fishPopulation.length; i++) {
                fishPopulation[i - 1] = fishPopulation[i];
            }

            // Add new fish at 8
            fishPopulation[8] = newFish;

            if (day <= 18)
                log.debug("After {} days: {}", String.format("%2d", day), fishPopulation);
        }

        log.info("After {} days there are {} fish.", days, LongStream.of(fishPopulation).sum());
    }

    /// Things that didn't work:

    private static void test3() {
        // Count of fish at each timer level
        long[] fishPopulation = { 0, 1, 1, 2, 1, 0, 0, 0, 0 }; //new long[9];

        log.debug("Initial state: {}", fishPopulation);

        // Run through the days
        int days = 256;
        for (int day = 1; day <= days; day++) {

            // The number of new fish this day.
            long newFish = fishPopulation[0];

            // Increase the number of fish which are resetting on 6 next 
            fishPopulation[7] += newFish;

            // Move each group down one
            for (int i = 1; i < fishPopulation.length; i++) {
                fishPopulation[i - 1] = fishPopulation[i];
            }

            // Add new fish at 8
            fishPopulation[8] = newFish;

            if (day <= 18)
                log.debug("After {} days: {}", String.format("%2d", day), fishPopulation);
        }

        log.debug("After {} days there are {} fish.", days, LongStream.of(fishPopulation).sum());
    }

    private static void test2() {

        // Create the list of simple fish
        List<SimpleFish> fish = new ArrayList<>();
        fish.add(new SimpleFish(0));

        //        log.debug("Initial State: {}", fish);

        //        List<Integer> growthRate = new ArrayList<>();

        // Run through the days
        int days = 256;
        for (int day = 1; day <= days; day++) {

            // Advance the timer for each fish, and count the number of new fish
            int newFish = (int) fish.stream()
                                    .filter(f -> {
                                        if (f.timer-- < 0) {
                                            f.timer = 6;
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    })
                                    .count();

            // Add the number of new fish
            IntStream.range(0, newFish)
                     .mapToObj(i -> new SimpleFish(8))
                     .forEach(fish::add);

            //            growthRate.add(fish.size());
            // log.debug("After {} days: ({}) {}", String.format("%2d", day), fish.size(), fish);
        }

        //        log.debug("Growth rate: {}", growthRate);
        log.debug("After {} days: {}", days, fish.size());
    }

    private static void test() {

        // Create the list of Lanternfish
        List<Lanternfish> fish = new ArrayList<>();
        fish.add(new Lanternfish());

        //        log.debug("Initial State: {}", fish);

        List<Integer> growthRate = new ArrayList<>();

        // Run through 80 days
        for (int day = 1; day <= 256; day++) {

            // Advance the timer for each fish, and count the number of new fish
            int newFish = (int) fish.stream()
                                    .filter(Lanternfish::day)
                                    .count();

            // Add the number of new fish
            IntStream.range(0, newFish)
                     .mapToObj(i -> new Lanternfish())
                     .forEach(fish::add);

            growthRate.add(fish.size());
            // log.debug("After {} days: ({}) {}", String.format("%2d", day), fish.size(), fish);
        }

        //        log.debug("Growth rate: {}", growthRate);
        log.debug("After 256 days: {}", fish.size());
    }

    private static class SimpleFish {
        int timer;

        SimpleFish(int timer) {
            this.timer = timer;
        }
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