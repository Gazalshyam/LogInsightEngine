package org.logInsightEngine.document.analyzer.impl;

import org.logInsightEngine.document.analyzer.SeverityAnalyzer;
import org.logInsightEngine.dtos.result.SeverityStatistics;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultSeverityAnalyzer implements SeverityAnalyzer {
    @Override
    public SeverityStatistics analyze(List<LogEntry> logEntries) {
        SeverityStatistics severityStatistics = new SeverityStatistics();
        Objects.requireNonNull(logEntries, "Log entries cannot be null");
        Map<LogLevel, Long> countByLevel = new EnumMap<>(LogLevel.class);
        for (LogLevel logLevel : LogLevel.values()) {
            countByLevel.put(logLevel, 0L);
        }
        for (LogEntry logEntry : logEntries) {
            // Analyze each log entry and update severity statistics
            LogLevel level = logEntry.getLevel()== null? LogLevel.UNKNOWN : logEntry.getLevel();
            countByLevel.put(level, countByLevel.get(level) + 1);
        }
        return SeverityStatistics.builder().countByLevel(countByLevel).build();
    }
}
