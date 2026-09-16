package org.logInsightEngine.dtos.ai_context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logInsightEngine.dtos.result.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAnalysisContext {

        private Summary summary;
        private SeverityStatistics severityStatistics;
        private List<ErrorGroup> errorGroups;
        private List<Finding> findings;
        private List<Correlation> correlations;
        private Timeline timeline;
    }

