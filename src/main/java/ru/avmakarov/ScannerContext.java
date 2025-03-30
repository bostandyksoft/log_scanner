package ru.avmakarov;

import ru.avmakarov.parser.LogLineParser;

import java.util.LinkedList;
import java.util.List;

public class ScannerContext {

    /**
     * Минимальнодопустимый уровень доступности
     */
    private final double requestedAvailability;

    /**
     * Максимальнодопустимое время обработки
     */
    private final double threshold;

    private ReportEntry current = null;
    private ReportEntry previous = null;
    private final List<ReportEntry> report = new LinkedList<>();

    public ScannerContext(double requestedAvailability, double threshold) {
        this.requestedAvailability = requestedAvailability;
        this.threshold = threshold;
    }

    public void next(LogLineParser.LineInfo lineInfo) {
        if (current == null) {
            current = new ReportEntry(lineInfo.timestamp());
        } else if (!current.getEndTimestamp().equals(lineInfo.timestamp())) {
            flushTimestamp(false);
            current = new ReportEntry(lineInfo.timestamp());
        }
        current.incTotalCount(1);
        if (isFailure(lineInfo)) {
            current.incFailureCount(1);
        }
    }

    /**
     * Если у новой записи сменилась секунда, то :
     * - проверим общую доступность текущего окончившегося отрезка и предыдущего
     * -   если общая доступность ниже допустимого уровня - сливаем отрезки
     * -   если общая доступность выше допустимого уровня
     * -   если доступность последнего элемента выше допустимого уровня - не учитываем его
     *
     * @param beforeClose Если true то в варианте 1 добавим суммарный отрезок в отчет
     */
    public void flushTimestamp(boolean beforeClose) {
        if (previous != null) {
            double totalAvailability = ReportEntry.getAvailability(current.getTotalCount() + previous.getTotalCount(), current.getFailureCount() + previous.getFailureCount());
            if (totalAvailability > requestedAvailability) {
                if (previous.getAvailability() < requestedAvailability) {
                    report.add(previous);
                }
                previous = current;
            } else {
                previous.setEndTimestamp(current.getEndTimestamp());
                previous.incFailureCount(current.getFailureCount());
                previous.incTotalCount(current.getTotalCount());
            }
        } else {
            previous = current;
        }
        if (beforeClose && previous != null && previous.getAvailability() < requestedAvailability) {
            report.add(previous);
        }
    }

    /**
     * Проверка на то, что текущая строчка лога - отказ
     *
     * @param lineInfo Информация из лога
     * @return true если отказ
     */
    boolean isFailure(LogLineParser.LineInfo lineInfo) {
        if (lineInfo.status() >= 500 && lineInfo.status() < 600) {
            return true;
        }
        return lineInfo.responseTime() > threshold;
    }

    public List<ReportEntry> getReport() {
        return report;
    }


}
