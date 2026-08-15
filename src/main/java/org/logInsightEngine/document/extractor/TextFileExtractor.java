package org.logInsightEngine.document.extractor;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class TextFileExtractor implements FileExtractor {
    @Override
    public boolean supports(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.getOriginalFilename() == null) {
            return false;
        }
        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".txt");
    }

    @Override
    public ExtractedDocument extract(MultipartFile multipartFile) throws IOException {
        // Implementation for extracting text from a text file
        String content = new String(multipartFile.getBytes());
        return ExtractedDocument.builder().content(content).documentType(DocumentType.TEXT).fileName(multipartFile.getOriginalFilename()).lineCount(content.lines().count()).size(multipartFile.getSize()).build();
    }
}
