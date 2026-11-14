package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logInsightEngine.model.domain.AnalysisStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalysisExecutionResult {
    private String analysisId;
    private String logFileError;
    private String logFileErrorType;
    private String logDataError;
    private String logDataErrorType;
    private AnalysisStatus status;
}
