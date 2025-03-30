package ru.avmakarov.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Выбирает информацию из строки путем разделения строки по пробелам
 */
public class SimpleLogLineParser implements LogLineParser {
    private static final Logger logger = LoggerFactory.getLogger(SimpleLogLineParser.class);

    /**
     * Разбор строки логов
     * @param line Входящая строка
     * @return null если строка не содержит достаточное количество токенов
     */
    @Override
    public LineInfo parse(String line) {
        String[] parts = line.split(" ");
        if (parts.length < 11) {
            logger.warn("Недостаточно данных: {}", line);
            return null;
        }

        String timestamp = parts[3] + " " + parts[4];
        int status = getStatus(parts[8]);
        double responseTime = getResponseTime(parts[10]);

        return new LineInfo(timestamp, status, responseTime);
    }

    int getStatus(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение статуса " + part);
        }
    }

    double getResponseTime(String part) {
        try {
            return Double.parseDouble(part);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение времени отклика " + part);
        }
    }

}
