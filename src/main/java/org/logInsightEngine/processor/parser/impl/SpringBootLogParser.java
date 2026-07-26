package org.logInsightEngine.processor.parser.impl;

import org.logInsightEngine.model.ExtractedDocument;
import org.logInsightEngine.model.LogEntry;
import org.logInsightEngine.processor.parser.LogParser;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class SpringBootLogParser implements LogParser {
    @Override
    public boolean supports(String message) {
        // Implementation for checking if the message is a Spring Boot log
        return false;
    }

    @Override
    public List<LogEntry> parse(ExtractedDocument extractedDocument) {
        // Implementation for parsing Spring Boot logs
        return java.util.Collections.emptyList();
    }
}
