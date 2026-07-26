package org.logInsightEngine.processor.parser;

import org.logInsightEngine.model.ExtractedDocument;
import org.logInsightEngine.model.LogEntry;

import java.util.List;

public interface LogParser {
    public boolean supports(String message);
    List<LogEntry> parse(ExtractedDocument  extractedDocument);
}
