package org.logInsightEngine;

import org.logInsightEngine.model.domain.DocumentType;
import org.logInsightEngine.model.domain.ExtractedDocument;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {
    private TestUtils() {

    }

    public static ExtractedDocument loadDocument(String resourcePath) throws IOException {

        String content = readResource(resourcePath);

        return ExtractedDocument.builder().fileName(resourcePath.substring(resourcePath.lastIndexOf('/') + 1)).documentType(DocumentType.TEXT).content(content).size(content.length()).lineCount(content.lines().count()).build();
    }

    public static String readResource(String resourcePath) throws IOException {

        try (InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
