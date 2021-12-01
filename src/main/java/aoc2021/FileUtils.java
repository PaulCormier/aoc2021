package aoc2021;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Useful methods for reading files.
 * 
 * @author Paul Cormier
 *
 */
public final class FileUtils {

    /**
     * Utility classes have private constructors.
     */
    private FileUtils() {
    }

    /**
     * Read a file, available on the classpath, into a {@link List} of strings.
     * 
     * @param fileName The name of a file which can be found on the classpath.
     * @return A {@link List} of strings, one for each line in the file. Returns an
     *     empty list if there were any errors opening the file.
     */
    public static List<String> readFile(String fileName) {
        try {
            return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(fileName).toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Read a file, available on the classpath, into a {@link Stream} of strings.
     * The stream can only be read from once.
     * 
     * @param fileName The name of a file which can be found on the classpath.
     * @return A {@link Stream} of strings, one for each line in the file. Returns
     *     an empty stream if there were any errors opening the file.
     */
    public static Stream<String> readFileToStream(String fileName) {
        try {
            return Files.lines(Paths.get(ClassLoader.getSystemResource(fileName).toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    /**
     * Clean the input: Replace newlines with spaces, then split along double
     * spaces.
     * 
     * @param lines The stream of lines from the input file. The data may be
     *     separated by newlines, records are separated by two newlines.
     * @return A stream of complete records, separated internally by spaces.
     */
    public static Stream<String> cleanInput(Stream<String> lines) {
        return lines.collect(Collectors.collectingAndThen(Collectors.joining(" "),
                                                          s -> Stream.of(s.split("  "))));
    }

    /**
     * Clean the input: Replace newlines with spaces, then split along double
     * spaces.
     * 
     * @param lines The stream of lines from the input file. The data may be
     *     separated by newlines, records are separated by two newlines.
     * @return A stream of complete records, separated internally by spaces.
     */
    public static List<String> cleanInput(List<String> lines) {
        return lines.stream().collect(Collectors.collectingAndThen(Collectors.joining(" "),
                                                                   s -> Arrays.asList(s.split("  "))));
    }
}
