package aoc2021;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/22
 * 
 * @author Paul Cormier
 *
 */
public class Day22 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day22.class);

    private static final String INPUT_TXT = "Input-Day22.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day22.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = Arrays.asList(FileUtils.readFileToStream(TEST_INPUT_TXT)
                                                        .collect(Collectors.joining("\n"))
                                                        .split("\n\n"));
        log.trace(testLines.toString());

        log.info("{} cubes are on in the test data.", part1(Arrays.asList(testLines.get(0).split("\n"))));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("{} cubes are on in the real data.", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("{} cubes are on in initialization region of the test data.",
                 part1(Arrays.asList(testLines.get(1).split("\n"))));

        log.info("{} cubes are on in the test data.", part2(Arrays.asList(testLines.get(1).split("\n"))));

        log.setLevel(Level.INFO);

        log.info("{} cubes are on in the real data.", part2(lines));
    }

    /**
     * Given a list of reboot instructions, turn cubes on or off in the reactor, and
     * count how many are on at the end. Limit the instructions to +/- 50.
     * 
     * @param lines The reboot instructions
     * @return The number of cubes which are on.
     */
    private static int part1(final List<String> lines) {

        Set<Cube> activeCubes = new HashSet<>();

        // Parse the lines
        for (String line : lines) {
            boolean on = line.startsWith("on");

            // Min/max x,y,z
            int[] coordinates = Arrays.stream(line.substring(on ? 3 : 4).split("(,?[xyz]=)|(\\.\\.)"))
                                      .filter(StringUtils::isNotBlank)
                                      .mapToInt(Integer::parseInt)
                                      .toArray();

            if (on) {
                // Add cubes to the set
                for (int x = coordinates[0]; x <= coordinates[1]; x++) {
                    if (Math.abs(x) > 50)
                        continue;
                    for (int y = coordinates[2]; y <= coordinates[3]; y++) {
                        if (Math.abs(y) > 50)
                            continue;
                        for (int z = coordinates[4]; z <= coordinates[5]; z++) {
                            if (Math.abs(z) > 50)
                                continue;

                            activeCubes.add(new Cube(x, y, z));
                        }
                    }
                }
            } else {
                // removes cubes from the set
                for (int x = coordinates[0]; x <= coordinates[1]; x++) {
                    if (Math.abs(x) > 50)
                        continue;
                    for (int y = coordinates[2]; y <= coordinates[3]; y++) {
                        if (Math.abs(y) > 50)
                            continue;
                        for (int z = coordinates[4]; z <= coordinates[5]; z++) {
                            if (Math.abs(z) > 50)
                                continue;

                            activeCubes.remove(new Cube(x, y, z));
                        }
                    }
                }
            }
        }
        return activeCubes.size();
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

    /**
     * A cube in 3 dimensional space.
     */
    private static class Cube {
        int x;
        int y;
        int z;

        /**
         * Create a new cube in three dimensional space.
         * 
         * @param x The x coordinate value.
         * @param y The y coordinate value.
         * @param z The z coordinate value.
         */
        public Cube(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Cube other = (Cube) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("(%d,%d,%d)", x, y, z);
        }

    }

}