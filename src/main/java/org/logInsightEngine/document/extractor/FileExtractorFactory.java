package org.logInsightEngine.document.extractor;

import org.logInsightEngine.exception.UnsupportedFileTypeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileExtractorFactory {
    private final List<FileExtractor> extractors;

    public FileExtractorFactory(List<FileExtractor> extractors) {
        this.extractors = extractors;
    }

    // currently supports .log, .gz. and .pdf
    public FileExtractor getExtractor(MultipartFile logFile) throws UnsupportedFileTypeException {

        for (FileExtractor extractor : extractors) {
            if (extractor.supports(logFile)) {
                return extractor;
            }

        }
        throw new UnsupportedFileTypeException("Unsupported file type found");
    }
}
