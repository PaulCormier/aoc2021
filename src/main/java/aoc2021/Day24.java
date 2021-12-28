package aoc2021;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/24
 * 
 * @author Paul Cormier
 *
 */
public class Day24 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day24.class);

    private static final String INPUT_TXT = "Input-Day24.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day24.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        log.trace(testLines.toString());

        ALU testALU = new ALU(testLines);
        IntStream inputs = IntStream.range(0, 16);
        inputs.forEach(i -> {
            testALU.reset();
            testALU.input.add(i);
            testALU.run();
            log.info("Test result for input {}: {}", i, testALU);
        });

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("The largest valid serial number is: {}", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        // log.info("{}", part2(testLines));

        log.setLevel(Level.INFO);

        log.info("The smalest valid serial number is: {}", part2(lines));
    }

    private static long part1(final List<String> lines) {

        // Build an ALU with the given instructions
        ALU alu = new ALU(lines);

        long serialNumber = 99999795919456L;// 99999999999999L;
        do {
            log.debug("Input: {}", serialNumber);
            alu.reset();
            // Load the ALU with an input
            Long.toString(serialNumber).chars().map(Character::getNumericValue).forEach(alu.input::add);
            if (alu.input.contains(0))
                continue;

            // Run the ALU
            alu.run();

            log.debug("ALU result: {}", alu);
            // If the serial number is valid
            if (alu.z.get() == 0)
                return serialNumber;

        } while (serialNumber-- > 0);
        return -1;

        // Largest: 99999795919456
    }

    private static long part2(final List<String> lines) {

        // Build an ALU with the given instructions
        ALU alu = new ALU(lines);

        long serialNumber = 11111111111111L;
        do {
            log.debug("Input: {}", serialNumber);
            alu.reset();
            // Load the ALU with an input
            Long.toString(serialNumber).chars().map(Character::getNumericValue).forEach(alu.input::add);
            if (alu.input.contains(0))
                continue;

            // Run the ALU
            alu.run();

            log.debug("ALU result: {}", alu);
            // If the serial number is valid
            if (alu.z.get() == 0)
                return serialNumber;

        } while (serialNumber++ <= 99999999999999L);

        return serialNumber;
    }

    private static final class ALU {
        final Queue<Integer> input = new LinkedList<>();

        final AtomicInteger w = new AtomicInteger(0);
        final AtomicInteger x = new AtomicInteger(0);
        final AtomicInteger y = new AtomicInteger(0);
        final AtomicInteger z = new AtomicInteger(0);

        final Map<String, AtomicInteger> registerMap = Map.of("w", w, "x", x, "y", y, "z", z);

        final List<Instruction> compiledInstructions = new ArrayList<>();

        ALU(List<String> instructions) {

            for (String line : instructions) {
                Operation operation = Operation.valueOf(line.substring(0, 3)
                                                            .toUpperCase());
                AtomicInteger register = registerMap.get(line.substring(4, 5));
                IntSupplier value;
                String valueString = line.length() > 5 ? line.substring(6) : "";
                if (line.startsWith("inp"))
                    value = input::poll;
                else if (NumberUtils.isParsable(valueString))
                    value = () -> Integer.parseInt(valueString);
                else
                    value = () -> registerMap.get(valueString).get();

                compiledInstructions.add(new Instruction(operation, register, value));

            }
        }

        void reset() {
            w.set(0);
            x.set(0);
            y.set(0);
            z.set(0);
            input.clear();
        }

        void run() {
            compiledInstructions.forEach(Instruction::execute);
        }

        @Override
        public String toString() {
            return String.format("w=%s x=%s y=%s z=%s", w, x, y, z);
        }

    }

    static class Instruction {
        Operation operation;
        AtomicInteger register;
        IntSupplier value;

        public Instruction(Operation operation, AtomicInteger register, IntSupplier value) {
            this.operation = operation;
            this.register = register;
            this.value = value;
        }

        public void execute() {
            this.operation.execution.accept(register, value);
        }
    }

    static enum Operation {
        INP((a, b) -> a.set(b.getAsInt())),
        ADD((a, b) -> a.addAndGet(b.getAsInt())),
        MUL((a, b) -> a.set(a.get() * b.getAsInt())),
        DIV((a, b) -> a.set(Math.floorDiv(a.get(), b.getAsInt()))),
        MOD((a, b) -> a.set(a.get() % b.getAsInt())),
        EQL((a, b) -> a.set(a.get() == b.getAsInt() ? 1 : 0));

        BiConsumer<AtomicInteger, IntSupplier> execution;

        Operation(BiConsumer<AtomicInteger, IntSupplier> operation) {
            this.execution = operation;
        }
    }

}