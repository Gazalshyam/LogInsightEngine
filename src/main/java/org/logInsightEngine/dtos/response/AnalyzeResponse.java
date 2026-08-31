package org.logInsightEngine.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logInsightEngine.model.domain.AnalysisStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyzeResponse {

    private AnalysisStatus status;
    private String analysisId;
    private String logDataError;
    private String logFileError;
    private String logFileErrorType;
    private String logDataErrorType;


}
