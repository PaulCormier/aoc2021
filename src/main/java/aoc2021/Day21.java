package aoc2021;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/21
 * 
 * @author Paul Cormier
 *
 */
public class Day21 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day21.class);

    private static final int P1_START = 8;
    private static final int P2_START = 9;

    private static final int TEST_P1_START = 4;
    private static final int TEST_P2_START = 8;

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        log.debug(IntStream.rangeClosed(1, 30).map(n -> (n - 1) % 10 + 1).mapToObj(Integer::toString)
                           .collect(Collectors.joining(",")));

        log.info("Starting from {} and {}, the product of the score of the losing player and the number of dice rolls is: {}",
                 TEST_P1_START, TEST_P2_START, part1(TEST_P1_START, TEST_P2_START));

        log.setLevel(Level.INFO);

        log.info("Starting from {} and {}, the product of the score of the losing player and the number of dice rolls is: {}",
                 P1_START, P2_START, part1(P1_START, P2_START));

        // PART 2

        log.setLevel(Level.DEBUG);

        // log.info("{}", part2(TEST_P1_START, TEST_P2_START));

        log.setLevel(Level.INFO);

        // log.info("{}", part2(P1_START, P2_START));
    }

    private static int part1(int p1Start, int p2Start) {

        // Deterministic die
        AtomicInteger die = new AtomicInteger(1);

        int p1Score = 0;
        int p2Score = 0;

        int p1Position = p1Start;
        int p2Position = p2Start;

        while (p1Score < 1000 && p2Score < 1000) {
            // Player 1's turn
            p1Position = (p1Position + die.getAndIncrement() + die.getAndIncrement() + die.getAndIncrement() - 1)
                         % 10 + 1;
            p1Score += p1Position;

            if (p1Score >= 1000)
                break;

            // Player 2's turn
            p2Position = (p2Position + die.getAndIncrement() + die.getAndIncrement() + die.getAndIncrement() - 1)
                         % 10 + 1;
            p2Score += p2Position;

        }

        log.info("Player {} wins! Player {} score: {}, Dice rolls: {}",
                 p1Score > p2Score ? 1 : 2, p1Score > p2Score ? 2 : 1,
                 p1Score > p2Score ? p2Score : p1Score, die.get() - 1);

        return (p1Score > p2Score ? p2Score : p1Score) * (die.get() - 1);
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

}