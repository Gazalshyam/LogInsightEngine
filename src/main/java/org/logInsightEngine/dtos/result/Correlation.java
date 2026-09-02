package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Correlation {
    private String id;
    private String sourceId;
    private CorrelationEntityType sourceType;
    private String relatedId;
    private CorrelationEntityType relatedType;
    private CorrelationType relationshipType;
    private Duration timeDifference;
    private double confidence;
}
