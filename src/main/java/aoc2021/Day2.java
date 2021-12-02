package aoc2021;

import java.util.List;

/**
 * https://adventofcode.com/2021/day/2
 * 
 * @author Paul Cormier
 *
 */
public class Day2 {

    private static final String INPUT_TXT = "Input-Day2.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day2.txt";

    public static void main(String[] args) {

        // Read the test file
        List<String> testDirections = FileUtils.readFile(TEST_INPUT_TXT);

        // Follow the directions
        Submarine testSub = new Submarine();
        directSubmarine(testSub, testDirections);

        // The final position should be a horizontal position of 15 and a depth of 10.
        System.out.printf("The final position is %d and a depth of %d.%n", testSub.horizontalPosition, testSub.depth);

        // Read the test file
        List<String> directions = FileUtils.readFile(INPUT_TXT);

        // Follow the directions
        Submarine sub = new Submarine();
        directSubmarine(sub, directions);

        System.out.printf("The final position is %d and a depth of %d.%n", sub.horizontalPosition, sub.depth);
        System.out.printf("The product of the final position is: %d%n", sub.horizontalPosition * sub.depth);

        // PART 2

        // Follow the directions with a V2 sub
        SubmarineV2 testSub2 = new SubmarineV2();
        directSubmarine(testSub2, testDirections);

        // The final position should be a horizontal position of 15 and a depth of 610.
        System.out.printf("The final position is %d and a depth of %d.%n", testSub2.horizontalPosition, testSub2.depth);

        // Follow the directions
        SubmarineV2 sub2 = new SubmarineV2();
        directSubmarine(sub2, directions);

        System.out.printf("The final position is %d and a depth of %d.%n", sub2.horizontalPosition, sub2.depth);
        System.out.printf("The product of the final position is: %d%n", sub2.horizontalPosition * sub2.depth);
    }

    /**
     * Follow the directions and modify the {@link Submarine}'s position.
     * 
     * @param sub
     *            The submarine to be moved around.
     * @param directions
     *            The list of directions to follow.
     */
    static void directSubmarine(final Submarine sub, final List<String> directions) {
        directions.stream()
                  .map(d -> d.split(" "))
                  .forEach(d -> {
                      int amount = Integer.parseInt(d[1]);
                      switch (d[0]) {
                          case "forward":
                              sub.forward(amount);
                              break;
                          case "up":
                              sub.up(amount);
                              break;
                          case "down":
                              sub.down(amount);
                              break;
                      }
                  });
    }

    /**
     * Representation of a submarine. It can move forward, up, or down.
     */
    static class Submarine {
        int horizontalPosition = 0;
        int depth = 0;

        void forward(int amount) {
            horizontalPosition += amount;
        }

        void down(int amount) {
            depth += amount;
        }

        void up(int amount) {
            depth -= amount;
        }
    }

    /**
     * Representation of a V2 submarine. It can move forward, up, or down.
     */
    static class SubmarineV2 extends Submarine {
        int aim = 0;

        void forward(int amount) {
            horizontalPosition += amount;
            depth += aim * amount;
        }

        void down(int amount) {
            aim += amount;
        }

        void up(int amount) {
            aim -= amount;
        }
    }
}