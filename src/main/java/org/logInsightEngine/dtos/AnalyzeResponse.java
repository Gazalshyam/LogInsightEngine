package org.logInsightEngine.dtos;



public class AnalyzeResponse {

    private String status;
    private String analysisId;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getAnalysisId() {
        return analysisId;
    }
    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }
}
