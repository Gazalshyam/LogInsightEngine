package org.logInsightEngine.dtos.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logInsightEngine.model.domain.LogLevel;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeverityStatistics {
    private Map<LogLevel, Long> countByLevel;
}