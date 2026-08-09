package org.logInsightEngine.document.extractor;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GzipFileExtractor implements FileExtractor {
    public boolean supports(AnalyzeRequest analyzeRequest) {
        if(analyzeRequest.getLogFile() == null) {
            return false;
        }
        MultipartFile multipartFile = analyzeRequest.getLogFile();
        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".gz");
    }

    @Override
    public ExtractedDocument extract(AnalyzeRequest analyzeRequest) throws IOException {
        return new ExtractedDocument();
    }
}
