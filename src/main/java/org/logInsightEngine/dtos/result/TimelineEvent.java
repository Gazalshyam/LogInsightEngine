package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logInsightEngine.model.domain.LogLevel;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimelineEvent {

    private Instant timestamp;

    private LogLevel level;

    private String title;

    private String description;

    private String findingId;
}
