package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.SourceType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceProcessingResult {
    private boolean success;
    private List<LogEntry> logEntries;
    private String errorType;
    private String errorMessage;
    private SourceType sourceType; // "logData" or "logFile"
}
