package org.logInsightEngine.document.parser;

import org.logInsightEngine.model.domain.ExtractedDocument;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface LogParser {
    boolean supports(List<String> nonEmptyLines);

    List<LogEntry> parse(ExtractedDocument extractedDocument);
}
