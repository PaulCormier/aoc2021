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

        log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        log.info("{}", part2(lines));
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

    private static int part2(final List<String> lines) {

        return -1;
    }

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
            if (visited.contains(next) && next.small)
                continue;
            routes += traverseCaves(next, visited);
        }

        visited.pop();
        return routes;
    }

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