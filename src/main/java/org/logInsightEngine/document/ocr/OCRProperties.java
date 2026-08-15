package org.logInsightEngine.document.ocr;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr")
public class OCRProperties {
    private String tessdataPath = "/tmp/tessdata/";

    public String getTessdataPath() {
        return tessdataPath;
    }

    public void setTessdataPath(String tessdataPath) {
        this.tessdataPath = tessdataPath;
    }
}
