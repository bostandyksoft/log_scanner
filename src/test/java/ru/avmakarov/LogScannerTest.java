package ru.avmakarov;

import org.junit.jupiter.api.Test;
import ru.avmakarov.parser.LogLineParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogScannerTest {

    private static final Map<String, LogLineParser.LineInfo> testData = Map.of(
            //#2 - 50%
            "1", new LogLineParser.LineInfo("2", 200, 20.0),
            "2", new LogLineParser.LineInfo("2", 500, 20.0),
            //#3 - 100%
            "3", new LogLineParser.LineInfo("3", 200, 20.0),
            "4", new LogLineParser.LineInfo("3", 200, 20.0),
            //#4 - 66%
            "5", new LogLineParser.LineInfo("4", 500, 20.0),
            "6", new LogLineParser.LineInfo("4", 200, 20.0),
            "7", new LogLineParser.LineInfo("4", 200, 20.0)
    );

    private static final String testKeys = IntStream.range(1, testData.size() + 1).mapToObj(Integer::toString).collect(Collectors.joining("\n"));

    private static class MapBasedParser implements LogLineParser {

        @Override
        public LineInfo parse(String line) {
            return testData.get(line);
        }
    }

    private LogScanner scanner = new LogScanner(new MapBasedParser());

    @Test
    void empty_stream() throws IOException {
        try (InputStream stream = new ByteArrayInputStream("".getBytes())) {
            assertTrue(
                    scanner.read(stream, 99.9, 45.0).isEmpty()
            );
        }
    }

    @Test
    void test_80_percent() throws IOException {
        try (InputStream stream = new ByteArrayInputStream(testKeys.getBytes())) {
            assertIterableEquals(
                    List.of(
                            new ReportEntry("2", "4", 7, 2)
                    ),
                    scanner.read(stream, 80, 45.0)
            );
        }
    }

    @Test
    void test_70_percent() throws IOException {
        try (InputStream stream = new ByteArrayInputStream(testKeys.getBytes())) {
            assertIterableEquals(
                    List.of(
                            new ReportEntry("2", "2", 2, 1),
                            new ReportEntry("4", "4", 3, 1)
                    ),
                    scanner.read(stream, 70, 45.0)
            );
        }
    }

    @Test
    void test_60_percent() throws IOException {
        try (InputStream stream = new ByteArrayInputStream(testKeys.getBytes())) {
            assertIterableEquals(
                    List.of(
                            new ReportEntry("2", "2", 2, 1)
                    ),
                    scanner.read(stream, 60, 45.0)
            );
        }
    }

}