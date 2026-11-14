package org.logInsightEngine.document.analyzer;

import org.logInsightEngine.dtos.result.ErrorGroup;
import org.logInsightEngine.dtos.result.Finding;

import java.util.List;

public interface FindingAnalyzer {
    List<Finding> analyze(List<ErrorGroup> document);
}
