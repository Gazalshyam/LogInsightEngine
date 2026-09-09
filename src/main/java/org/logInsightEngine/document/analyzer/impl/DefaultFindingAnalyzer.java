package org.logInsightEngine.document.analyzer.impl;

import org.logInsightEngine.document.analyzer.AnalyzerUtils;
import org.logInsightEngine.document.analyzer.FindingAnalyzer;
import org.logInsightEngine.dtos.result.ErrorGroup;
import org.logInsightEngine.dtos.result.Finding;
import org.logInsightEngine.dtos.result.FindingCategory;
import org.logInsightEngine.dtos.result.PriorityLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DefaultFindingAnalyzer implements FindingAnalyzer {
    @Override
    public List<Finding> analyze(List<ErrorGroup> errorGroups) {
        Objects.requireNonNull(errorGroups,"errorGroups cannot be null");
        List<Finding> findings = new ArrayList<>();
        if(errorGroups.isEmpty() ){
            return findings;
        }
        for (ErrorGroup errorGroup : errorGroups) {
            Objects.requireNonNull(errorGroup,"errorGroup cannot be null");
            Finding finding = createFinding(errorGroup);
            findings.add(finding);
        }
        return findings;
    }

    private Finding createFinding(ErrorGroup errorGroup) {
        if (isDatabaseIssue(errorGroup)) {
            return createDatabaseFinding(errorGroup);
        }
        if (isClientAbort(errorGroup)) {
            return createClientAbortFinding(errorGroup);
        }
        if (isTimeout(errorGroup)) {
            return createTimeoutFinding(errorGroup);
        }
        return createGenericFinding(errorGroup);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean isDatabaseIssue(ErrorGroup errorGroup) {
    String message = errorGroup.getMessage();
    return (hasExceptionType(errorGroup,"SQLTransientConnectionException") || hasExceptionType( errorGroup, "SQLNonTransientConnectionException") || hasExceptionType(errorGroup,"SQLRecoverableException") || hasExceptionType(errorGroup,"SQLException") || containsIgnoreCase(message, "jdbc") || containsIgnoreCase(message, "hikari")|| containsIgnoreCase(message, "connection pool")|| containsIgnoreCase(message,"unable to acquire jdbc connection" ));
    }

    private boolean isClientAbort(ErrorGroup errorGroup) {
        String message = errorGroup.getMessage();
        return hasExceptionType(errorGroup, "ClientAbortException") || containsIgnoreCase(message, "Broken pipe") || containsIgnoreCase(message, "Connection reset by peer") || containsIgnoreCase(message, "Connection reset");
    }
    private boolean isTimeout(ErrorGroup errorGroup) {
        String message = errorGroup.getMessage();
        return containsIgnoreCase(message, "Connection timed out") || containsIgnoreCase(message, "Read timed out") || hasExceptionType(errorGroup, "SocketTimeoutException") || containsIgnoreCase(message, "Request timeout") || hasExceptionType(errorGroup, "TimeoutException");
    }

    private Finding createBaseFinding(ErrorGroup errorGroup) {
        Finding finding = new Finding();
        finding.setId(AnalyzerUtils.generateUniqueId());
        finding.setOccurrenceCount(errorGroup.getOccurrenceCount());
        finding.setFirstOccurrence(errorGroup.getFirstOccurrence());
        finding.setLastOccurrence(errorGroup.getLastOccurrence());
        finding.setImpact(errorGroup.getImpact());
        finding.setRelatedErrorGroupIds(List.of(errorGroup.getId()));
        return finding;
    }

    private Finding createDatabaseFinding(ErrorGroup errorGroup) {

        Finding finding = createBaseFinding(errorGroup);

        finding.setPriorityLevel(PriorityLevel.HIGH);
        finding.setCategory(FindingCategory.ERROR);
        finding.setTitle("Database connection failure");
        finding.setDescription("Database-related failures were detected while processing the application logs.");

        return finding;
    }

    private Finding createClientAbortFinding(ErrorGroup errorGroup) {

        Finding finding = createBaseFinding(errorGroup);

        finding.setPriorityLevel(PriorityLevel.LOW);
        finding.setCategory(FindingCategory.LIKELY_BENIGN);
        finding.setTitle("Client disconnected during request");
        finding.setDescription("Client-side aborts were detected while processing the application logs.");
        return finding;
    }

    private Finding createTimeoutFinding(ErrorGroup errorGroup) {

        Finding finding = createBaseFinding(errorGroup);

        finding.setPriorityLevel(PriorityLevel.HIGH);
        finding.setCategory(FindingCategory.ERROR);
        finding.setTitle("External service timeout");
        finding.setDescription("Request timeouts were detected while processing the application logs.");
        return finding;
    }

    private Finding createGenericFinding(ErrorGroup errorGroup) {

        Finding finding = createBaseFinding(errorGroup);

        finding.setPriorityLevel(PriorityLevel.MEDIUM);
        finding.setCategory(FindingCategory.ERROR);
        finding.setTitle("Application error detected");
        finding.setDescription("A generic error was detected while processing the application logs.");

        return finding;
    }
    private boolean hasExceptionType(ErrorGroup errorGroup, String exceptionName) {
        String exceptionType = errorGroup.getExceptionType();
        return exceptionType != null && exceptionType.endsWith(exceptionName);
        }

}
