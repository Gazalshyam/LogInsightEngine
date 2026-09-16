package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultCorrelationAnalyzerTests {
    private final DefaultCorrelationAnalyzer defaultCorrelationAnalyzer = new DefaultCorrelationAnalyzer();

    @Test
    public void testNullInput() {
        assertThrows(NullPointerException.class, () -> defaultCorrelationAnalyzer.analyze(null));
    }

    @Test
    public void testEmptyInput() {
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult(List.of());
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    private ErrorGroup createErrorGroup(String id, String exceptionType, String fingerprint, String message, long occurenceCount, Instant firstOccurrence, Instant lastOccurrence, Set<String> threads, Set<String> loggers) {
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setId(id);
        errorGroup.setExceptionType(exceptionType);
        errorGroup.setFingerprint(fingerprint);
        errorGroup.setMessage(message);
        errorGroup.setOccurrenceCount(occurenceCount);
        errorGroup.setFirstOccurrence(firstOccurrence);
        errorGroup.setLastOccurrence(lastOccurrence);
        Impact impact = new Impact();
        impact.setLoggers(loggers);
        impact.setThreads(threads);
        errorGroup.setImpact(impact);
        return errorGroup;
    }

    @Test
    public void testSingleErrorGroup() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|new database exception", "new database exception", 12, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    @Test
    public void testMultipleErrorGroupsUnrelated() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|new database exception", "new database exception", 2, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-12", "Exception", "Exception|database exception", "database exception", 12, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }


    @Test
    public void testSimilarErrorGroups() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 2, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|database exception", "database exception", 1, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertFalse(correlations.isEmpty());
        assertEquals(CorrelationType.SIMILAR, correlations.getFirst().getRelationshipType());
        assertEquals("id-1", correlations.getFirst().getSourceId());
        assertEquals("id-2", correlations.getFirst().getTargetId());
        assertEquals(Duration.ofSeconds(10L), correlations.getFirst().getTimeDifference());
    }

    @Test
    public void testSameException() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|new database exception", "new database exception", 5, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|database exception", "database exception", 4, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    @Test
    public void testSameMessage() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 11, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "Exception", "Exception|database exception", "database exception", 1, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    @Test
    public void testNullMessage() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|", null, 2, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "Exception", "Exception|database exception", "database exception", 10, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    @Test
    public void testNullException() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", null, "|database exception", null, 6, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("auth", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "Exception", "Exception|database exception", "database exception", 2, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }


    @Test
    public void testPrecedes() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 7, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("authentication", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 6, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertFalse(correlations.isEmpty());
        assertEquals(CorrelationType.PRECEDES, correlations.getFirst().getRelationshipType());
        assertEquals("id-1", correlations.getFirst().getSourceId());
        assertEquals("id-2", correlations.getFirst().getTargetId());
        assertEquals(Duration.ofSeconds(10L), correlations.getFirst().getTimeDifference());
    }

    @Test
    public void testPrecedesThirtySeconds() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 8, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("authentication", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 6, Instant.ofEpochSecond(12345678930L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertFalse(correlations.isEmpty());
        assertEquals(CorrelationType.PRECEDES, correlations.getFirst().getRelationshipType());
        assertEquals("id-1", correlations.getFirst().getSourceId());
        assertEquals("id-2", correlations.getFirst().getTargetId());
        assertEquals(Duration.ofSeconds(30L), correlations.getFirst().getTimeDifference());
    }

    @Test
    public void testNotPrecedes() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 4, Instant.ofEpochSecond(12345678900L), Instant.ofEpochSecond(12345678901L), Set.of("authentication", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 7, Instant.ofEpochSecond(12345678931L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    @Test
    public void testPrecedesReverseOrder() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678921L), Set.of("authentication", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 12, Instant.ofEpochSecond(12345678910L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertFalse(correlations.isEmpty());
        assertEquals(CorrelationType.PRECEDES, correlations.getFirst().getRelationshipType());
        assertEquals("id-2", correlations.getFirst().getSourceId());
        assertEquals("id-1", correlations.getFirst().getTargetId());
        assertEquals(Duration.ofSeconds(10L), correlations.getFirst().getTimeDifference());
    }

    @Test
    public void testPrecedesSameTimestamp() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678921L), Set.of("authentic", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678921L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }

    @Test
    public void testRelatedSameThreads() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678901L), Set.of("authentication", "validation"), Set.of("auth-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678911L), Set.of("authentication", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertFalse(correlations.isEmpty());
        assertEquals(CorrelationType.RELATED_ERROR, correlations.getFirst().getRelationshipType());
        assertEquals("id-2", correlations.getFirst().getSourceId());
        assertEquals("id-1", correlations.getFirst().getTargetId());
    }

    @Test
    public void testRelatedSameLogger() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678921L), Set.of("authentication", "validation"), Set.of("authentication-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678921L), Set.of("authenticatio", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertFalse(correlations.isEmpty());
        assertEquals(CorrelationType.RELATED_ERROR, correlations.getFirst().getRelationshipType());
        assertEquals("id-2", correlations.getFirst().getSourceId());
        assertEquals("id-1", correlations.getFirst().getTargetId());
    }

    @Test
    public void testNotRelatedSameLogger() {
        ErrorGroup errorGroup1 = createErrorGroup("id-1", "DatabaseException", "DatabaseException|database exception", "database exception", 12, Instant.ofEpochSecond(12345678980L), Instant.ofEpochSecond(12345678981L), Set.of("authentication", "validation"), Set.of("authentication-logger", "validate-logger"));
        ErrorGroup errorGroup2 = createErrorGroup("id-2", "DatabaseException", "DatabaseException|new database exception", "new database exception", 12, Instant.ofEpochSecond(12345678920L), Instant.ofEpochSecond(12345678921L), Set.of("authenticatio", "validate"), Set.of("authentication-logger", "validation-logger"));
        ErrorAnalysisResult errorAnalysisResult = new ErrorAnalysisResult();
        errorAnalysisResult.setErrorGroups(List.of(errorGroup1, errorGroup2));
        List<Correlation> correlations = defaultCorrelationAnalyzer.analyze(errorAnalysisResult);
        assertTrue(correlations.isEmpty());
    }


}
