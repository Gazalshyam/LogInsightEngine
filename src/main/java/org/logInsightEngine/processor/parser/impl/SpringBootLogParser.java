package org.logInsightEngine.processor.parser.impl;

import io.micrometer.common.util.StringUtils;
import org.logInsightEngine.model.LogEntry;
import org.logInsightEngine.model.LogLevel;
import org.logInsightEngine.processor.parser.AbstractLogParser;
import org.springframework.stereotype.Component;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Component
public class SpringBootLogParser extends AbstractLogParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootLogParser.class);

    @Override
    public boolean supports(String message) {
        return false;
    }

    @Override
    protected boolean isStartOfLogEntry(String line) {
        try {
            int firstWhitespace = line.indexOf(' ');
            if (firstWhitespace == -1) {
                return false; // Entire line is one token or malformed
            }
            if (StringUtils.isBlank(line))
                return false;
            String firstToken = line.substring(0, firstWhitespace);
            Instant.parse(firstToken);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    @Override
    protected LogEntry parseLogEntryHeader(String line) {

        if (StringUtils.isBlank(line)) {
            return null;
        }

        String[] parts = line.split("\\s+");

        int separatorIndex = findMessageSeparator(parts);
        String validationError = validateHeader(parts, separatorIndex);

        if (validationError != null) {
            LOGGER.warn("Skipping malformed Spring Boot log header. Reason: {}. Line: {}", validationError, line);
            return null;
        }
        LogEntry entry = new LogEntry();

        entry.setTimestamp(getTimeStamp(parts[0]));
        entry.setLevel(getLogLevel(parts[1]));
        entry.setLogger(getLogger(parts, separatorIndex));
        entry.setThread(getThread(parts, separatorIndex));
        entry.setMessage(getMessage(parts, separatorIndex));

        return entry;
    }

    @Override
    protected void handleContinuationLine(LogEntry currentEntry, String line) {
        if (line.startsWith("at") || line.startsWith("Caused by: ") || line.startsWith("Suppressed") || line.startsWith("...")) {
            currentEntry.appendToStackTrace(line);
        } else {
            currentEntry.appendToMessage(line);
        }
    }

    private String validateHeader(String[] parts, int separatorIndex) {

        if (parts.length < 2) {
            return "Expected timestamp and log level.";
        }

        if (separatorIndex == -1) {
            return "Missing ':' separator.";
        }

        if (separatorIndex >= parts.length - 1) {
            return "Missing log message after ':' separator.";
        }

        return null;
    }

    private Instant getTimeStamp(String line) {
        try {
            return Instant.parse(line);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LogLevel getLogLevel(String line) {
        try {
            return LogLevel.valueOf(line);
        } catch (IllegalArgumentException e) {
            return LogLevel.UNKNOWN;
        }
    }

    private String getLogger(String[] parts, int separatorIndex) {
        return parts[separatorIndex - 1];
    }

    private String getThread(String[] parts, int separatorIndex) {

        for (int i = separatorIndex; i >= 0; i--) {
            if (parts[i].startsWith("[") && parts[i].endsWith("]") && parts[i].length() > 2) {
                return parts[i].substring(1, parts[i].length() - 1);
            }
        }

        return "";
    }

    private int findMessageSeparator(String[] parts) {
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(":")) return i;
        }
        return -1;
    }

    private String getMessage(String[] parts, int separatorIndex) {
        return String.join(" ", Arrays.copyOfRange(parts, separatorIndex + 1, parts.length));
    }
}
