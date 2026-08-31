package org.logInsightEngine.service;

import lombok.extern.slf4j.Slf4j;
import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.dtos.response.AnalyzeResponse;
import org.logInsightEngine.model.domain.AnalysisStatus;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.SourceType;
import org.logInsightEngine.result.SourceProcessingResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AnalysisService {
    private final SourceProcessingService sourceProcessingService;

    public AnalysisService(SourceProcessingService sourceProcessingService) {
        this.sourceProcessingService = sourceProcessingService;
    }

    public AnalyzeResponse submitAnalysis(AnalyzeRequest analyzeRequest) {
        // Placeholder for analysis submission logic
        List<LogEntry> parsedLogEntries = new ArrayList<>();
        List<SourceProcessingResult> sourceProcessingResults = sourceProcessingService.processRequest(analyzeRequest);
        int sourcesSucceeded = 0;
        int sourcesAttempted = sourceProcessingResults.size();
        AnalyzeResponse analyzeResponse = new AnalyzeResponse();
        analyzeResponse.setAnalysisId(UUID.randomUUID().toString());

        for (SourceProcessingResult result : sourceProcessingResults) {
            if (result.isSuccess()) {
                sourcesSucceeded++;
                parsedLogEntries.addAll(result.getLogEntries());
            } else {
                if (result.getSourceType().equals(SourceType.LOG_FILE)) {
                    log.info("Error received at log file source.");
                    analyzeResponse.setLogFileError(result.getErrorMessage());
                    analyzeResponse.setLogFileErrorType(result.getErrorType());
                } else if (result.getSourceType().equals(SourceType.LOG_DATA)) {
                    log.info("Error received at log data source.");
                    analyzeResponse.setLogDataErrorType(result.getErrorType());
                    analyzeResponse.setLogDataError(result.getErrorMessage());
                }
            }
        }
        if (sourcesSucceeded == sourcesAttempted && sourcesAttempted > 0) {
            analyzeResponse.setStatus(AnalysisStatus.SUCCESS);
        } else if (sourcesSucceeded == 0 || sourcesAttempted == 0) {
            analyzeResponse.setStatus(AnalysisStatus.FAILED);
        } else {
            analyzeResponse.setStatus(AnalysisStatus.PARTIAL_SUCCESS);
        }
        return analyzeResponse;
    }

    public AnalyzeResponse getAnalysisResult(String id) {
        // Placeholder for retrieving analysis result
        AnalyzeResponse analyzeResponse = new AnalyzeResponse();
        analyzeResponse.setStatus(AnalysisStatus.SUCCESS);
        analyzeResponse.setAnalysisId(UUID.randomUUID().toString());
        return analyzeResponse;
    }
}
