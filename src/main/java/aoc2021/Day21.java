package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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

    private static final int[] DIRAC_DIE = { 0, 0, 0, 1, 3, 6, 7, 6, 3, 1 };

    private static final int POSITIONS = 11;

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

        log.info("The most wins by a player in the test data is: {}", part2(TEST_P1_START, TEST_P2_START));

        log.setLevel(Level.INFO);

        log.info("The most wins by a player in the real data is: {}", part2(P1_START, P2_START));
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

    /**
     * Play with the Dirac die until a player reaches 21. In how many universes does
     * the player who won most win?
     * 
     * @param p1Start The starting position of player 1.
     * @param p2Start The starting position of player 2.
     * 
     * @return The highest number of wins by player.
     */
    private static long part2(int p1Start, int p2Start) {
        /*// Number of player Ns on a given space with a given score
        long[][] p1Scores = new long[POSITIONS][30];
        p1Scores[p1Start][0] = 1;
        long[][] p2Scores = new long[POSITIONS][30];
        p2Scores[p2Start][0] = 1;
        
        AtomicLong p1Wins = new AtomicLong(0);
        AtomicLong p2Wins = new AtomicLong(0);
        
        // Play a round
        playGame(p1Scores, p2Scores, p1Wins, p2Wins);
        
        return Math.max(p1Wins.longValue(), p2Wins.longValue());*/

        Result gameResults = playGame(0, 0, p1Start, p2Start);

        log.debug("player 1 wins in {} universes, while player 2 wins in {} universes.",
                  gameResults.p1Wins, gameResults.p2Wins);

        return Math.max(gameResults.p1Wins, gameResults.p2Wins);
    }

    private static Result playGame(int p1Score, int p2Score, int p1Position, int p2Position) {
        final int winningScore = 21;

        // First, check if this is a winning state
        if (p1Score >= winningScore)
            return new Result(1, 0);
        else if (p2Score >= winningScore)
            return new Result(0, 1);

        // Neither wins, roll the dice and try the outcome states
        long p1Wins = 0;
        long p2Wins = 0;

        // There are 7 possible states out of this state
        for (int rollTotal = 3; rollTotal < DIRAC_DIE.length; rollTotal++) {

            // Check the outcome of each state
            int newPosition = (p1Position + rollTotal - 1) % 10 + 1;

            // Player 2 goes next, set them as p1
            Result rollResult = playGame(p2Score, p1Score + newPosition, p2Position, newPosition);

            // Multiply by the number of universes in which this happens
            rollResult.p1Wins *= DIRAC_DIE[rollTotal];
            rollResult.p2Wins *= DIRAC_DIE[rollTotal];

            // Tally the results (remembering that p1 and p2 are switched)
            p1Wins += rollResult.p2Wins;
            p2Wins += rollResult.p1Wins;

        }

        return new Result(p1Wins, p2Wins);
    }

    private static void playGame(long[][] p1Scores, long[][] p2Scores, AtomicLong p1Wins, AtomicLong p2Wins) {

        // Player 1's turn

        // Roll some dice
        long[][] newScores = new long[POSITIONS][30];
        for (int position = 1; position < POSITIONS; position++) {
            for (int rollTotal = 3; rollTotal < DIRAC_DIE.length; rollTotal++) {
                // Move the pieces
                int newPosition = (position + rollTotal - 1) % 10 + 1;

                // Tally the scores
                for (int score = 0; score + newPosition < 30; score++) {
                    // Count winner
                    if (score + newPosition >= 21)
                        p1Wins.addAndGet(p1Scores[position][score] * DIRAC_DIE[rollTotal]);

                    // Adjust non-winners
                    else
                        newScores[newPosition][score + newPosition] += p1Scores[position][score] * DIRAC_DIE[rollTotal];
                }

                // The non-winners go on to play against player 2
            }
        }
        log.debug("Player 1 wins: {}; Remaining scores:\n{}", p1Wins, printMap(newScores));
        // // Count winners
        // // TODO Could this be moved up into the previous loop?
        // for (int position = 1; position < POSITIONS; position++) {
        // for (int score = 21; score < newScores[position].length; score++) {
        // long winners = newScores[position][score];
        // p1Wins.addAndGet(winners);
        //
        // // Remove them
        // newScores[position][score] = 0;
        // }
        // }

        // Multiply the player 2 counts by the three dice rolls.
        // TODO Another trip through the map indices which could be in the previous
        // loop...
        for (int position = 1; position < p2Scores.length; position++) {
            for (int score = 0; score < p2Scores[position].length; score++) {
                p2Scores[position][score] *= 27;
            }
        }

        p1Scores = newScores;

        // Player 2's turn
        // newPositions = new long[positions];
        newScores = new long[POSITIONS][30];
        for (int position = 1; position < POSITIONS; position++) {
            for (int rollTotal = 3; rollTotal < DIRAC_DIE.length; rollTotal++) {
                // Move the pieces
                int newPosition = (position + rollTotal - 1) % 10 + 1;
                // newPositions[newPosition] += p2Positions[position] * diracDie[roll];

                // Tally the scores
                for (int score = 0; score + newPosition < 30; score++) {
                    // count the winners
                    if (score + newPosition >= 21)
                        p2Wins.addAndGet(p2Scores[position][score] * DIRAC_DIE[rollTotal]);

                    // Adjust non-winners
                    else
                        newScores[newPosition][score + newPosition] += p2Scores[position][score] * DIRAC_DIE[rollTotal];
                }
            }
        }
        log.debug("Player 2 wins: {}; Remaining scores:\n{}", p2Wins, printMap(newScores));
        // // Count winners
        // for (int position = 1; position < newScores.length; position++) {
        // for (int score = 21; score < newScores[position].length; score++) {
        // long winners = newScores[position][score];
        // p2Wins.addAndGet(winners);
        //
        // // Remove them
        // newScores[position][score] = 0;
        // }
        // }
        // Multiply the player 1 counts by the three dice rolls.
        for (int position = 1; position < p1Scores.length; position++) {
            for (int score = 0; score < p1Scores[position].length; score++) {
                p1Scores[position][score] *= 27;
            }
        }

        p2Scores = newScores;

        // Anybody left playing?

        // Play the next round...

    }

    private static int part2_old(int p1Start, int p2Start) {

        // Number of player Ns in a position
        // (with a dummy 0 position because I don't want to deal with the index offset)
        // long[] p1Positions = new long[positions];
        // p1Positions[p1Start] = 1;
        // long[] p2Positions = new long[positions];
        // p2Positions[p2Start] = 1;

        // Number of player Ns on a given space with a given score
        long[][] p1Scores = new long[POSITIONS][30];
        p1Scores[p1Start][0] = 1;
        long[][] p2Scores = new long[POSITIONS][30];
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
            long[][] newScores = new long[POSITIONS][30];
            for (int position = 1; position < POSITIONS; position++) {
                for (int roll = 3; roll < DIRAC_DIE.length; roll++) {
                    // Move the pieces
                    int newPosition = (position + roll - 1) % 10 + 1;
                    // newPositions[newPosition] += p1Positions[position] * diracDie[roll];

                    // Tally the scores
                    for (int s = 0; s + newPosition < 30; s++) {
                        newScores[newPosition][s + newPosition] += p1Scores[position][s] * DIRAC_DIE[roll];
                    }
                }
            }
            log.debug("Player 1 scores:\n{}", printMap(newScores));
            // Count winners
            for (int position = 1; position < POSITIONS; position++) {
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
            newScores = new long[POSITIONS][30];
            for (int position = 1; position < POSITIONS; position++) {
                for (int roll = 3; roll < DIRAC_DIE.length; roll++) {
                    // Move the pieces
                    int newPosition = (position + roll - 1) % 10 + 1;
                    // newPositions[newPosition] += p2Positions[position] * diracDie[roll];

                    // Tally the scores
                    for (int s = 0; s + newPosition < 30; s++) {
                        newScores[newPosition][s + newPosition] += p2Scores[position][s] * DIRAC_DIE[roll];
                    }
                }
            }
            games *= 27;
            log.debug("Player 2 scores:\n{}", printMap(newScores));
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

    private static class Result {
        long p1Wins;
        long p2Wins;

        public Result(long p1Wins, long p2Wins) {
            this.p1Wins = p1Wins;
            this.p2Wins = p2Wins;
        }

    }
}