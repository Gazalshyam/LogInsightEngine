package org.logInsightEngine.processor.parser;

import org.logInsightEngine.model.ExtractedDocument;
import org.logInsightEngine.model.LogEntry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractLogParser implements LogParser {
    protected abstract boolean isStartOfLogEntry(String line);

    protected abstract LogEntry parseLogEntryHeader(String line);
    protected abstract void handleContinuationLine(LogEntry currentEntry, String line);
    @Override
    public List<LogEntry> parse(ExtractedDocument extractedDocument){
        String content = extractedDocument.getContent();
        List<LogEntry> logEntries = new ArrayList<>();
        LogEntry currentEntry = null;

        for(String lineString : content.split("\r?\n")){
            if(isStartOfLogEntry(lineString)){
                LogEntry logEntry = parseLogEntryHeader(lineString);
                if(currentEntry != null) {
                    logEntries.add(currentEntry);
                }
                currentEntry = logEntry;
            }
            else{
                if(StringUtils.hasText(lineString) && currentEntry != null){
                    handleContinuationLine(currentEntry, lineString);
                }
            }
        }
        if(currentEntry != null){
            logEntries.add(currentEntry);
        }
        return logEntries;
    }
}
