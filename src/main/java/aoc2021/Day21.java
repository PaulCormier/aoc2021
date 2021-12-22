package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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

        log.info("{}", part2(TEST_P1_START, TEST_P2_START));

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

    private static int part2(int p1Start, int p2Start) {

        int[] diracDie = { 0, 0, 0, 1, 3, 6, 7, 6, 3, 1 };
        int positions = 11;

        // Number of player Ns in a position
        // (with a dummy 0 position because I don't want to deal with the index offset)
        // long[] p1Positions = new long[positions];
        // p1Positions[p1Start] = 1;
        // long[] p2Positions = new long[positions];
        // p2Positions[p2Start] = 1;

        // Number of player Ns on a given space with a given score
        long[][] p1Scores = new long[positions][30];
        p1Scores[p1Start][0] = 1;
        long[][] p2Scores = new long[positions][30];
        p2Scores[p2Start][0] = 1;

        int maxScore = 21;
        int maxTurn = 10;

        long games = 1;

        int turn = 1;
        long[] p1Wins = new long[maxTurn + 1];
        long[] p2Wins = new long[maxTurn + 1];
        while (turn <= maxTurn) {
            log.debug("There are {} universes on turn {}.", games, turn);
            // Player 1's turn
            // long[] newPositions = new long[positions];
            long[][] newScores = new long[positions][30];
            for (int position = 1; position < positions; position++) {
                for (int roll = 3; roll < diracDie.length; roll++) {
                    // Move the pieces
                    int newPosition = (position + roll - 1) % 10 + 1;
                    // newPositions[newPosition] += p1Positions[position] * diracDie[roll];

                    // Tally the scores
                    for (int s = 0; s + newPosition < 30; s++) {
                        newScores[newPosition][s + newPosition] += p1Scores[position][s] * diracDie[roll];
                    }
                }
            }
            log.debug("Player 1 scores:\n{}",printMap(newScores));
            // Count winners
            for (int position = 1; position < positions; position++) {
                for (int score = 21; score < newScores[position].length; score++) {
                    long winners = newScores[position][score];
                    p1Wins[turn] += winners;

                    // Remove them
                    // newPositions[position] -= winners;
                    newScores[position][score] = 0;
                }
            }
            games *= 27;
            // Multiply the player 2 counts by the three dice rolls.
            for (int position = 1; position < p2Scores.length; position++) {
                for (int score = 0; score < p2Scores[position].length; score++) {
                    p2Scores[position][score] *= 27;
                }
            }

            // p1Positions = newPositions;
            p1Scores = newScores;

            // Player 2's turn
            // newPositions = new long[positions];
            newScores = new long[positions][30];
            for (int position = 1; position < positions; position++) {
                for (int roll = 3; roll < diracDie.length; roll++) {
                    // Move the pieces
                    int newPosition = (position + roll - 1) % 10 + 1;
                    // newPositions[newPosition] += p2Positions[position] * diracDie[roll];

                    // Tally the scores
                    for (int s = 0; s + newPosition < 30; s++) {
                        newScores[newPosition][s + newPosition] += p2Scores[position][s] * diracDie[roll];
                    }
                }
            }
            games *= 27;
            log.debug("Player 2 scores:\n{}",printMap(newScores));
            // Count winners
            for (int position = 1; position < newScores.length; position++) {
                for (int score = 21; score < newScores[position].length; score++) {
                    long winners = newScores[position][score];
                    // But player 1 had to not win first
                    double player1NoWin = (games - p1Wins[turn] * 27);
                    p2Wins[turn] += winners / (double) games * player1NoWin;

                    // Remove them
                    // newPositions[position] -= winners;
                    newScores[position][score] = 0;
                }
            }
            // Multiply the player 1 counts by the three dice rolls.
            for (int position = 1; position < p1Scores.length; position++) {
                for (int score = 0; score < p1Scores[position].length; score++) {
                    p1Scores[position][score] *= 27;
                }
            }
            games -= p1Wins[turn] * 27 + p2Wins[turn];

            // p2Positions = newPositions;
            p2Scores = newScores;

            log.debug("Player 1 won {} times, player 2 won {} times.", p1Wins[turn], p2Wins[turn]);

            
            turn++;
        }

        long p1TotalWins = Arrays.stream(p1Wins).sum();
        long p2TotalWins = Arrays.stream(p2Wins).sum();

        log.debug("In total player 1 won {} times, and player 2 won {}.", p1TotalWins, p2TotalWins);

        return -1;
    }

    private static String printMap(long[][] map) {
        return printMap(map, ",");
    }

    private static String printMap(long[][] map, String separator) {
        return Arrays.stream(map)
                     .map(l -> LongStream.of(l)
                                        .mapToObj(Long::toString)
                                        .collect(Collectors.joining(separator)))
                     .collect(Collectors.joining("\n"));
    }

}