package org.logInsightEngine.service;

import org.logInsightEngine.dtos.AnalyzeRequest;
import org.logInsightEngine.dtos.AnalyzeResponse;
import org.logInsightEngine.processor.InputNormalizer;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    private InputNormalizer inputNormalizer;
    public AnalysisService(InputNormalizer inputNormalizer){
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
