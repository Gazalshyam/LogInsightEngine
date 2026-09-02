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
public class ErrorAnalysisResult {
    private List<ErrorGroup> errorGroups;
    private List<Finding> findings;
}
