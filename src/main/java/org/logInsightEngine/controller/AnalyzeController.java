package org.logInsightEngine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.logInsightEngine.dtos.AnalyzeRequest;
import org.logInsightEngine.dtos.AnalyzeResponse;
import org.logInsightEngine.service.AnalysisService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnalyzeController {

    private final AnalysisService analysisService;
    private final ObjectMapper objectMapper;

    //controller constructor
    public AnalyzeController(AnalysisService analysisService, ObjectMapper objectMapper) {
        this.analysisService = analysisService;
        this.objectMapper = objectMapper;
    }

    //endpoint to submit log data for analysis
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> analyze(@ModelAttribute AnalyzeRequest analyzeRequest){
        AnalyzeResponse analyzeResponse = analysisService.submitAnalysis(analyzeRequest);
            return ResponseEntity.ok(analyzeResponse);
    }

    //endpoint to get analysis result by ID
    @GetMapping(value = "/analysis/{id}", produces = "application/json")
    public ResponseEntity<Object> getAnalysis(@PathVariable String id){
        AnalyzeResponse analyzeResponse = analysisService.getAnalysisResult(id);
        return ResponseEntity.ok(analyzeResponse);
    }

}
