package org.logInsightEngine.document.extractor;

import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GzipFileExtractor implements FileExtractor {
    public boolean supports(MultipartFile  multipartFile) {
        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".gz");
    }

    @Override
    public ExtractedDocument extract(MultipartFile file) throws IOException {
        return new ExtractedDocument();
    }
}
