package org.logInsightEngine.document.normalizer;

import org.logInsightEngine.document.ocr.OcrExtractor;
import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.springframework.stereotype.Component;

@Component
public class InputNormalizer {

    private final OcrExtractor ocrExtractor;

    public InputNormalizer(OcrExtractor ocrExtractor) {
        this.ocrExtractor = ocrExtractor;
    }

    public String normalize(AnalyzeRequest analyzeRequest) {
        StringBuilder normalizedInput = new StringBuilder();
        if (analyzeRequest == null) {
            throw new IllegalArgumentException("AnalyzeRequest cannot be null");
        }
        if (analyzeRequest.getLogData() == null && (analyzeRequest.getLogFile() == null || analyzeRequest.getLogFile().isEmpty())) {
            throw new IllegalArgumentException("Either log data or log file must be provided");
        }
        if (analyzeRequest.getLogData() != null) {
            normalizedInput.append("\n-------------LOG DATA START-------------\n");
            normalizedInput.append(analyzeRequest.getLogData().trim());
            normalizedInput.append("\n-------------LOG DATA END-------------\n");
        }
        if (analyzeRequest.getLogFile() != null && !analyzeRequest.getLogFile().isEmpty()) {
            normalizedInput.append("\n-------------LOG FILE START-------------\n");
            normalizedInput.append("Original Name: ").append(analyzeRequest.getLogFile().getOriginalFilename());
            normalizedInput.append("\nContent-Type: ").append(analyzeRequest.getLogFile().getContentType());
            normalizedInput.append("\nFile Size: ").append(analyzeRequest.getLogFile().getSize());
            String fileData = ocrExtractor.extractText(analyzeRequest.getLogFile());
            normalizedInput.append("\nExtracting Log Data:\n");
            normalizedInput.append("File Data: ");
            if (fileData == null || fileData.isEmpty()) {
                normalizedInput.append("No text could be extracted from the log file.");
            } else {
                normalizedInput.append(fileData.trim());
            }
            normalizedInput.append("\n-------------LOG FILE END-------------\n");
        }
        return normalizedInput.toString();
    }
}
