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
public class Finding {

    private String id;

    private PriorityLevel priorityLevel;

    private FindingCategory category;

    private String title;

    private String description;

    private long occurrenceCount;

    private Instant firstOccurrence;

    private Instant lastOccurrence;

    private Impact impact;
}