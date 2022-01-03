package aoc2021;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
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

        // log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        log.trace(testLines.toString());

        ALU testALU = new ALU(testLines);
        IntStream inputs = IntStream.range(0, 16);
        inputs.forEach(i -> {
            testALU.reset();
            testALU.input.add(i);
            testALU.run();
            log.debug("Test result for input {}: {}", i, testALU);
        });

        log.setLevel(Level.INFO);

        // Read the real file, filter blank lines and comments
        List<String> lines = FileUtils.readFileToStream(INPUT_TXT)
                                      .map(l -> l.replaceAll("#.*$", "").trim())
                                      .filter(StringUtils::isNotBlank)
                                      .collect(Collectors.toList());

        log.info("The largest valid serial number is: {}", part1(lines));

        // PART 2

        log.setLevel(Level.DEBUG);

        // log.info("{}", part2(testLines));

        // log.setLevel(Level.INFO);

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
         ALU2 part2Alu = new ALU2();
        
         return part2Alu.run();


    }

    private static long part2_old(final List<String> lines) {

        // Going to need to narrow down the digits of the serial number, maybe?

        // How about splitting the ALU up into 14 pieces?
        List<ALU> alus = IntStream.range(0, 14)
                                  .mapToObj(i -> lines.subList(i * 18, i * 18 + 18))
                                  .peek(l -> log.trace(l.toString()))
                                  .map(ALU::new)
                                  .collect(Collectors.toList());

        // Work out the I/O combinations for w and z
        ALU alu1 = alus.get(0);
        List<Integer> alu1Results = IntStream.rangeClosed(1, 9)
                                             .map(w -> {
                                                 alu1.reset();
                                                 alu1.input.add(w);
                                                 alu1.run();
                                                 return alu1.z.intValue();
                                             })
                                             .boxed()
                                             .collect(Collectors.toList());
        log.debug("ALU1 results: {}", alu1Results); // z1 is between 2 and 10 (w+1) {Technically z*26+w+1, but z=0)

        ALU alu2 = alus.get(1);
        List<List<Integer>> alu2Results = IntStream.rangeClosed(2, 10)
                                                   .mapToObj(z -> IntStream.rangeClosed(1, 9)
                                                                           .map(w -> {
                                                                               alu2.reset();
                                                                               alu2.z.set(z);
                                                                               alu2.input.add(w);
                                                                               alu2.run();
                                                                               return alu2.z.intValue();
                                                                           })
                                                                           .boxed()
                                                                           .collect(Collectors.toList()))
                                                   .collect(Collectors.toList());
        log.debug("ALU2 results: {}", alu2Results); // z2 is z1*26+w+9

        int aluNum = 7;
        ALU alu = alus.get(aluNum - 1);

        int zMin = 94;
        int zMax = zMin + 8;

        Map<Integer, List<Integer>> aluResults = new TreeMap<>();
        for (int z : IntStream.rangeClosed(zMin, zMax).toArray()) {
            aluResults.put(z, IntStream.rangeClosed(1, 9)
                                       .map(w -> {
                                           alu.reset();
                                           alu.z.set(z);
                                           alu.input.add(w);
                                           alu.run();
                                           return alu.z.intValue();
                                       })
                                       .boxed()
                                       .collect(Collectors.toList()));
        }
        log.debug("ALU{} results: {}", aluNum, aluResults);
        // z3 = z2*26 + w + 12 (62-70 to 270-278)
        // z4 = z3*26 + w + 6 (1625-1633 to 7241-7249) - Noticing a pattern?
        // z5 = z3*26 + w + 3 or z3+z3/26-2 if w = z4%26-6 (try 1622-1630, 7238-7246,
        // and 62,89,116,143) - No, but this one is interesting for w = 7-9.
        // z6 = Hmm.... With the outliers, z6 = z5(0) + 6 - ?; The others are just z5(0)
        // + w + 6 (try 7244-7252 and 1628-1636, and 94-102, 68-76)
        // z7 =

        // Build a full ALU with the given instructions
        ALU fullAlu = new ALU(lines);

        // Test the number
        long serialNumber = 11111111111111L;
        // do {
        log.debug("Input: {}", serialNumber);
        fullAlu.reset();
        // Load the ALU with an input
        Long.toString(serialNumber).chars().map(Character::getNumericValue).forEach(fullAlu.input::add);
        // if (alu.input.contains(0))
        // continue;

        // Run the ALU
        fullAlu.run();

        log.debug("ALU result: {}", fullAlu);
        // If the serial number is valid
        if (fullAlu.z.get() == 0)
            return serialNumber;

        // } while (serialNumber++ <= 99999999999999L);

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

    /**
     * Hardcode the instructions for each stage of the ALU. Set it to work forward
     * to find the lowest valid serial number.
     */
    private static final class ALU2 {
        final long START = 45_199_191_516_111L;

        List<Integer> startingDigits = Long.toString(START)
                                           .chars()
                                           .map(Character::getNumericValue)
                                           .boxed()
                                           .collect(Collectors.toList());

        long count = 0;

        long run() {
            log.debug("Starting at: {}", START);
            return firstDigit(0);
        }

        private long firstDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // div z 1
                z /= 1;
                // add x 10
                x += 10;
                // x = z % 26 + 10;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 + 10 == w ? 0 : 1; // z=0 for the first digit
                // x = 10 == w ? 0 : 1; // w < 10
                // x=1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y; // z=0 for the first digit

                // mul y 0
                y = 0;
                // add y w
                // add y 1
                y = w + 1;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z = w + 1;

                // Call the next stage
                result = secondDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000_000_000_000L;
            }

            return result;
        }

        private long secondDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x=z%26;
                // div z 1
                z /= 1;
                // add x 11
                x += 11;
                // x = z % 26 + 11;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 + 11 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                // y *= x; // x=1
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 26;

                // mul y 0
                y = 0;
                // add y w
                // add y 9
                y = w + 9;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += w + 9;

                // z = z * 26 + w + 9;

                // Call the next stage
                result = thirdDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000_000_000_000L;
            }

            return result;
        }

        private long thirdDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // div z 1
                z /= 1;
                // add x 14
                x += 14;
                // x = z % 26 + 14;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 + 14 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                // y *= x; // x=1
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 26;

                // mul y 0
                y = 0;
                // add y w
                // add y 12
                y = w + 12;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += w + 12;

                // z = z * 26 + w + 12;

                // Call the next stage
                result = fourthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100_000_000_000L;
            }

            return result;
        }

        private long fourthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                log.debug("Checked: {}", count);
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // div z 1
                z /= 1;
                // add x 13
                x += 13;
                // x = z % 26 + 13;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 + 13 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x; // x=1
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 26;

                // mul y 0
                y = 0;
                // add y w
                // add y 6
                y = w + 6;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += w + 6;

                // z = z * 26 + w + 6;

                // Call the next stage
                result = fifthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000_000_000L;
            }

            return result;
        }

        private long fifthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // div z 1
                z /= 1;
                // add x -6
                x += -6;
                // x = z % 26 - 6;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 - 6 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 9
                y = w + 9;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 9) * x;

                // Call the next stage
                result = sixthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000_000_000L;
            }

            return result;
        }

        private long sixthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 26
                z /= 26;
                // add x -14
                x += -14;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x - 14 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 15
                y = w + 15;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 15) * x;

                // Call the next stage
                result = seventhDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100_000_000L;
            }

            return result;
        }

        private long seventhDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 1
                z /= 1;
                // add x 14
                x += 14;
                // x = z % 26 + 14;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x + 14 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 7
                y = w + 7;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 7) * x;

                // Call the next stage
                result = eighthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000_000L;
            }

            return result;
        }

        private long eighthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // div z 1
                z /= 1;
                // add x 13
                x += 13;
                // x = z % 26 + 13;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 + 13 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x; // x=1
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 26;

                // mul y 0
                y = 0;
                // add y w
                // add y 12
                y = w + 12;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += w + 12;

                // z = z * 26 + w + 12;

                // Call the next stage
                result = ninthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000_000L;
            }

            return result;
        }

        private long ninthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 26
                z /= 26;
                // add x -8
                x += -8;
                // x = z % 26 - 8;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x - 8 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 15
                y = w + 15;
                // mul y x
                y *= x;
                // add z y
                z += y;
