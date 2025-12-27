package org.logInsightEngine.dtos;

import org.springframework.web.multipart.MultipartFile;

public class AnalyzeRequest {
private String logData;
private MultipartFile logFile;

    public String getLogData() {
        return logData;
    }

    public void setLogData(String logData) {
        this.logData = logData;
    }

    public MultipartFile getLogFile() {
        return logFile;
    }

    public void setLogFile(MultipartFile logFile){
        this.logFile = logFile;
    }
}