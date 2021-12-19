package aoc2021;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/19
 * 
 * @author Paul Cormier
 *
 */
public class Day19 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day19.class);

    private static final String INPUT_TXT = "Input-Day19.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day19.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = Arrays.asList(FileUtils.readFileToStream(TEST_INPUT_TXT)
                                                        .filter(l -> !l.startsWith("---"))
                                                        .collect(Collectors.joining(" "))
                                                        .split("  "));
        log.trace(testLines.toString());

        log.info("There are {} beacons in the test map.", part1(testLines));

        // log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = Arrays.asList(FileUtils.readFileToStream(INPUT_TXT)
                                                    .filter(l -> !l.startsWith("---"))
                                                    .collect(Collectors.joining(" "))
                                                    .split("  "));

        log.info("There are {} beacons in the real map.", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        log.info("{}", part2(lines));
    }

    /**
     * Reorient the points in each scanner's map to determine how many beacons there
     * really are.
     * 
     * @param lines The coordinates of the beacons as seen by each scanner.
     * @return The number of beacons in the complete map.
     */
    private static int part1(final List<String> lines) {

        // Parse the points into maps
        List<Set<Point3D>> scannerMaps = lines.stream()
                                              .map(map -> Arrays.stream(map.split(" "))
                                                                .map(line -> line.split(","))
                                                                .map(coordinates -> new Point3D(Integer.parseInt(coordinates[0]),
                                                                                                Integer.parseInt(coordinates[1]),
                                                                                                Integer.parseInt(coordinates[2])))
                                                                .collect(Collectors.toSet()))
                                              .collect(Collectors.toList());

        Set<Point3D> baseMap = scannerMaps.remove(0);

        log.trace(baseMap.toString());

        // While there are still maps to check, check them
        while (scannerMaps.size() > 0) {
            log.debug("There are {} maps remaining to be checked.", scannerMaps.size());

            // Start with two points in the base map, find their deltas
            Set<Point3D> matchingMap = null;
            nextMap: //
            for (Point3D point1 : baseMap) {
                for (Point3D point2 : baseMap) {
                    if (point1.equals(point2))
                        continue;

                    int deltaX = (point1.x - point2.x);
                    int deltaY = (point1.y - point2.y);
                    int deltaZ = (point1.z - point2.z);

                    log.trace("Points {} and {} differ by: ({},{},{}).", point1, point2, deltaX, deltaY, deltaZ);

                    matchingMap = scanMaps(baseMap, scannerMaps, point1, deltaX, deltaY, deltaZ);

                    // Remove the matching map
                    if (matchingMap != null) {
                        scannerMaps.remove(matchingMap);
                        break nextMap;
                    }

                }
            }
            log.trace("Next map...");

            if (matchingMap == null) {
                log.warn("Didn't find any new maps...");
                break;
            }
        }

        return baseMap.size();
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

    private static Set<Point3D> scanMaps(Set<Point3D> baseMap, List<Set<Point3D>> scannerMaps,
                                         Point3D point1, int deltaX, int deltaY, int deltaZ) {

        int mapNum = 1;
        // Try to find a matching pair in another map
        for (Set<Point3D> scannerMap : scannerMaps) {

            // Copy the map

            Set<Point3D> rotatedMap = scannerMap.stream().map(Point3D::copy)
                                                .collect(Collectors.toSet());

            // Go through the rotations
            for (int x = 0; x < 4; x++) {
                for (Point3D point : rotatedMap)
                    point.rotate(1, 0, 0);
                for (int y = 0; y < 4; y++) {
                    for (Point3D point : rotatedMap)
                        point.rotate(0, 1, 0);
                    for (int z = 0; z < 4; z++) {
                        for (Point3D point : rotatedMap)
                            point.rotate(0, 0, 1);

                        // If it matches, add it to the main map
                        // and remove it from the set of maps to check
                        Set<Point3D> foundMap = findInMap(baseMap, rotatedMap, point1, deltaX, deltaY,
                                                          deltaZ);
                        if (foundMap != null) {
                            log.debug("Found a match in map {}", mapNum);
                            baseMap.addAll(foundMap);
                            return scannerMap;
                        }
                    }
                }
            }
            mapNum++;
        }

        return null;
    }

    private static Set<Point3D> findInMap(Set<Point3D> baseMap, Set<Point3D> scannerMap,
                                          Point3D point1, int deltaX, int deltaY, int deltaZ) {

        // Try to find a matching pair in another map
        for (Point3D candidatePoint1 : scannerMap) {
            for (Point3D candidatePoint2 : scannerMap) {
                if (candidatePoint1.equals(candidatePoint2))
                    continue;

                if ((candidatePoint1.x - candidatePoint2.x) == deltaX &&
                    (candidatePoint1.y - candidatePoint2.y) == deltaY &&
                    (candidatePoint1.z - candidatePoint2.z) == deltaZ) {
                    log.trace("Points {} and {} are a match!", candidatePoint1, candidatePoint2);

                    double distance11 = Math.sqrt(Math.pow((point1.x - candidatePoint1.x), 2) +
                                                  Math.pow((point1.y - candidatePoint1.y), 2) +
                                                  Math.pow((point1.z - candidatePoint1.z), 2));
                    double distance12 = Math.sqrt(Math.pow((point1.x - candidatePoint2.x), 2) +
                                                  Math.pow((point1.y - candidatePoint2.y), 2) +
                                                  Math.pow((point1.z - candidatePoint2.z), 2));

                    int transX = distance11 < distance12 ? point1.x - candidatePoint1.x
                            : point1.x - candidatePoint2.x;
                    int transY = distance11 < distance12 ? point1.y - candidatePoint1.y
                            : point1.y - candidatePoint2.y;
                    int transZ = distance11 < distance12 ? point1.z - candidatePoint1.z
                            : point1.z - candidatePoint2.z;

                    log.trace("The translation between the two spaces is: ({},{},{}).", transX, transY, transZ);

                    // Maybe take a copy of the map?
                    Set<Point3D> translatedMap = scannerMap.stream().map(Point3D::copy)
                                                           .collect(Collectors.toSet());
                    // Translate the coordinates
                    translatedMap.stream().forEach(p -> p.translate(transX, transY, transZ));
                    // log.debug("Translated points: {} and {}.", candidatePoint1, candidatePoint2);

                    // Are the at least 12 matching points?
                    Collection<Point3D> intersection = CollectionUtils.intersection(baseMap, translatedMap);
                    log.trace("There are {} points in common: {}", intersection.size(), intersection);

                    if (intersection.size() >= 12) {
                        return translatedMap;
                    }
                }
            }
        }
        return null;
    }

    /**
     * A point in 3 dimensional space.
     */
    private static class Point3D {
        int x;
        int y;
        int z;

        /**
         * Create a new point in three dimensional space.
         * 
         * @param x The x coordinate value.
         * @param y The y coordinate value.
         * @param z The z coordinate value.
         */
        public Point3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Point3D copy() {
            return new Point3D(x, y, z);
        }

        /**
         * Translate the coordinates by a certain amount.
         * 
         * @param x The distance to translate the coordinates along the x axis.
         * @param y The distance to translate the coordinates along the y axis.
         * @param z The distance to translate the coordinates along the z axis.
         */
        void translate(int x, int y, int z) {
            this.x += x;
            this.y += y;
            this.z += z;
        }

        /**
         * Rotate the coordinates (90 degrees clockwise) about the origin a certain
         * number of times about the x, y, and z axes.
         * 
         * @param x The number of times to rotate the coordinates about the x axis.
         * @param y The number of times to rotate the coordinates about the y axis.
         * @param z The number of times to rotate the coordinates about the z axis.
         */
        void rotate(int x, int y, int z) {
            int temp;
            x %= 4;
            while (x-- > 0) {
                temp = -this.y;
                this.y = this.z;
                this.z = temp;
            }
            y %= 4;
            while (y-- > 0) {
                temp = -this.z;
                this.z = this.x;
                this.x = temp;
            }
            z %= 4;
            while (z-- > 0) {
                temp = -this.x;
                this.x = this.y;
                this.y = temp;
            }
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
            Point3D other = (Point3D) obj;
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