package org.logInsightEngine.controller;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.dtos.result.AnalysisResult;
import org.logInsightEngine.service.AnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnalyzeController {

    private final AnalysisService analysisService;

    //controller constructor
    public AnalyzeController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    //endpoint to submit log data for analysis
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisResult> analyze(@ModelAttribute AnalyzeRequest analyzeRequest) {
        AnalysisResult analyzeResponses = analysisService.submitAnalysis(analyzeRequest);
        return ResponseEntity.ok(analyzeResponses);
    }

    //endpoint to get analysis result by ID
    @GetMapping(value = "/analysis/{id}", produces = "application/json")
    public ResponseEntity<AnalysisResult> getAnalysis(@PathVariable String id) {
        AnalysisResult analyzeResponse = analysisService.getAnalysisResult(id);
        return ResponseEntity.ok(analyzeResponse);
    }

}
