package org.logInsightEngine.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeInstantAsIsoString() throws Exception {

        String json = objectMapper.writeValueAsString(Instant.parse("2026-09-19T10:00:00Z"));
        assertEquals( "\"2026-09-19T10:00:00Z\"", json);
    }
}
