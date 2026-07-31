package org.logInsightEngine.document.extractor;

import org.logInsightEngine.model.domain.ExtractedDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileExtractor {

    boolean supports(MultipartFile file);

    ExtractedDocument extract(MultipartFile file) throws IOException;

}