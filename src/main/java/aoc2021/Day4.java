package aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

/**
 * https://adventofcode.com/2021/day/4
 * 
 * @author Paul Cormier
 *
 */
public class Day4 {

    private static final String INPUT_TXT = "Input-Day4.txt";
    private static final List<Integer> NUMBERS = Arrays.asList(27, 14, 70, 7, 85, 66, 65, 57, 68, 23, 33, 78, 4, 84, 25,
                                                               18, 43, 71, 76, 61, 34, 82, 93, 74, 26, 15, 83, 64, 2,
                                                               35, 19, 97, 32, 47, 6, 51, 99, 20, 77, 75, 56, 73, 80,
                                                               86, 55, 36, 13, 95, 52, 63, 79, 72, 9, 10, 16, 8, 69, 11,
                                                               50, 54, 81, 22, 45, 1, 12, 88, 44, 17, 62, 0, 96, 94, 31,
                                                               90, 39, 92, 37, 40, 5, 98, 24, 38, 46, 21, 30, 49, 41,
                                                               87, 91, 60, 48, 29, 59, 89, 3, 42, 58, 53, 67, 28);

    private static final String TEST_INPUT_TXT = "TestInput-Day4.txt";
    private static final List<Integer> TEST_NUMBERS = Arrays.asList(7, 4, 9, 5, 11, 17, 23, 2, 0, 14, 21, 24, 10, 16,
                                                                    13, 6, 15, 25, 12, 22, 18, 20, 8, 19, 3, 26, 1);

    public static void main(String[] args) {

        // Read the test file
        List<String> testBingoCardLines = FileUtils.readFileToStream(TEST_INPUT_TXT)
                                                   .filter(StringUtils::isNotBlank)
                                                   .collect(Collectors.toList());

        // System.err.println(testBingoCardLines);

        List<BingoCard> testBingoCards = new ArrayList<>();
        // Parse the lines into bingo cards
        for (int i = 0; i < testBingoCardLines.size(); i += 5) {
            testBingoCards.add(new BingoCard(testBingoCardLines.subList(i, i + 5)));
        }

        // System.err.println(testBingoCards);

        int testScore = part1(testBingoCards, TEST_NUMBERS);
        System.out.println("The score for the test data is: " + testScore);

        // Read the test file
        List<String> bingoCardLines = FileUtils.readFileToStream(INPUT_TXT)
                                               .filter(StringUtils::isNotBlank)
                                               .collect(Collectors.toList());

        // System.err.println(bingoCardLines);

        List<BingoCard> bingoCards = new ArrayList<>();
        // Parse the lines into bingo cards
        for (int i = 0; i < bingoCardLines.size(); i += 5) {
            bingoCards.add(new BingoCard(bingoCardLines.subList(i, i + 5)));
        }

        // System.err.println(bingoCards);

        int score = part1(bingoCards, NUMBERS);
        System.out.println("The score for the real data is: " + score);
        // System.err.println(bingoCards);

        // PART 2

        // What id the score of the last card to win?
        testScore = part2(testBingoCards, TEST_NUMBERS);
        System.out.println("The score for the test data is: " + testScore);
        score = part2(bingoCards, NUMBERS);
        System.out.println("The score for the real data is: " + score);
    }

    /**
     * Given a list of {@link BingoCard}s and a list of numbers, go through the
     * list, and find the score of the winning card.
     * 
     * @param bingoCards The list of bingo cards to check.
     * @param numbers The list of numbers to check.
     * @return The final score; which is the winning card's score multiplied by the
     *     winning number.
     */
    private static int part1(List<BingoCard> bingoCards, List<Integer> numbers) {

        // System.err.println(bingoCards);

        // Mark the numbers sequentially, and watch for a winner
        for (int number : numbers) {
            // System.err.println("\n" + number);

            for (BingoCard card : bingoCards) {
                card.markNumber(number);
                if (card.isBingo())
                    // Get the score of the winning card
                    return number * card.getScore();
            }

            // System.err.println(bingoCards);

        }
        return -1;
    }

    /**
     * Given a list of {@link BingoCard}s and a list of numbers, go through the
     * list, and find the score of the last winning card.
     * 
     * @param bingoCards The list of bingo cards to check.
     * @param numbers The list of numbers to check.
     * @return The final score; which is the last winning card's score multiplied by
     *     the winning number.
     */
    private static int part2(final List<BingoCard> bingoCards, final List<Integer> numbers) {

        // System.err.println(bingoCards);

        List<BingoCard> cardsLeft = new ArrayList<>(bingoCards);
        // Mark the numbers sequentially, and
        for (int number : numbers) {
            // System.err.println("\n" + number);
            List<BingoCard> winningCards = new ArrayList<>();
            for (BingoCard card : cardsLeft) {
                card.markNumber(number);
                // watch for a winner
                if (card.isBingo())
                    // Remove it
                    winningCards.add(card);
            }
            if (cardsLeft.size() == 1 && winningCards.size() == 1)
                // We have a winner
                return number * winningCards.get(0).getScore();
            else
                cardsLeft.removeAll(winningCards);

            // System.err.println(bingoCards);

        }
        return -1;
    }

    /**
     * A Bingo card, initialised from a list of numbers. It can track numbers marked
     * off, determine if it's a winner, and compute its score.
     * 
     * @author Paul
     *
     */
    private static class BingoCard {

        Integer[][] numbers = new Integer[5][5];

        BingoCard(List<String> lines) {
            int i = 0;
            for (String line : lines) {
                // System.err.println(line);
                int j = 0;
                for (String digit : line.trim().split(" +"))
                    numbers[i][j++] = Integer.valueOf(digit);
                i++;
            }
        }

        void markNumber(int number) {
            for (int i = 0; i < numbers.length; i++) {
                for (int j = 0; j < numbers[i].length; j++) {
                    if (numbers[i][j] != null && numbers[i][j] == number) {
                        numbers[i][j] = null;
                        return;
                    }
                }
            }
        }

        boolean isBingo() {
            // Check rows
            for (int i = 0; i < numbers.length; i++) {
                boolean bingo = true;
                for (int j = 0; j < numbers[i].length; j++) {
                    bingo &= numbers[i][j] == null;
                }
                if (bingo)
                    return true;
            }
            // Check rows
            for (int i = 0; i < numbers.length; i++) {
                boolean bingo = true;
                for (int j = 0; j < numbers[i].length; j++) {
                    bingo &= numbers[j][i] == null;
                }
                if (bingo)
                    return true;
            }
            return false;
        }

        int getScore() {
            int score = 0;
            for (int i = 0; i < numbers.length; i++) {
                for (int j = 0; j < numbers[i].length; j++) {
                    if (numbers[i][j] != null)
                        score += numbers[i][j];
                }
            }
            return score;
        }

        @Override
        public String toString() {
            StringBuilder cardString = new StringBuilder("\n");

            for (int i = 0; i < numbers.length; i++) {
                for (int j = 0; j < numbers[i].length; j++) {
                    cardString.append(numbers[i][j] == null ? "   " : String.format("%2d ", numbers[i][j]));
                }
                cardString.append("\n");
            }

            return cardString.toString();
        }
    }

}