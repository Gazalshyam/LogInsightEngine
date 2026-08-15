package org.logInsightEngine.document.extractor;

import org.logInsightEngine.dtos.request.AnalyzeRequest;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GzipFileExtractor implements FileExtractor {
    public boolean supports(MultipartFile multipartFile) {
       if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            return false;
        }
        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".gz");
    }

    @Override
    public ExtractedDocument extract(MultipartFile multipartFile) throws IOException {
        String content = new String(multipartFile.getBytes());
        return ExtractedDocument.builder().content(content).documentType(DocumentType.GZIP).fileName(multipartFile.getOriginalFilename()).lineCount(content.lines().count()).size(multipartFile.getSize()).build();
    }
}
