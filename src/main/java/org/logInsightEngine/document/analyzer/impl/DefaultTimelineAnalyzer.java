package org.logInsightEngine.document.analyzer.impl;

import org.logInsightEngine.document.analyzer.TimelineAnalyzer;
import org.logInsightEngine.dtos.result.Finding;
import org.logInsightEngine.dtos.result.Timeline;
import org.logInsightEngine.dtos.result.TimelineEvent;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class DefaultTimelineAnalyzer implements TimelineAnalyzer {

    @Override
    public Timeline analyze(List<LogEntry> logEntries, List<Finding> findings) {
        Timeline timeline = new Timeline();
        List<TimelineEvent> timelineEventList = new ArrayList<>();

        Objects.requireNonNull(findings, "Findings can not be null");
        Objects.requireNonNull(logEntries, "LogEntries can not be null");

        for (Finding finding : findings) {
            if (finding == null || finding.getFirstOccurrence() == null) {
                continue;
            }
            TimelineEvent timelineEvent = new TimelineEvent();
            timelineEvent.setDescription(finding.getDescription());
            timelineEvent.setFindingId(finding.getId());
            timelineEvent.setLevel(LogLevel.ERROR);
            timelineEvent.setTimestamp(finding.getFirstOccurrence());
            timelineEvent.setTitle(finding.getTitle());
            timelineEventList.add(timelineEvent);
        }
        for (LogEntry logEntry : logEntries) {
            if (logEntry == null || logEntry.getLevel() == null || logEntry.getTimestamp() == null) {
                continue;
            }
            if (!logEntry.getLevel().equals(LogLevel.INFO) && !logEntry.getLevel().equals(LogLevel.WARN)) {
                continue;
            }
            TimelineEvent timelineEvent = new TimelineEvent();
            timelineEvent.setLevel(logEntry.getLevel());
            timelineEvent.setTimestamp(logEntry.getTimestamp());
            timelineEvent.setTitle(logEntry.getMessage());
            timelineEvent.setDescription(logEntry.getMessage());
            timelineEventList.add(timelineEvent);
        }
        timelineEventList.sort(Comparator.comparing(TimelineEvent::getTimestamp));
        timeline.setEvents(timelineEventList);
        return timeline;
    }

}
