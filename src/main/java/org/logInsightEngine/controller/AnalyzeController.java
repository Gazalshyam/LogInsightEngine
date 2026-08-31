package org.logInsightEngine.controller;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.dtos.response.AnalyzeResponse;
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
    public ResponseEntity<AnalyzeResponse> analyze(@ModelAttribute AnalyzeRequest analyzeRequest) {
        AnalyzeResponse analyzeResponses = analysisService.submitAnalysis(analyzeRequest);
        return ResponseEntity.ok(analyzeResponses);
    }

    //endpoint to get analysis result by ID
    @GetMapping(value = "/analysis/{id}", produces = "application/json")
    public ResponseEntity<AnalyzeResponse> getAnalysis(@PathVariable String id) {
        AnalyzeResponse analyzeResponse = analysisService.getAnalysisResult(id);
        return ResponseEntity.ok(analyzeResponse);
    }

}
