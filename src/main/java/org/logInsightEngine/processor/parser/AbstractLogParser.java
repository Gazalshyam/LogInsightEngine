package org.logInsightEngine.processor.parser;

import org.logInsightEngine.model.LogEntry;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractLogParser implements LogParser {
    protected abstract boolean isStartOfLogEntry(String line);

    protected abstract LogEntry parseHeader(String line);
}
