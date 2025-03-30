package ru.avmakarov;

import java.text.DecimalFormat;
import java.util.Objects;

public class ReportEntry {

    private static final ThreadLocal<DecimalFormat> format = ThreadLocal.withInitial(() -> new DecimalFormat("#.00"));

    private final String beginTimestamp;
    private String endTimestamp;
    private int totalCount = 0;
    private int failureCount = 0;

    public ReportEntry(String timestamp) {
        beginTimestamp = timestamp;
        endTimestamp = timestamp;
    }

    public ReportEntry(String beginTimestamp, String endTimestamp, int totalCount, int failureCount) {
        this.beginTimestamp = beginTimestamp;
        this.endTimestamp = endTimestamp;
        this.totalCount = totalCount;
        this.failureCount = failureCount;
    }

    public static double getAvailability(int totalCount, int failureCount) {
        if (totalCount < failureCount) {
            throw new IllegalStateException("Failure count is bigger than total count. Check the algorithm");
        }
        if (totalCount == 0) {
            if (failureCount == 0) {
                return 100.0;
            }
        }
        return 100.0 - 100.0 * failureCount / totalCount;
    }

    public double getAvailability() {
        return getAvailability(getTotalCount(), getFailureCount());
    }

    @Override
    public String toString() {
        return beginTimestamp + ' ' + endTimestamp + ' ' + format.get().format(getAvailability());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportEntry entry = (ReportEntry) o;
        return totalCount == entry.totalCount && failureCount == entry.failureCount && Objects.equals(beginTimestamp, entry.beginTimestamp) && Objects.equals(endTimestamp, entry.endTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginTimestamp, endTimestamp, totalCount, failureCount);
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void incFailureCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Failure count cannot be negative");
        }
        failureCount += count;
    }

    public void incTotalCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Total count cannot be negative");
        }
        totalCount += count;
    }
}
