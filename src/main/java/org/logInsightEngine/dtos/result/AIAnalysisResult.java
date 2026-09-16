package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAnalysisResult {
    String summary;
    String probableRootCause;
    List<String> contributingFactors;
    List<String> affectedComponents;
    List<String> recommendedInvestigations;
    List<String> caveats;
}
