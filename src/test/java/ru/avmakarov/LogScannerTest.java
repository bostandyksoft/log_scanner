package ru.avmakarov;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.avmakarov.parser.SimpleLogLineParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogScannerTest {

	private final LogScanner scanner = new LogScanner(new SimpleLogLineParser());

	public static Stream<Arguments> access_log_test_source() {
		return Stream.of(
				Arguments.of(99.9, 45.0,
						List.of(
								new ReportEntry(
										"[14/06/2017:16:47:02 +1000]",
										"[14/06/2017:16:47:27 +1000]",
										1487,
										373
								),
								new ReportEntry(
										"[14/06/2017:16:47:29 +1000]",
										"[14/06/2017:16:47:36 +1000]",
										499,
										47
								),
								new ReportEntry(
										"[14/06/2017:16:47:39 +1000]",
										"[14/06/2017:16:48:01 +1000]",
										1378,
										194
								),
								new ReportEntry(
										"[14/06/2017:16:48:03 +1000]",
										"[14/06/2017:16:48:16 +1000]",
										884,
										118
								),
								new ReportEntry(
										"[14/06/2017:16:48:18 +1000]",
										"[14/06/2017:16:48:18 +1000]",
										92,
										17
								),
								new ReportEntry(
										"[14/06/2017:16:48:20 +1000]",
										"[14/06/2017:16:48:22 +1000]",
										186,
										17
								),
								new ReportEntry(
										"[14/06/2017:16:48:24 +1000]",
										"[14/06/2017:16:48:29 +1000]",
										412,
										45
								),
								new ReportEntry(
										"[14/06/2017:16:48:33 +1000]",
										"[14/06/2017:16:48:39 +1000]",
										461,
										33
								),
								new ReportEntry(
										"[14/06/2017:16:48:41 +1000]",
										"[14/06/2017:16:48:48 +1000]",
										585,
										74
								),
								new ReportEntry(
										"[14/06/2017:16:48:50 +1000]",
										"[14/06/2017:16:48:52 +1000]",
										188,
										15
								)
						)
				)
		);
	}

	@ParameterizedTest
	@MethodSource("access_log_test_source")
	void access_log_test(double availability, double requestThreshold, List<ReportEntry> expected) throws IOException {
		try (InputStream stream = this.getClass().getResourceAsStream("access.log")) {
			List<ReportEntry> actual = scanner.read(stream, availability, requestThreshold);

			assertEquals(expected, actual,
					"Проверка " + availability + " / " + requestThreshold
			);
		}
	}

}
