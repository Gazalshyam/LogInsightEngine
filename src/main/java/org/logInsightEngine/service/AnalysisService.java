package org.logInsightEngine.service;

import org.logInsightEngine.document.extractor.FileExtractor;
import org.logInsightEngine.document.extractor.FileExtractorFactory;
import org.logInsightEngine.document.parser.LogParser;
import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.dtos.response.AnalyzeResponse;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.logInsightEngine.model.domain.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    private final FileExtractorFactory fileExtractorFactory;
    private final LogParser logParser;

    public AnalysisService(FileExtractorFactory fileExtractorFactory, LogParser logParser) {
        this.fileExtractorFactory = fileExtractorFactory;
        this.logParser = logParser;
    }

    public AnalyzeResponse submitAnalysis(AnalyzeRequest analyzeRequest) {
        // Placeholder for analysis submission logic
        try {
            AnalyzeResponse analyzeResponse = new AnalyzeResponse();
            analyzeResponse.setStatus("success");
            analyzeResponse.setAnalysisId(UUID.randomUUID().toString());
//            String normalizedInput = inputNormalizer.normalize(analyzeRequest);
            ExtractedDocument extractedDocument = null;
            if (analyzeRequest.getLogFile() != null) {
                FileExtractor extractor = fileExtractorFactory.getExtractor(analyzeRequest);

                extractedDocument = extractor.extract(analyzeRequest);
            }
            if (analyzeRequest.getLogData() != null) {
                extractedDocument = ExtractedDocument.builder().content(analyzeRequest.getLogData()).fileName("raw_text" + System.currentTimeMillis()).documentType(DocumentType.RAW_TEXT).size(analyzeRequest.getLogData().length()).lineCount(analyzeRequest.getLogData().getBytes(StandardCharsets.UTF_8).length).build();
            }
            List<LogEntry> parsedLogEntries = logParser.parse(extractedDocument);
//            logger.info("Normalized Input for Analysis ID {}: \n{}", analyzeResponse.getAnalysisId(), normalizedInput);
            return analyzeResponse;
        } catch (Exception e) {
            logger.error("Error while processing analysis request", e);
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
