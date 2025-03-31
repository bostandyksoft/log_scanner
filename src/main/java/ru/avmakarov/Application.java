package ru.avmakarov;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avmakarov.parser.SimpleLogLineParser;

import java.io.*;
import java.util.List;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Options options = new Options();
        options.addRequiredOption("u", "user", true, "Уровень доступности (%)");
        options.addRequiredOption("t", "time", true, "Максимально допустимое время ответа (мс)");
        options.addOption("f", "file", true, "Файл логов");

        CommandLineParser parser = new DefaultParser();
        double availability, requestThreshold;
        File file = null;
        try {
            CommandLine cmd = parser.parse(options, args);

            availability = Double.parseDouble(cmd.getOptionValue("u"));
            requestThreshold = Double.parseDouble(cmd.getOptionValue("t"));

            String filePath = cmd.getOptionValue("f");
            if (!StringUtils.isEmpty(filePath)) {
                file = new File(filePath);
                if (!file.exists()) {
                    logger.error("Файл логов \"{}\" не найден", filePath);
                    System.exit(1);
                    return;
                }
            }

        } catch (ParseException e) {
            logger.error("Ошибка разбора параметров: {}", e.getMessage());
            System.exit(1);
            return;
        }

        try (InputStream stream = selectStream(file)) {
            run(stream, availability, requestThreshold);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
    }

    static void run(InputStream inputStream, double availability, double requestThreshold) throws IOException {
        LogScanner scanner = new LogScanner(new SimpleLogLineParser());
        List<ReportEntry> report = scanner.read(inputStream, availability, requestThreshold);

        for (ReportEntry reportEntry : report) {
            System.out.println(reportEntry.toString());
        }
    }

    private static InputStream selectStream(File file) throws FileNotFoundException {
        if (file != null) {
            return new FileInputStream(file);
        } else {
            return System.in;
        }
    }

}
