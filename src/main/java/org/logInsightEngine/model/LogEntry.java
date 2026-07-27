package org.logInsightEngine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEntry {
    private Instant timestamp;

    private LogLevel level;

    private String logger;

    private String thread;

    private String message;

    private String stackTrace;

    public void appendToMessage(String lineString) {
        if(this.message==null) {
            this.message = lineString;
        }else{
            this.message += System.lineSeparator() + lineString;
        }
    }
    public  void appendToStackTrace(String lineString) {
        if(this.stackTrace == null) {
            this.stackTrace = lineString;
        } else {
            this.stackTrace += System.lineSeparator() + lineString;
        }
    }

}
