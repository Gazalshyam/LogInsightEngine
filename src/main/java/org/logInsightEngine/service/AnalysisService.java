package org.logInsightEngine.service;

import lombok.extern.slf4j.Slf4j;
import org.logInsightEngine.document.extractor.FileExtractor;
import org.logInsightEngine.document.extractor.FileExtractorFactory;
import org.logInsightEngine.document.parser.LogParser;
import org.logInsightEngine.document.parser.LogParserFactory;
import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.dtos.response.AnalyzeResponse;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.logInsightEngine.model.domain.LogEntry;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AnalysisService {

    private final FileExtractorFactory fileExtractorFactory;
    private final LogParserFactory logParserFactory;

    public AnalysisService(FileExtractorFactory fileExtractorFactory, LogParserFactory logParserFactory) {
        this.fileExtractorFactory = fileExtractorFactory;
        this.logParserFactory = logParserFactory;
    }

    public AnalyzeResponse submitAnalysis(AnalyzeRequest analyzeRequest) {
        // Placeholder for analysis submission logic
        try {
            AnalyzeResponse analyzeResponse = new AnalyzeResponse();
            analyzeResponse.setStatus("success");
            analyzeResponse.setAnalysisId(UUID.randomUUID().toString());
            ExtractedDocument extractedDocument = null;
            if (analyzeRequest.getLogFile() != null) {
                FileExtractor extractor = fileExtractorFactory.getExtractor(analyzeRequest.getLogFile());
                extractedDocument = extractor.extract(analyzeRequest.getLogFile());
            } else if (analyzeRequest.getLogData() != null) {
                extractedDocument = ExtractedDocument.builder().content(analyzeRequest.getLogData()).fileName("raw_text" + System.currentTimeMillis()).documentType(DocumentType.RAW_TEXT).size(analyzeRequest.getLogData().getBytes(StandardCharsets.UTF_8).length).lineCount(analyzeRequest.getLogData().lines().count()).build();
            }
            LogParser logParser = logParserFactory.getParser(extractedDocument.getContent());
            List<LogEntry> parsedLogEntries = logParser.parse(extractedDocument);
            return analyzeResponse;
        } catch (Exception e) {
            log.error("Error while processing analysis request", e);
            return new AnalyzeResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        }
    }


    public AnalyzeResponse getAnalysisResult(String id) {
        // Placeholder for retrieving analysis result
        AnalyzeResponse analyzeResponse = new AnalyzeResponse();
        analyzeResponse.setStatus("success");
        analyzeResponse.setAnalysisId(UUID.randomUUID().toString());
        return analyzeResponse;
    }
}
