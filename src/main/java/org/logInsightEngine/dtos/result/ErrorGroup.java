package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorGroup {

    private String id;

    private String fingerprint;

    private String exceptionType;

    private String message;

    private long occurrenceCount;

    private Instant firstOccurrence;

    private Instant lastOccurrence;

    private Impact impact;
}
