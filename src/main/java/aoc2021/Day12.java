package aoc2021;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/12
 * 
 * @author Paul Cormier
 *
 */
public class Day12 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day12.class);

    private static final String INPUT_TXT = "Input-Day12.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day12.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file (it has three sets of data)
        List<String> testLines = Arrays.asList(FileUtils.readFileToStream(TEST_INPUT_TXT)
                                                        .collect(Collectors.joining(" "))
                                                        .split("  "));

        log.trace(testLines.toString());
        int i = 1;
        for (String testLine : testLines) {
            log.info("There are {} paths through test example {}.", part1(Arrays.asList(testLine.split(" "))), i++);
        }

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("There are {} paths through the real data.", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        i = 1;
        for (String testLine : testLines) {
            log.info("There are {} paths through test example {} for part 2.",
                     part2(Arrays.asList(testLine.split(" "))), i++);
        }

        log.setLevel(Level.INFO);

        log.info("There are {} paths through the real data for part 2.", part2(lines));
    }

    /**
     * How many paths through this cave system are there that visit small caves at
     * most once?
     * 
     * @param lines The lines representing the connections between the caves.
     * @return The number of the paths through the caves which only visit the small
     *     caves once.
     */
    private static int part1(final List<String> lines) {

        // Map out all the caves
        Map<String, Cave> caves = new HashMap<>();
        lines.stream()
             .flatMap(l -> Stream.of(l.split("-")))
             .forEach(name -> caves.computeIfAbsent(name, Cave::new));

        // Create the links
        lines.forEach(l -> {
            String[] names = l.split("-");
            Cave cave1 = caves.get(names[0]);
            Cave cave2 = caves.get(names[1]);
            cave1.connectedCaves.add(cave2);
            cave2.connectedCaves.add(cave1);
        });

        log.debug("Caves: \n{}", caves.values());

        return traverseCaves(caves.get("start"), new Stack<>());
    }

    /**
     * How many paths through this cave system are there that visit a single small
     * caves at most twice?
     * 
     * @param lines The lines representing the connections between the caves.
     * @return The number of the paths through the caves which only visit a small
     *     caves at most twice.
     */
    private static int part2(final List<String> lines) {

        // Map out all the caves
        Map<String, Cave> caves = new HashMap<>();
        lines.stream()
             .flatMap(l -> Stream.of(l.split("-")))
             .forEach(name -> caves.computeIfAbsent(name, Cave::new));

        // Create the links
        lines.forEach(l -> {
            String[] names = l.split("-");
            Cave cave1 = caves.get(names[0]);
            Cave cave2 = caves.get(names[1]);
            // Don't link back to "start" and "end."
            if (!("start".equals(cave2.name) || "end".equals(cave1.name)))
                cave1.connectedCaves.add(cave2);
            if (!("start".equals(cave1.name) || "end".equals(cave2.name)))
                cave2.connectedCaves.add(cave1);
        });

        log.debug("Caves: \n{}", caves.values());

        return traverseCaves2(caves.get("start"), new Stack<>());
    }

    /**
     * Traverse the caves, visiting small caves at most once.
     * 
     * @param start The cave to start in.
     * @param visited The path taken to get to the current cave.
     * @return The number of routes to the end from the current cave.
     */
    private static int traverseCaves(Cave start, Stack<Cave> visited) {
        log.trace(start.name);

        if ("end".equals(start.name)) {
            log.debug("{},end", visited.stream().map(c -> c.name).collect(Collectors.joining(",")));
            return 1;
        }

        visited.push(start);

        int routes = 0;
        // Visit all connected caves
        for (Cave next : start.connectedCaves) {
            if (next.small && visited.contains(next))
                continue;
            routes += traverseCaves(next, visited);
        }

        visited.pop();
        return routes;
    }

    /**
     * Traverse the caves, visiting a single small cave at most twice.
     * 
     * @param start The cave to start in.
     * @param visited The path taken to get to the current cave.
     * @return The number of routes to the end from the current cave.
     */
    private static int traverseCaves2(Cave start, Stack<Cave> visited) {
        log.trace(start.name);

        if ("end".equals(start.name)) {
            log.debug("{},end", visited.stream().map(c -> c.name).collect(Collectors.joining(",")));
            return 1;
        }

        visited.push(start);

        int routes = 0;
        // Visit all connected caves
        for (Cave next : start.connectedCaves) {
            // If this is a small cave which we've already visited, and there are already
            // two identical small caves in the list of visited caves; skip it
            if (next.small && visited.contains(next)
                && visited.stream()
                          .filter(c -> c.small)
                          .collect(Collectors.groupingBy(c -> c.name, Collectors.counting()))
                          .values()
                          .contains(2L))
                continue;
            
            routes += traverseCaves2(next, visited);
        }

        visited.pop();
        return routes;
    }

    /**
     * Representation of a cave connected to other caves.
     */
    private static class Cave {
        String name;
        boolean small;

        Set<Cave> connectedCaves;

        Cave(String name) {
            this.name = name;
            this.small = StringUtils.isAllLowerCase(name);
            this.connectedCaves = new HashSet<>();
        }

        @Override
        public String toString() {
            return name + " [" + connectedCaves.stream().map(c -> c.name).collect(Collectors.joining(", ")) + "]";
        }
    }

}