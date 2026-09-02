package org.logInsightEngine.dtos.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalysisResult {

    private Summary summary;

    private SeverityStatistics severityStatistics;

    private List<Finding> findings;

    private List<ErrorGroup> errorGroups;

    private List<Correlation> correlations;

    private Impact impact;

    private Timeline timeline;
}