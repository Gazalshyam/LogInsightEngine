package org.logInsightEngine.document.analyzer.impl;

import org.logInsightEngine.document.analyzer.AnalyzerUtils;
import org.logInsightEngine.document.analyzer.ErrorAnalyzer;
import org.logInsightEngine.dtos.result.ErrorAnalysisResult;
import org.logInsightEngine.dtos.result.ErrorGroup;
import org.logInsightEngine.dtos.result.Impact;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class DefaultErrorAnalyzerImplementation implements ErrorAnalyzer {
    @Override
    public ErrorAnalysisResult analyze(List<LogEntry> logEntries) {
        // Implement your error analysis logic here
        Map<String, ErrorGroup> errorGroupsByFingerprint = new LinkedHashMap<>();
        Objects.requireNonNull(logEntries, "logEntries cannot be null");
        for (LogEntry logEntry : logEntries) {
            Objects.requireNonNull(logEntry, "logEntry cannot be null");
            if (logEntry.getLevel() != LogLevel.ERROR) {
                continue;
            }
            String exceptionType = AnalyzerUtils.extractExceptionType(logEntry.getStackTrace());
            if (exceptionType == null || exceptionType.isEmpty()) {
                exceptionType = "UnknownException";
            }
            String fingerprint = AnalyzerUtils.normalize(exceptionType) + "|" + AnalyzerUtils.normalize(logEntry.getMessage());
            ErrorGroup errorGroup = errorGroupsByFingerprint.get(fingerprint);
            if (errorGroup == null) {
                errorGroup = new ErrorGroup(fingerprint, exceptionType, logEntry.getMessage(), AnalyzerUtils.generateUniqueId());
                errorGroupsByFingerprint.put(fingerprint, errorGroup);

            }
            errorGroup.setOccurrenceCount(errorGroup.getOccurrenceCount() + 1);
            updateErrorGroupTimestamps(errorGroup, logEntry);
            updateErrorGroupImpact(errorGroup, logEntry);
        }
        return ErrorAnalysisResult.builder().errorGroups(new ArrayList<>(errorGroupsByFingerprint.values())).build();
    }

    private void updateErrorGroupTimestamps(ErrorGroup errorGroup, LogEntry logEntry) {
        Instant timestamp = logEntry.getTimestamp();
        if (timestamp != null) {
            if (errorGroup.getFirstOccurrence() == null || timestamp.isBefore(errorGroup.getFirstOccurrence())) {
                errorGroup.setFirstOccurrence(timestamp);
            }
            if (errorGroup.getLastOccurrence() == null || timestamp.isAfter(errorGroup.getLastOccurrence())) {
                errorGroup.setLastOccurrence(timestamp);
            }
        }
    }

    private void updateErrorGroupImpact(ErrorGroup errorGroup, LogEntry logEntry) {
        if (errorGroup.getImpact() == null) {
            errorGroup.setImpact(Impact.createImpact());
        }
        Impact impact = errorGroup.getImpact();
        if (logEntry.getLogger() != null) {
            impact.getLoggers().add(logEntry.getLogger());
        }
        if (logEntry.getThread() != null) {
            impact.getThreads().add(logEntry.getThread());
        }
        //todo: later add services
    }
}
