package org.logInsightEngine.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtractedDocument {

    private String fileName;

    private String sourceType;

    private String content;

    private long size;

    private long lineCount;


}

