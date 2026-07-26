package org.logInsightEngine.processor.extractor;

import org.logInsightEngine.exception.UnsupportedFileTypeException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileExtractorFactory {
    private final  List<FileExtractor> extractors;
    public FileExtractorFactory(List<FileExtractor> extractors) {
        this.extractors = extractors;
    }
    // currently supports .log, .gz. and .pdf
    public FileExtractor getExtractor(String fileName) {

        for (FileExtractor extractor : extractors) {
            if (extractor.supports(fileName)) {
                return extractor;
            }

        }

        throw new UnsupportedFileTypeException("Unsupported file type: " + fileName);
    }
}
