package org.logInsightEngine.processor.extractor;

import org.logInsightEngine.model.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfFileExtractor implements FileExtractor {
    public boolean supports(String file) {
        if (file.toLowerCase().endsWith(".pdf")) {
            return true;
        }
        return false;
    }

    @Override
    public ExtractedDocument extract(MultipartFile file) throws IOException {
        return new ExtractedDocument();
    }


}
