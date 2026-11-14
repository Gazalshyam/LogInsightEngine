package org.logInsightEngine.document.analyzer.impl;

import org.logInsightEngine.document.analyzer.AnalyzerUtils;
import org.logInsightEngine.document.analyzer.CorrelationAnalyzer;
import org.logInsightEngine.dtos.result.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class DefaultCorrelationAnalyzer implements CorrelationAnalyzer {
    private static final Duration CORRELATION_WINDOW = Duration.ofSeconds(30);

    @Override
    public List<Correlation> analyze(ErrorAnalysisResult errorAnalysisResult) {
        List<Correlation> correlations = new ArrayList<>();
        List<ErrorGroup> errorGroups = errorAnalysisResult.getErrorGroups();

        Objects.requireNonNull(errorGroups, "Require non null errorGroups");
        for (int i = 0; i < errorGroups.size(); i++) {
            ErrorGroup source = errorGroups.get(i);
            for (int j = i + 1; j < errorGroups.size(); j++) {
                ErrorGroup related = errorGroups.get(j);
                CorrelationType relationType = getRelationType(source, related);
                if (relationType != null) {
                    correlations.add(createCorrelation(source, related, relationType));
                }

            }
        }
        return correlations;
    }

    private CorrelationType getRelationType(ErrorGroup source, ErrorGroup related) {

        boolean hasSharedThread = false;
        if (source.getImpact() != null && source.getImpact().getThreads() != null) {
            hasSharedThread = source.getImpact().getThreads().stream().anyMatch(related.getImpact().getThreads()::contains);
        }
        boolean hasSharedLogger = false;
        if (source.getImpact() != null && source.getImpact().getLoggers() != null) {
            hasSharedLogger = source.getImpact().getLoggers().stream().anyMatch(related.getImpact().getLoggers()::contains);
        }
        Duration timeDifference = getTimeDifference(source.getFirstOccurrence(), related.getFirstOccurrence());
        boolean hasSharedContext = hasSharedLogger || hasSharedThread;
        boolean sourcePrecedesRelated = timeDifference != null && !timeDifference.isZero() && !timeDifference.isNegative() && timeDifference.compareTo(CORRELATION_WINDOW) <= 0 && timeDifference.compareTo(Duration.ZERO) > 0;
        if (isSimilar(source, related)) {
            return CorrelationType.SIMILAR;
        }
        if (hasSharedContext && sourcePrecedesRelated) {
            return CorrelationType.PRECEDES;
        }
        if (isRelated(hasSharedContext, timeDifference)) {
            return CorrelationType.RELATED_ERROR;
        }

        return null;
    }

    private Duration getTimeDifference(Instant sourceTime, Instant relatedTime) {
        if (sourceTime != null && relatedTime != null) {
            if (sourceTime.isBefore(relatedTime))
                return Duration.between(sourceTime, relatedTime);
            else
                return Duration.between(relatedTime, sourceTime);
        }
        return null;
    }

    private Correlation createCorrelation(ErrorGroup source, ErrorGroup target, CorrelationType relationshipType) {
        Correlation correlation = new Correlation();
        correlation.setId(UUID.randomUUID().toString());
        correlation.setSourceType(CorrelationEntityType.ERROR_GROUP);
        if (source.getFirstOccurrence().isBefore(target.getFirstOccurrence())) {
            correlation.setSourceId(source.getId());
            correlation.setTargetId(target.getId());
        } else {
            correlation.setSourceId(target.getId());
            correlation.setTargetId(source.getId());
        }
        correlation.setTargetType(CorrelationEntityType.ERROR_GROUP);
        correlation.setRelationshipType(relationshipType);
        correlation.setTimeDifference(getTimeDifference(source.getFirstOccurrence(), target.getFirstOccurrence()));
        return correlation;
    }

    private boolean isSimilar(ErrorGroup source, ErrorGroup related) {
        if (source.getExceptionType() == null || related.getExceptionType() == null) {
            return false;
        }
        return AnalyzerUtils.normalize(source.getExceptionType()).equalsIgnoreCase(AnalyzerUtils.normalize(related.getExceptionType()))
                && AnalyzerUtils.normalize(source.getMessage()).equalsIgnoreCase(AnalyzerUtils.normalize(related.getMessage()));
    }

    private boolean isRelated(boolean hasSharedContext, Duration timeDifference) {
        return hasSharedContext && timeDifference != null && timeDifference.compareTo(CORRELATION_WINDOW) <= 0 && timeDifference.compareTo(Duration.ZERO) >= 0;
    }
}