//                z += (w + 15) * x;

                // Call the next stage
                result = tenthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100_000L;
            }

            return result;
        }

        private long tenthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 26
                z /= 26;
                // add x -15
                x += -15;
                // x = z % 26 - 15;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x - 15 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 3
                y = w + 3;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 3) * x;

                // Call the next stage
                result = eleventhDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000L;
            }

            return result;
        }

        private long eleventhDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // div z 1
                z /= 1;
                // add x 10
                x += 10;
                // x = z % 26 + 10;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = z % 26 + 10 == w ? 0 : 1; // z=0 for the first digit
                // x = 10 == w ? 0 : 1; // w < 10
                // x=1;
                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;

                // mul y 0
                y = 0;
                // add y w
                // add y 6
                y = w + 6;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z = z * 26 + w + 6;

                // Call the next stage
                result = twelfthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000L;
            }

            return result;
        }

        private long twelfthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 26
                z /= 26;
                // add x -11
                x += -11;
                // x = z % 26 - 11;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x - 11 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 2
                y = w + 2;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 2) * x;

                // Call the next stage
                result = thirteenthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100L;
            }

            return result;
        }

        private long thirteenthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 26
                z /= 26;
                // add x -13
                x += -13;
                // x = z % 26 - 13;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x - 13 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 10
                y = w + 10;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 10) * x;

                // Call the next stage
                result = fourteenthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10L;
            }

            return result;
        }

        private long fourteenthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            long start = startingDigits.size() > 0 ? startingDigits.remove(0) : 1;
            for (long w = start; w < 10; w++) {
                count++;
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                x = 0;
                // add x z
                x = z;
                // mod x 26
                x %= 26;
                // x = z % 26;
                // div z 26
                z /= 26;
                // add x -4
                x += -4;
                // x = z % 26 - 4;
                // eql x w
                x = x == w ? 1 : 0;
                // eql x 0
                x = x == 0 ? 1 : 0;

                // x = x - 4 == w ? 0 : 1;

                // mul y 0
                y = 0;
                // add y 25
                y = 25;
                // mul y x
                y *= x;
                // add y 1
                y += 1;
                // mul z y
                z *= y;
                // z *= 25 * x + 1;

                // mul y 0
                y = 0;
                // add y w
                // add y 12
                y = w + 12;
                // mul y x
                y *= x;
                // add z y
                z += y;
                // z += (w + 12) * x;

                // Last stage
                // If z is 0, return the digit.
                if (z == 0) {
                    log.info("Found it!");
                    return w;
                }
            }

            return result;
        }
    }

    /**
     * Hardcode the instructions for each stage of the ALU. Set it to work forward
     * to find the lowest valid serial number.
     * 
     * XXX This is probably wrong...
     */
    private static final class ALU2Optimized {
        long total = 11_111_111_111_111L;

        long run() {
            return firstDigit(0);
        }

        private long firstDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                // z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // div z 1
                // z /= 1;
                // add x 10
                // x += 10;
                // x = z % 26 + 10;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                // x = z % 26 + 10 == w ? 0 : 1; // z=0 for the first digit
                // x = 10 == w ? 0 : 1; // w < 10
                // x=1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y; // z=0 for the first digit

                // mul y 0
                // y=0;
                // add y w
                // add y 1
                // y = w + 1;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z = w + 1;

                // Call the next stage
                result = secondDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000_000_000_000L;
            }

            return result;
        }

        private long secondDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // x=z%26;
                // div z 1
                // z /= 1;
                // add x 11
                // x += 11;
                // x = z % 26 + 11;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                // x = z % 26 + 11 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x; // x=1
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                // z *= 26;

                // mul y 0
                // y=0;
                // add y w
                // add y 9
                // y = w + 9;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                // z += w + 9;

                z = z * 26 + w + 9;

                // Call the next stage
                result = thirdDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000_000_000_000L;
            }

            return result;
        }

        private long thirdDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // div z 1
                // z /= 1;
                // add x 14
                // x += 14;
                // x = z % 26 + 14;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                // x = z % 26 + 14 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x; // x=1
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                // z *= 26;

                // mul y 0
                // y=0;
                // add y w
                // add y 12
                // y = w + 12;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                // z += w + 12;

                z = z * 26 + w + 12;

                // Call the next stage
                result = fourthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100_000_000_000L;
            }

            return result;
        }

        private long fourthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                log.debug("Trying: {}", total);
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // div z 1
                // z /= 1;
                // add x 13
                // x += 13;
                // x = z % 26 + 13;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                // x = z % 26 + 13 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x; // x=1
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                // z *= 26;

                // mul y 0
                // y=0;
                // add y w
                // add y 6
                // y = w + 6;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                // z += w + 6;

                z = z * 26 + w + 6;

                // Call the next stage
                result = fifthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000_000_000L;
            }

            return result;
        }

        private long fifthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // div z 1
                // z /= 1;
                // add x -6
                // x += -6;
                // x = z % 26 - 6;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = z % 26 - 6 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 9
                // y = w + 9;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 9) * x;

                // Call the next stage
                result = sixthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000_000_000L;
            }

            return result;
        }

        private long sixthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x -14
                // x += -14;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x - 14 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 15
                // y = w + 15;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 15) * x;

                // Call the next stage
                result = seventhDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100_000_000L;
            }

            return result;
        }

        private long seventhDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x 14
                // x += 14;
                // x = z % 26 + 14;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x + 14 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 7
                // y = w + 7;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 7) * x;

                // Call the next stage
                result = eighthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000_000L;
            }

            return result;
        }

        private long eighthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // div z 1
                // z /= 1;
                // add x 13
                // x += 13;
                // x = z % 26 + 13;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                // x = z % 26 + 13 == w ? 0 : 1;
                // x = 1; // z>0 and w<9

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x; // x=1
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                // z *= 26;

                // mul y 0
                // y=0;
                // add y w
                // add y 12
                // y = w + 12;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                // z += w + 12;

                z = z * 26 + w + 12;

                // Call the next stage
                result = ninthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000_000L;
            }

            return result;
        }

        private long ninthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x -8
                // x += -8;
                // x = z % 26 - 8;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x - 8 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 15
                // y = w + 15;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 15) * x;

                // Call the next stage
                result = tenthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100_000L;
            }

            return result;
        }

        private long tenthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x -15
                // x += -15;
                // x = z % 26 - 15;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x - 15 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 3
                // y = w + 3;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 3) * x;

                // Call the next stage
                result = eleventhDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10_000L;
            }

            return result;
        }

        private long eleventhDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                // div z 1
                // z /= 1;
                // add x 10
                // x += 10;
                // x = z % 26 + 10;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                // x = z % 26 + 10 == w ? 0 : 1; // z=0 for the first digit
                // x = 10 == w ? 0 : 1; // w < 10
                // x=1;
                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;

                // mul y 0
                // y=0;
                // add y w
                // add y 6
                // y = w + 6;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z = z * 26 + w + 6;

                // Call the next stage
                result = twelfthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 1_000L;
            }

            return result;
        }

        private long twelfthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x -11
                // x += -11;
                // x = z % 26 - 11;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x - 11 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 2
                // y = w + 2;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 2) * x;

                // Call the next stage
                result = thirteenthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 100L;
            }

            return result;
        }

        private long thirteenthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x -13
                // x += -13;
                // x = z % 26 - 13;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x - 13 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 10
                // y = w + 10;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 10) * x;

                // Call the next stage
                result = fourteenthDigit(z);

                // If the result is > 0, return the digit.
                if (result > 0)
                    return result + w * 10L;
            }

            return result;
        }

        private long fourteenthDigit(final long initialZ) {
            long result = -1;
            long x;
            long y;
            long z;

            // Try each digit
            for (long w = 1; w < 10; w++) {
                total++;
                z = initialZ;
                // Run the code
                // inp w
                // mul x 0
                // x=0;
                // add x z
                // x=z;
                // mod x 26
                // x %= 26;
                x = z % 26;
                // div z 26
                z /= 26;
                // add x -4
                // x += -4;
                // x = z % 26 - 4;
                // eql x w
                // x = x == w ? 1 : 0;
                // eql x 0
                // x = x == 0 ? 1 : 0;

                x = x - 4 == w ? 0 : 1;

                // mul y 0
                // y=0;
                // add y 25
                // y = 25;
                // mul y x
                // y *= x;
                // add y 1
                // y += 1;
                // mul z y
                // z *= y;
                z *= 25 * x + 1;

                // mul y 0
                // y=0;
                // add y w
                // add y 12
                // y = w + 12;
                // mul y x
                // y *= x;
                // add z y
                // z += y;
                z += (w + 12) * x;

                // Last stage
                // If z is 0, return the digit.
                if (z == 0) {
                    log.info("Found it!");
                    return w;
                }
            }

            total++;
            return result;
        }
    }

}