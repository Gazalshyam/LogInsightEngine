package org.logInsightEngine.document.ocr;


import org.springframework.beans.factory.annotation.Value;

public class OCRProperties {
    @Value("$ocr.tessdata.path")
    public static final String OCR_DATA_PATH = "/tmp/tessdata/";
}
