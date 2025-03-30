package ru.avmakarov.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleLogLineParserTest {

    SimpleLogLineParser parser = new SimpleLogLineParser();

    public static Stream<Arguments> parse_source() {
        return Stream.of(
                Arguments.of(
                        "192.168.32.181 - - [14/06/2017:16:47:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 44.510983 \"-\" \"@list-item-updater\" prio:0",
                        new LogLineParser.LineInfo(
                                "[14/06/2017:16:47:02 +1000]",
                                200,
                                44.510983
                        )
                ),
                Arguments.of(
                        "192.168.32.181 - - [14/06/2017:16:47:31 +1000] \"P",
                        null
                )
        );
    }

    public static Stream<Arguments> getStatus_success_source() {
        return Stream.of(
                Arguments.of("200", 200),
                Arguments.of("500", 500)
        );
    }

    public static Stream<Arguments> getResponseTime_success_source() {
        return Stream.of(
                Arguments.of(
                        "44.510983", 44.510983
                ),
                Arguments.of(
                        "27.219334", 27.219334
                )
        );
    }

    @ParameterizedTest
    @MethodSource("parse_source")
    void parse(String input, LogLineParser.LineInfo expected) {
        LogLineParser.LineInfo info = parser.parse(input);
        assertEquals(expected, info);
        if (expected != null) {
            assertEquals(expected.timestamp(), info.timestamp(), "Timestamp should be the same");
            assertEquals(expected.status(), info.status(), "Status should be the same");
            assertEquals(expected.responseTime(), info.responseTime(), "Response time should be the same");
        }
    }

    @ParameterizedTest
    @MethodSource("getStatus_success_source")
    void getStatus_success(String input, int expectedStatus) {
        assertEquals(expectedStatus, parser.getStatus(input), "Status should be the same");
    }

    @Test
    void getStatus_fail() {
        assertThrows(IllegalArgumentException.class, () -> parser.getStatus("\"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\""));
    }

    @ParameterizedTest
    @MethodSource("getResponseTime_success_source")
    void getResponseTime_success(String input, double responseTime) {
        assertEquals(responseTime, parser.getResponseTime(input), "ResponseTime should be the same");
    }

    @Test
    void getResponseTime_fail() {
        assertThrows(IllegalArgumentException.class, () -> parser.getResponseTime("\"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\""));
    }
}