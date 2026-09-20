package org.logInsightEngine.dtos.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIAnalysisResult {
    private String summary;
    private String probableRootCause;
    private List<String> contributingFactors;
    private List<String> affectedComponents;
    private List<String> recommendedInvestigations;
    private List<String> caveats;
}
