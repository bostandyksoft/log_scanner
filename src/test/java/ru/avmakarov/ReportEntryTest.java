package ru.avmakarov;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ReportEntryTest {

    public static Stream<Arguments> getAvailability_source() {
        return Stream.of(
                Arguments.of(100.0, 500, 0),
                Arguments.of(0.0, 500, 500),
                Arguments.of(50.0, 100, 50),
                Arguments.of(100.0, 0, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("getAvailability_source")
    void getAvailability(double expected, int totalCount, int failureCount) {
        assertEquals(expected, ReportEntry.getAvailability(totalCount, failureCount));
    }

}