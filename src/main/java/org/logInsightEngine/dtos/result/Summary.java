package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    private long totalLogEntries;
    private Instant firstTimestamp;
    private Instant lastTimestamp;
    private Duration duration;
}
