package aoc2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

        // log.info("{} cubes are on in the test data.",
        // part1(Arrays.asList(testLines.get(0).split("\n"))));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        // log.info("{} cubes are on in the real data.", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        // log.info("{} cubes are on in initialization region of the test data.",
        // part1(Arrays.asList(testLines.get(1).split("\n"))));

        log.info("{} cubes are on in the test data.", part2(Arrays.asList(testLines.get(1).split("\n"))));

        log.setLevel(Level.INFO);

        // log.info("{} cubes are on in the real data.", part2(lines));
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

    /**
     * Given the larger set of data, determine how many cubes are on.
     * 
     * @param lines The reboot instructions.
     * @return The number of cubes which are on.
     */
    private static long part2(final List<String> lines) {

        AtomicInteger id = new AtomicInteger(1);

        List<Cuboid> cuboids = lines.stream()
                                    .map(l -> new Cuboid(Integer.toString(id.getAndIncrement()), l))
                                    .collect(Collectors.toList());

        log.trace("\n{}", cuboids.stream().map(Cuboid::toString).collect(Collectors.joining("\n")));

        // Apply the first 10 (this will give a good checkpoint for the test data)
        List<Cuboid> processed = new ArrayList<>();
        processed.add(cuboids.get(0));
        for (Cuboid otherCuboid : cuboids.subList(1, 10)) {
            for (Cuboid cuboid : processed) {
                if (cuboid.equals(otherCuboid))
                    continue;
                if (cuboid.overlaps(otherCuboid)) {
                   long difference= cuboid.subtract(otherCuboid);
                    log.debug("{} overlaps with {} by {}.", otherCuboid, cuboid, difference);
                }
            }
            processed.add(otherCuboid);
        }

        // Apply the overlaps from each subsequent cuboid

        return processed.stream().filter(c -> c.on).mapToLong(Cuboid::getVolume).sum();
    }

    private static boolean isInRange(int value, int minValue, int maxValue) {
        return minValue <= value && value <= maxValue;
    }

    /**
     * A cubic region in 3 dimensional space.
     */
    private static class Cuboid {
        String id;

        boolean on;
        int xMin;
        int yMin;
        int zMin;
        int xMax;
        int yMax;
        int zMax;

        long initialVolume;

        Set<Cuboid> containedCuboids = new HashSet<>();

        Cuboid(String id, String parameters) {
            this.id = id;

            this.on = parameters.startsWith("on");

            // Min/max x,y,z
            int[] coordinates = Arrays.stream(parameters.substring(on ? 3 : 4).split("(,?[xyz]=)|(\\.\\.)"))
                                      .filter(StringUtils::isNotBlank)
                                      .mapToInt(Integer::parseInt)
                                      .toArray();
            this.xMin = coordinates[0];
            this.xMax = coordinates[1];
            this.yMin = coordinates[2];
            this.yMax = coordinates[3];
            this.zMin = coordinates[4];
            this.zMax = coordinates[5];

            this.initialVolume = (xMax - xMin) * (yMax - yMin) * (zMax - zMin);

        }

        Cuboid(String id, boolean on, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
            this.id = id;
            
            this.on = on;
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.zMin = zMin;
            this.zMax = zMax;

            this.initialVolume = (xMax - xMin) * (yMax - yMin) * (zMax - zMin);
        }

        /**
         * Determine the volume of "on" spaces of this cuboid.
         * 
         * @return The number of effectively "on" cubes in this region.
         */
        long getVolume() {
            // if (on)
            return initialVolume - containedCuboids.stream().mapToLong(Cuboid::getVolume).sum();
            // else
            // return containedCuboids.stream().mapToLong(Cuboid::getVolume).sum();
        }

        /**
         * Determine if this cuboid overlaps another's region.
         * 
         * @return true if these two cuboids share a region of space.
         */
        boolean overlaps(Cuboid otherCuboid) {
            return ((isInRange(otherCuboid.xMin, this.xMin, this.xMax)
                     || isInRange(otherCuboid.xMax, this.xMin, this.xMax))
                    && (isInRange(otherCuboid.yMin, this.yMin, this.yMax)
                        || isInRange(otherCuboid.yMax, this.yMin, this.yMax))
                    && (isInRange(otherCuboid.zMin, this.zMin, this.zMax)
                        || isInRange(otherCuboid.zMax, this.zMin, this.zMax)));
        }

        /**
         * Create an enclosed cuboid representing the overlapping region.
         * 
         * @param otherCuboid The other cuboid to overlap.
         */
        long subtract(Cuboid otherCuboid) {

            // New region boundaries
            int xMin = Math.max(this.xMin, otherCuboid.xMin);
            int yMin = Math.max(this.yMin, otherCuboid.yMin);
            int zMin = Math.max(this.zMin, otherCuboid.zMin);
            int xMax = Math.min(this.xMax, otherCuboid.xMax);
            int yMax = Math.min(this.yMax, otherCuboid.yMax);
            int zMax = Math.min(this.zMax, otherCuboid.zMax);

            Cuboid overlap = new Cuboid(this.id + "-" + (containedCuboids.size() + 1),
                                        otherCuboid.on, xMin, yMin, zMin, xMax, yMax, zMax);
            // Add its overlap to any of the contained cuboids
            for (Cuboid containedCuboid : this.containedCuboids) {
                if (containedCuboid.overlaps(otherCuboid))
                    containedCuboid.subtract(otherCuboid);
            }
            this.containedCuboids.add(overlap);

            return overlap.getVolume();
        }

        @Override
        public String toString() {
            return String.format("#%s: %s x=%d..%d,y=%d..%d,z=%d..%d (%d)", id,
                                 on ? "on" : "off", xMin, xMax, yMin, yMax, zMin, zMax, getVolume());
        }
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