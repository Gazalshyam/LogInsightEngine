package org.logInsightEngine.processor.extractor;

import lombok.AllArgsConstructor;
import org.logInsightEngine.model.ExtractedDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
public interface FileExtractor {

   boolean supports(String fileName);
   ExtractedDocument extract(MultipartFile file) throws IOException;

}