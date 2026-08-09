package org.logInsightEngine.document.extractor;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileExtractor {

    boolean supports(AnalyzeRequest analyzeRequest);

    ExtractedDocument extract(AnalyzeRequest analyzeRequest) throws IOException;

}