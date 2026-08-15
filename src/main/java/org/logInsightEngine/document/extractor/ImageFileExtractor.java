package org.logInsightEngine.document.extractor;

import org.logInsightEngine.document.ocr.OcrExtractor;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageFileExtractor implements FileExtractor {

    private final OcrExtractor ocrExtractor;

    public ImageFileExtractor(OcrExtractor ocrExtractor) {
        this.ocrExtractor = ocrExtractor;
    }

    public boolean supports(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            return false;
        }

        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".png");
    }

    @Override
    public ExtractedDocument extract(MultipartFile multipartFile) throws IOException {
        String content = ocrExtractor.extractText(multipartFile);
        return ExtractedDocument.builder().content(content).documentType(DocumentType.IMAGE).fileName(multipartFile.getOriginalFilename()).lineCount(content.lines().count()).size(multipartFile.getSize()).build();
    }
}
