package org.logInsightEngine.document.parser.impl;

import org.logInsightEngine.document.parser.AbstractLogParser;
import org.logInsightEngine.model.domain.LogEntry;
import org.springframework.stereotype.Component;

@Component
public class ApacheLogParser extends AbstractLogParser {
    @Override
    public boolean supports(String message) {
        // Implementation for checking if the message is a Spring Boot log
        return false;
    }

    @Override
    protected boolean isStartOfLogEntry(String line) {
        return true;
    }

    @Override
    protected LogEntry parseLogEntryHeader(String line) {
        return null;
    }

    @Override
    protected void handleContinuationLine(LogEntry currentEntry, String line) {

    }
}
