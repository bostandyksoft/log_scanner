package ru.avmakarov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avmakarov.parser.LogLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LogScanner {
    private final Logger logger = LoggerFactory.getLogger(LogScanner.class);

    private final LogLineParser lineParser;

    public LogScanner(LogLineParser lineParser) {
        this.lineParser = lineParser;
    }

    /**
     * Чтение входящего потока с настройками
     *
     * @param inputStream  Входящий поток
     * @param availability Уровень доступности в %
     * @param requestThreshold    Максимально допустимое время ответа
     * @throws IOException при ошибки обработки входящих данных
     */
    public List<ReportEntry> read(InputStream inputStream, double availability, double requestThreshold) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            ScannerContext context = new ScannerContext(availability, requestThreshold);
            String line;
            while ((line = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(line.trim())) {
                    break;
                }
                LogLineParser.LineInfo info = lineParser.parse(line);
                if (info == null) {
                    logger.warn("Ошибка разбора строки {}",line);
                    continue;
                }
                context.next(info);
            }
            context.flushTimestamp(true);
            return context.getReport();
        }
    }

}
