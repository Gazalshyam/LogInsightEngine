package org.logInsightEngine.document.extractor;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfFileExtractor implements FileExtractor {
    public boolean supports(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            return false;
        }
        return multipartFile.getOriginalFilename().toLowerCase().endsWith(".pdf");
    }

    @Override
    public ExtractedDocument extract(MultipartFile multipartFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(multipartFile.getBytes())) {
            String content = new org.apache.pdfbox.text.PDFTextStripper().getText(document);
            return ExtractedDocument.builder().content(content).documentType(DocumentType.PDF).fileName(multipartFile.getOriginalFilename()).lineCount(content.lines().count()).size(multipartFile.getSize()).build();
        }
    }


}
