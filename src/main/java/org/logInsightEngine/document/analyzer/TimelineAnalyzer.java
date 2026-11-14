package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.Finding;
import org.logInsightEngine.dtos.result.Timeline;
import org.logInsightEngine.model.domain.LogEntry;

import java.util.List;

public interface TimelineAnalyzer {
    Timeline analyze(List<LogEntry> logEntries, List<Finding> findings);
}



