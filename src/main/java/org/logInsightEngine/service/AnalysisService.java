package org.logInsightEngine.service;

import org.logInsightEngine.document.normalizer.InputNormalizer;
import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.dtos.response.AnalyzeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    private final InputNormalizer inputNormalizer;

    public AnalysisService(InputNormalizer inputNormalizer) {
        this.inputNormalizer = inputNormalizer;
    }

    public AnalyzeResponse submitAnalysis(AnalyzeRequest analyzeRequest) {
        // Placeholder for analysis submission logic
        AnalyzeResponse analyzeResponse = new AnalyzeResponse();
        analyzeResponse.setStatus("success");
        analyzeResponse.setAnalysisId(UUID.randomUUID().toString());
        String normalizedInput = inputNormalizer.normalize(analyzeRequest);
        logger.info("Normalized Input for Analysis ID {}: \n{}", analyzeResponse.getAnalysisId(), normalizedInput);
        return analyzeResponse;
    }


    public AnalyzeResponse getAnalysisResult(String id) {
        // Placeholder for retrieving analysis result
        AnalyzeResponse analyzeResponse = new AnalyzeResponse();
        analyzeResponse.setStatus("success");
        analyzeResponse.setAnalysisId(UUID.randomUUID().toString());
        return analyzeResponse;
    }
}
