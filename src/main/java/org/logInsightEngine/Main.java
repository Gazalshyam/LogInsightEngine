package org.logInsightEngine;

import org.logInsightEngine.document.ocr.OCRProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
@EnableConfigurationProperties(OCRProperties.class)
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}