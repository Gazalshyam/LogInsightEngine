package org.logInsightEngine.document.extractor;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class LogFileExtractor implements FileExtractor {
    public boolean supports(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.getOriginalFilename() == null) {
            return false;
        }
        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".log");
    }

     public ExtractedDocument extract(MultipartFile multipartFile) throws IOException {
         String content = new String(multipartFile.getBytes());
         return ExtractedDocument.builder().content(content).documentType(DocumentType.LOG).fileName(multipartFile.getOriginalFilename()).lineCount(content.lines().count()).size(multipartFile.getSize()).build();
     }

}
