package aoc2021;

import java.util.stream.IntStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/17
 * 
 * @author Paul Cormier
 *
 */
public class Day17 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day17.class);

    //target area: x=124..174, y=-123..-86
    private static final int[] TARGET_RANGES = { 124, 174, -123, -86 };

    //target area: x=20..30, y=-10..-5
    private static final int[] TEST_TARGET_RANGES = { 20, 30, -10, -5 };

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        log.info("The highest the probe can go for the test data is: {}", part1(TEST_TARGET_RANGES));

        log.setLevel(Level.INFO);

        log.info("The highest the probe can go for the real data is: {}", part1(TARGET_RANGES));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("{}", part2(TEST_TARGET_RANGES));

        log.setLevel(Level.INFO);

        log.info("{}", part2(TARGET_RANGES));
    }

    /**
     * What is the highest y position it reaches on this trajectory?
     * 
     * @param targetRanges
     *            The x and y coordinate ranges in the order: minX, maxX, minY,
     *            maxY.
     * @return The maximum height the probe can reach, and still hit the target
     *         range.
     */
    private static int part1(final int[] targetRanges) {
        int minX = targetRanges[0];
        int maxX = targetRanges[1];
        int minY = targetRanges[2];
        int maxY = targetRanges[3];

        // To reach the maximum height, the probe should be shot up as fast as possible
        // but not so fast that it misses the target box.

        // Figure out the y velocity needed to still hit the box
        int yVelocity = -minY - 1;

        // How long will the probe take to come back down, then hit the bottom of the target?
        int airTime = yVelocity * 2 + 2;

        // What x velocity is needed to be going straight down on the last step?
        // Sum 1:xVelocity = minX (Quadratic equation)
        int xVelocity = (int) (Math.sqrt(1 + 8 * minX) / 2);

        Probe probe = new Probe(xVelocity, yVelocity);
        log.debug("Initial probe parameters: {}, impact in {} steps.", probe, airTime);

        int estimatedHeight = (1 + yVelocity) * yVelocity / 2;

        // Technically, I have the solution at this point, the rest is just to confirm.

        int maxHeight = IntStream.rangeClosed(1, airTime)
                                 .peek(i -> probe.step())
                                 .peek(i -> log.debug(probe.toString()))
                                 .map(i -> probe.yPosition)
                                 .max()
                                 .getAsInt();

        log.debug("Target: ({},{})", minX, minY);
        log.debug("Max recorded height: {} (computed: {})", maxHeight, estimatedHeight);
        log.debug("Final probe state: {}", probe);

        return maxHeight;
    }

    private static int part2(final int[] targetRanges) {

        return -1;
    }

    private static class Probe {
        int xPosition = 0;
        int yPosition = 0;

        int xVelocity;
        int yVelocity;

        int step = 0;

        Probe(int xVelocity, int yVelocity) {
            this.xVelocity = xVelocity;
            this.yVelocity = yVelocity;
        }

        void step() {
            xPosition += xVelocity;
            yPosition += yVelocity;
            xVelocity -= xVelocity > 0 ? 1 : (xVelocity == 0 ? 0 : -1);
            yVelocity--;

            step++;
        }

        @Override
        public String toString() {
            return String.format("Step %d: (%d,%d) v(%d,%d)", step, xPosition, yPosition, xVelocity, yVelocity);
        }
    }

}