package aoc2021;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2021/day/16
 * 
 * @author Paul Cormier
 *
 */
public class Day16 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day16.class);

    private static final String INPUT_TXT = "Input-Day16.txt";

    private static final String TEST_INPUT_TXT = "TestInput-Day16.txt";

    public static void main(String[] args) {

        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        log.trace(testLines.toString());

        for (String line : testLines)
            log.info("The version sum of {} is: {}", line, part1(line));

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("The version sum of the real data is: {}", part1(lines.get(0)));

        // PART 2

        log.setLevel(Level.DEBUG);

        for (String line : testLines)
            log.info("The value of {} is: {}", line, part2(line));

        log.setLevel(Level.INFO);

        log.info("The value of the real data packet is: {}", part2(lines.get(0)));
    }

    /**
     * Parse the input line into packets and return the sum of the version
     * numbers.
     * 
     * @param inputLine
     *            The hexadecimal encoded command line.
     * @return The sum of the version numbers in the command line.
     */
    private static int part1(final String inputLine) {

        // Convert to binary
        String bits = inputLine.chars()
                               .mapToObj(ch -> Integer.toBinaryString(Character.getNumericValue(ch)))
                               .map(s -> StringUtils.leftPad(s, 4, '0'))
                               .collect(Collectors.joining());
        log.debug("{} => {}", inputLine, bits);

        Packet packet = new Packet(bits);
        log.debug(packet.toString());

        // Sum the version numbers
        Queue<Packet> packetsToCheck = new LinkedList<>();
        packetsToCheck.add(packet);
        int versionSum = 0;
        while (!packetsToCheck.isEmpty()) {
            Packet currentPacket = packetsToCheck.poll();
            versionSum += currentPacket.version;
            packetsToCheck.addAll(currentPacket.subPackets);
        }

        return versionSum;
    }

    private static long part2(final String inputLine) {

        // Convert to binary
        String bits = inputLine.chars()
                               .mapToObj(ch -> Integer.toBinaryString(Character.getNumericValue(ch)))
                               .map(s -> StringUtils.leftPad(s, 4, '0'))
                               .collect(Collectors.joining());

        Packet packet = new Packet(bits);

        return packet.getValue();
    }

    private static class Packet {
        int version;
        int type;

        long value;

        List<Packet> subPackets = new ArrayList<>();

        // The number of bits in the binary representation of this packet. 
        int bitCount = 6;

        Packet(String bits) {
            this.version = Integer.parseInt(bits.substring(0, 3), 2);
            this.type = Integer.parseInt(bits.substring(3, 6), 2);

            // Handle data packets
            if (this.type == 4) {
                List<String> data = Pattern.compile(".{5}")
                                           .matcher(bits.substring(6))
                                           .results()
                                           .map(m -> m.group())
                                           .collect(Collectors.toList());

                StringBuilder valueString = new StringBuilder();
                for (String nibble : data) {
                    this.bitCount += 5;

                    valueString.append(nibble.substring(1));

                    if (nibble.charAt(0) == '0')
                        break;
                }
                this.value = Long.parseLong(valueString.toString(), 2);
            }

            // Operators
            else {
                bitCount++;
                if (bits.charAt(6) == '0') {
                    int subPacketBits = Integer.parseInt(bits.substring(bitCount, bitCount + 15), 2);
                    this.bitCount += 15;
                    // Parse them into packets
                    int offset = this.bitCount;
                    this.bitCount += subPacketBits;
                    while (subPacketBits > 0) {
                        Packet subPacket = new Packet(bits.substring(offset, offset + subPacketBits));
                        this.subPackets.add(subPacket);
                        subPacketBits -= subPacket.bitCount;
                        offset += subPacket.bitCount;
                    }
                } else {
                    int subPackets = Integer.parseInt(bits.substring(bitCount, bitCount + 11), 2);
                    this.bitCount += 11;
                    // Parse them into packets
                    int offset = this.bitCount;
                    while (subPackets > 0) {
                        Packet subPacket = new Packet(bits.substring(offset));
                        this.subPackets.add(subPacket);
                        subPackets--;
                        offset += subPacket.bitCount;
                        this.bitCount += subPacket.bitCount;
                    }

                }
            }

        }

        long getValue() {

            LongStream subPacketValues = this.subPackets.stream().mapToLong(Packet::getValue);

            switch (this.type) {
                case 0:
                    // Sum
                    this.value = subPacketValues.sum();
                    break;
                case 1:
                    // Product
                    this.value = subPacketValues.reduce(Math::multiplyExact).getAsLong();
                    break;
                case 2:
                    // Min
                    this.value = subPacketValues.min().getAsLong();
                    break;
                case 3:
                    // Max
                    this.value = subPacketValues.max().getAsLong();
                    break;
                case 4:
                    // Already computed
                    break;
                case 5:
                    // Greater than
                    this.value = subPacketValues.reduce(Math::subtractExact).getAsLong() > 0 ? 1 : 0;
                    break;
                case 6:
                    // Less than
                    this.value = subPacketValues.reduce(Math::subtractExact).getAsLong() < 0 ? 1 : 0;
                    break;
                case 7:
                    // Equal to
                    this.value = subPacketValues.reduce(Math::subtractExact).getAsLong() == 0 ? 1 : 0;
                    break;
            }

            return this.value;
        }

        @Override
        public String toString() {
            return String.format("Packet [version=%s, type=%s, value=%s, subPackets=%s]", version, type, value, subPackets);
        }

    }

}