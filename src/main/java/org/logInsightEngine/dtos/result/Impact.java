package org.logInsightEngine.dtos.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Impact {

    private Set<String> threads;

    private Set<String> loggers;

    private Set<String> services;

    public static Impact createImpact() {
        Impact impact = new Impact();
        impact.setThreads(new HashSet<>());
        impact.setLoggers(new HashSet<>());
        impact.setServices(new HashSet<>());
        return impact;
    }
}