package org.logInsightEngine.document.analyzer.impl;


import org.logInsightEngine.document.analyzer.SummaryAnalyzer;
import org.logInsightEngine.dtos.result.Summary;
import org.logInsightEngine.model.domain.LogEntry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public class DefaultSummaryAnalyzer implements SummaryAnalyzer {
    @Override
    public Summary analyze(List<LogEntry> logEntries) {
        // validate/handle null input
        // count the total number of log entries
        // calculate earliest timestamp
        // calculate latest timestamp
        // calculate duration
        // build Summary
        Objects.requireNonNull(logEntries, "Log entries cannot be null");
        Summary summary = new Summary();
        if (logEntries.isEmpty()) {
            summary.setTotalLogEntries(0);
            summary.setFirstTimestamp(null);
            summary.setLastTimestamp(null);
            summary.setDuration(Duration.ZERO);
            return summary;
        }
        summary.setTotalLogEntries(logEntries.size());
        extractNonNullTimestampEntries(logEntries, summary);
        return summary;
    }

    private void extractNonNullTimestampEntries(List<LogEntry> logEntries, Summary summary) {
        // filter out log entries with null timestamps
        List<Instant> timestamps = logEntries.stream().map(LogEntry::getTimestamp).filter(Objects::nonNull).toList();
        Instant firstTimestamp = timestamps.stream().min(Instant::compareTo).orElse(null);

        Instant lastTimestamp = timestamps.stream().max(Instant::compareTo).orElse(null);
        // calculate earliest timestamp
        summary.setFirstTimestamp(firstTimestamp);
        // calculate latest timestamp
        summary.setLastTimestamp(lastTimestamp);
        // calculate duration
        summary.setDuration(timestamps.isEmpty()? Duration.ZERO : Duration.between(firstTimestamp, lastTimestamp));
    }
}
