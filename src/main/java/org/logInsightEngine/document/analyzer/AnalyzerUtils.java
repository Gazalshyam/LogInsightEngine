package org.logInsightEngine.document.analyzer;

import java.util.UUID;

public class AnalyzerUtils {
    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", " ");
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }


    public static String extractExceptionType(String stackTrace) {

        if (stackTrace == null || stackTrace.isBlank()) {
            return null;
        }
        String firstLine = stackTrace.lines().map(String::strip).filter(line -> !line.isEmpty()).findFirst().orElse("");
        int colonIndex = firstLine.indexOf(':');
        if (colonIndex > 0) {
            return firstLine.substring(0, colonIndex).strip();
        }
        return firstLine.strip();
    }
}
