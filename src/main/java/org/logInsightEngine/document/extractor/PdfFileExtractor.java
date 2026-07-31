package org.logInsightEngine.document.extractor;

import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfFileExtractor implements FileExtractor {
    public boolean supports(MultipartFile file) {
        return file.getOriginalFilename().toLowerCase().endsWith(".pdf");
    }

    @Override
    public ExtractedDocument extract(MultipartFile file) throws IOException {
        return new ExtractedDocument();
    }


}
