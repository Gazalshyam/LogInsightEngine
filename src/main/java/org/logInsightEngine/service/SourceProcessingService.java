package org.logInsightEngine.service;

import org.logInsightEngine.document.extractor.FileExtractor;
import org.logInsightEngine.document.extractor.FileExtractorFactory;
import org.logInsightEngine.document.parser.LogParser;
import org.logInsightEngine.document.parser.LogParserFactory;
import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.exception.FileProcessingException;
import org.logInsightEngine.exception.UnsupportedLogFormatException;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.SourceType;
import org.logInsightEngine.dtos.result.SourceProcessingResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class SourceProcessingService {

    private final LogParserFactory logParserFactory;
    private final FileExtractorFactory fileExtractorFactory;

    public SourceProcessingService(LogParserFactory logParserFactory, FileExtractorFactory fileExtractorFactory) {
        this.fileExtractorFactory = fileExtractorFactory;
        this.logParserFactory = logParserFactory;
    }

    public List<SourceProcessingResult> processRequest(AnalyzeRequest analyzeRequest) {
        List<SourceProcessingResult> results = new ArrayList<>();
        if (analyzeRequest.getLogFile() != null && !analyzeRequest.getLogFile().isEmpty()) {
            SourceProcessingResult resultLogFile = processLogFile(analyzeRequest);
            results.add(resultLogFile);
        }
        if (analyzeRequest.getLogData() != null && !analyzeRequest.getLogData().isBlank()) {
            SourceProcessingResult resultLogData = processLogData(analyzeRequest);
            results.add(resultLogData);
        }
        return results;

    }


    public SourceProcessingResult processLogFile(AnalyzeRequest analyzeRequest) {
        SourceProcessingResult result = new SourceProcessingResult();
        result.setSourceType(SourceType.LOG_FILE);
        try {
            FileExtractor extractor = fileExtractorFactory.getExtractor(analyzeRequest.getLogFile());
            ExtractedDocument extractedDocument = extractor.extract(analyzeRequest.getLogFile());

            LogParser logParser = logParserFactory.getParser(extractedDocument.getContent());
            List<LogEntry> logEntries = logParser.parse(extractedDocument);
            result.setLogEntries(logEntries);
            result.setSuccess(true);
            return result;
        } catch (UnsupportedLogFormatException e) {
            result.setErrorMessage("Unsupported log format. Please ensure the log file is in a supported format.");
            result.setErrorType("UNSUPPORTED_LOG_FORMAT");
        } catch (IOException e) {
            throw new FileProcessingException("An error occurred while processing the log file: " + e.getMessage());
        }
        result.setSuccess(false);
        return result;
    }

    public SourceProcessingResult processLogData(AnalyzeRequest analyzeRequest) {
        SourceProcessingResult result = new SourceProcessingResult();
        result.setSourceType(SourceType.LOG_DATA);
        try {
            ExtractedDocument extractedDocument = ExtractedDocument.builder().content(analyzeRequest.getLogData()).fileName("raw_text" + System.currentTimeMillis()).documentType(DocumentType.RAW_TEXT).size(analyzeRequest.getLogData().getBytes(StandardCharsets.UTF_8).length).lineCount(analyzeRequest.getLogData().lines().count()).build();
            LogParser logParser = logParserFactory.getParser(extractedDocument.getContent());
            List<LogEntry> logEntries = logParser.parse(extractedDocument);
            result.setLogEntries(logEntries);
            result.setSuccess(true);
            return result;
        } catch (UnsupportedLogFormatException e) {
            result.setErrorMessage("Unsupported log format. Please ensure the log data is in a supported format.");
            result.setErrorType("UNSUPPORTED_LOG_FORMAT");
        }
        result.setSuccess(false);
        return result;
    }

}
