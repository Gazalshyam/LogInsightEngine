package org.logInsightEngine.document.extractor;

import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

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
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(multipartFile.getInputStream())) {
            String content = new String(gzipInputStream.readAllBytes(), StandardCharsets.UTF_8);
            return ExtractedDocument.builder().content(content).documentType(DocumentType.GZIP).fileName(multipartFile.getOriginalFilename()).lineCount(content.lines().count()).size(multipartFile.getSize()).build();
        }
    }
}
