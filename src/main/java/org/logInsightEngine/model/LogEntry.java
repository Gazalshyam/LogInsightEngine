package org.logInsightEngine.model;

import java.time.LocalDateTime;

public class LogEntry {
    private LocalDateTime timestamp;

    private LogLevel level;

    private String logger;

    private String thread;

    private String message;

    private String stackTrace;
}
