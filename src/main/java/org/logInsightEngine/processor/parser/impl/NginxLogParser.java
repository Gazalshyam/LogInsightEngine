package org.logInsightEngine.processor.parser.impl;

import org.logInsightEngine.model.ExtractedDocument;
import org.logInsightEngine.model.LogEntry;
import org.logInsightEngine.processor.parser.AbstractLogParser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NginxLogParser extends AbstractLogParser {
    @Override
    public boolean supports(String message) {
        // Implementation for checking if the message is a Spring Boot log
        return false;
    }

    @Override
    protected boolean isStartOfLogEntry(String line){
        return true;
    }
    @Override
    protected LogEntry parseLogEntryHeader(String line){
        return null;
    }

    @Override
    protected void handleContinuationLine(LogEntry currentEntry, String line) {

    }
}
