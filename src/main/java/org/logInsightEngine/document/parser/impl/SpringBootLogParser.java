package org.logInsightEngine.document.parser.impl;

import io.micrometer.common.util.StringUtils;
import org.logInsightEngine.document.parser.AbstractLogParser;
import org.logInsightEngine.model.domain.LogEntry;
import org.logInsightEngine.model.domain.LogLevel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SpringBootLogParser extends AbstractLogParser {
    private static final Pattern LOG_HEADER_PATTERN = Pattern.compile(
            "^(?<timestamp>\\S+)\\s+" +
                    "(?<level>TRACE|DEBUG|INFO|WARN|ERROR)\\s+" +
                    "\\d+\\s+---\\s+" +
                    "\\[(?<thread>[^\\]]+)]\\s+" +
                    "(?<logger>[\\w.$]+)\\s+:\\s+" +
                    "(?<message>.*)$");

    @Override
    public boolean supports(String line) {
        return LOG_HEADER_PATTERN.matcher(line).matches();
    }

    @Override
    protected boolean isStartOfLogEntry(String line) {
        return supports(line);
    }


    @Override
    protected LogEntry parseLogEntryHeader(String line) {

        if (StringUtils.isBlank(line)) {
            return null;
        }

        Matcher matcher = LOG_HEADER_PATTERN.matcher(line);

        if (!matcher.matches()) {
            // malformed header
            return null;
        }
        LogEntry entry = LogEntry.builder().build();
        entry.setTimestamp(parseTimestamp(matcher.group("timestamp")));
        entry.setLevel(parseLogLevel(matcher.group("level")));
        entry.setLogger(matcher.group("logger"));
        entry.setThread(matcher.group("thread"));
        entry.setMessage(matcher.group("message"));
        return entry;
    }

    @Override
    protected void handleContinuationLine(LogEntry currentEntry, String line) {
        String trimmedLine = StringUtils.isNotBlank(line) ? line.trim() : line;
        if (isStackTrace(trimmedLine)) {
            currentEntry.appendToStackTrace(line);
        } else {
            currentEntry.appendToMessage(line);
        }
    }

    private Instant parseTimestamp(String line) {
        try {
            return Instant.parse(line);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LogLevel parseLogLevel(String line) {
        return LogLevel.valueOf(line);
    }

    private boolean isStackTrace(String line) {
        return line.startsWith("at ") || line.startsWith("Caused by: ") || line.startsWith("Suppressed:") || line.startsWith("...") || line.matches("^[a-zA-Z_$][\\w$.]*(Exception|Error)(:.*)?$");
    }

}
