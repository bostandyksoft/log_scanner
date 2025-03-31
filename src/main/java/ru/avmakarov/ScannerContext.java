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
	private final double requestThreshold;

	private ReportEntry current = null;
	private ReportEntry lastFailed = null;
	private final List<ReportEntry> report = new LinkedList<>();

	public ScannerContext(double requestedAvailability, double requestThreshold) {
		this.requestedAvailability = requestedAvailability;
		this.requestThreshold = requestThreshold;
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
	 * - проверим доступность текущего окончившегося отрезка
	 * - если она удовлетворительна, то, если имеется отрезок, выпавший их нормы - добавим его в отчет
	 * - если нет, то установим наростим последний ошибокчный отрезок на величину текущего
	 *
	 * @param beforeClose Если true то в варианте 1 добавим суммарный отрезок в отчет
	 */
	public void flushTimestamp(boolean beforeClose) {
		if (current == null) {
			return;
		}
		if (current.getAvailability() >= requestedAvailability) {
			if (lastFailed != null) {
				report.add(lastFailed);
				lastFailed = null;
			}
		} else {
			if (lastFailed == null) {
				lastFailed = current;
			} else {
				lastFailed.setEndTimestamp(current.getEndTimestamp());
				lastFailed.incTotalCount(current.getTotalCount());
				lastFailed.incFailureCount(current.getFailureCount());
			}
		}
		if (beforeClose && lastFailed != null) {
			report.add(lastFailed);
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
		return lineInfo.responseTime() > requestThreshold;
	}

	public List<ReportEntry> getReport() {
		return report;
	}


}
