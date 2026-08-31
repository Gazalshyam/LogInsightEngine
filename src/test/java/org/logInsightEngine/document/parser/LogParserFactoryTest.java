package org.logInsightEngine.document.parser;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.document.parser.impl.ApacheLogParser;
import org.logInsightEngine.document.parser.impl.NginxLogParser;
import org.logInsightEngine.document.parser.impl.SpringBootLogParser;
import org.logInsightEngine.exception.UnsupportedLogFormatException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogParserFactoryTest {
    private final LogParserFactory logParserFactory = new LogParserFactory(List.of(new SpringBootLogParser(), new ApacheLogParser(), new NginxLogParser()));

    @Test
    public void testGetParserWithNullContent() {
        assertThrows(IllegalArgumentException.class, () -> logParserFactory.getParser(null));
    }

    @Test
    public void testParserWithSpringBootLogs() {

        String springBootLog = """
                2023-06-01T12:00:00.000Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Starting DemoApplication using Java 11 on my-machine with PID 12345 (/path/to/demo.jar started by user in /path/to)
                2023-06-01T12:00:00.001Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : No active profile set, falling back to default profiles: default
                2023-06-01T12:00:00.002Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 2.345 seconds (JVM running for 3.456)
                2023-06-01T12:00:01.000Z  INFO 12345 --- [           main] com.example.demo.config.DatabaseConfig   : Initializing database configuration
                2023-06-01T12:00:01.015Z  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
                2023-06-01T12:00:01.125Z  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
                2023-06-01T12:00:01.200Z  INFO 12345 --- [           main] com.example.demo.repository.UserRepository : Database connection established
                2023-06-01T12:00:01.350Z  INFO 12345 --- [           main] org.springframework.web.servlet.DispatcherServlet : Initializing Servlet 'dispatcherServlet'
                2023-06-01T12:00:01.351Z  INFO 12345 --- [           main] org.springframework.web.servlet.DispatcherServlet : Completed initialization in 1 mss
                2023-06-01T12:00:02.000Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.controller.UserController : Received request to fetch users
                2023-06-01T12:00:02.015Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.service.UserService : Fetching users from database
                2023-06-01T12:00:02.050Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.repository.UserRepository : Retrieved 25 users
                2023-06-01T12:00:02.060Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.service.UserService : Successfully processed user request
                2023-06-01T12:00:03.000Z  WARN 12345 --- [http-nio-8080-exec-2] com.example.demo.service.UserService : User request exceeded expected response time
                2023-06-01T12:00:03.250Z ERROR 12345 --- [http-nio-8080-exec-3] com.example.demo.controller.UserController : Failed to process user request
                2023-06-01T12:00:03.251Z  INFO 12345 --- [http-nio-8080-exec-3] com.example.demo.service.ErrorService : Preparing error response for client
                2023-06-01T12:00:03.300Z DEBUG 12345 --- [http-nio-8080-exec-3] com.example.demo.service.ErrorService : Error response generated successfully
                2023-06-01T12:00:04.000Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Application health check completed
                2023-06-01T12:00:05.000Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Application shutdown hook registered
                """;

        LogParser parser = logParserFactory.getParser(springBootLog);
        assert parser instanceof SpringBootLogParser;
    }
    @Test
    public void testParserWithSpringBootLogsLessThan20() {

        String springBootLog = """
                2023-06-01T12:00:00.000Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Starting DemoApplication using Java 11 on my-machine with PID 12345 (/path/to/demo.jar started by user in /path/to)
                2023-06-01T12:00:00.001Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : No active profile set, falling back to default profiles: default
                2023-06-01T12:00:00.002Z  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 2.345 seconds (JVM running for 3.456)
                2023-06-01T12:00:01.000Z  INFO 12345 --- [           main] com.example.demo.config.DatabaseConfig   : Initializing database configuration
                2023-06-01T12:00:01.015Z  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
                2023-06-01T12:00:01.125Z  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
                2023-06-01T12:00:01.200Z  INFO 12345 --- [           main] com.example.demo.repository.UserRepository : Database connection established
                2023-06-01T12:00:01.350Z  INFO 12345 --- [           main] org.springframework.web.servlet.DispatcherServlet : Initializing Servlet 'dispatcherServlet'
                2023-06-01T12:00:01.351Z  INFO 12345 --- [           main] org.springframework.web.servlet.DispatcherServlet : Completed initialization in 1 mss
                2023-06-01T12:00:02.000Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.controller.UserController : Received request to fetch users
                2023-06-01T12:00:02.015Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.service.UserService : Fetching users from database
                2023-06-01T12:00:02.050Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.repository.UserRepository : Retrieved 25 users
                2023-06-01T12:00:02.060Z  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.service.UserService : Successfully processed user request
                """;

        LogParser parser = logParserFactory.getParser(springBootLog);
        assert parser instanceof SpringBootLogParser;
    }


    @Test
    public void testParserWithUnsupportedLogs() {
        String unsupportedLog = """
                This is an unsupported log format.
                It does not match any known log patterns.
                """;

        assertThrows(UnsupportedLogFormatException.class, () -> logParserFactory.getParser(unsupportedLog));

    }

    @Test
    public void testParserWithEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> logParserFactory.getParser(""));
    }

    @Test
    public void testParserWithBlankContent() {
        assertThrows(IllegalArgumentException.class, () -> logParserFactory.getParser("   "));
    }

    @Test
    public void shouldIgnoreContentAfterDetectionWindow() {
        String randomContent = """
                2023-06-01 12:00:00.000  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Starting DemoApplication using Java 11 on my-machine with PID 12345 (/path/to/demo.jar started by user in /path/to)
                2023-06-01 12:00:00.001  INFO 12345 --- [           main] com.example.demo.DemoApplication         : No active profile set, falling back to default profiles: default
                2023-06-01 12:00:00.002  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 2.345 seconds (JVM running for 3.456)
                2023-06-01 12:00:01.000  INFO 12345 --- [           main] com.example.demo.config.DatabaseConfig   : Initializing database configuration
                2023-06-01 12:00:01.015  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
                2023-06-01 12:00:01.125  INFO 12345 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
                2023-06-01 12:00:01.200  INFO 12345 --- [           main] com.example.demo.repository.UserRepository : Database connection established
                2023-06-01 12:00:01.350  INFO 12345 --- [           main] org.springframework.web.servlet.DispatcherServlet : Initializing Servlet 'dispatcherServlet'
                2023-06-01 12:00:01.351  INFO 12345 --- [           main] org.springframework.web.servlet.DispatcherServlet : Completed initialization in 1 ms
                2023-06-01 12:00:02.000  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.controller.UserController : Received request to fetch users
                2023-06-01 12:00:02.015  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.service.UserService : Fetching users from database
                2023-06-01 12:00:02.050  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.repository.UserRepository : Retrieved 25 users
                2023-06-01 12:00:02.060  INFO 12345 --- [http-nio-8080-exec-1] com.example.demo.service.UserService : Successfully processed user request
                2023-06-01 12:00:03.000  WARN 12345 --- [http-nio-8080-exec-2] com.example.demo.service.UserService : User request exceeded expected response time
                2023-06-01 12:00:03.250 ERROR 12345 --- [http-nio-8080-exec-3] com.example.demo.controller.UserController : Failed to process user request
                2023-06-01 12:00:03.251  INFO 12345 --- [http-nio-8080-exec-3] com.example.demo.service.ErrorService : Preparing error response for client
                2023-06-01 12:00:03.300 DEBUG 12345 --- [http-nio-8080-exec-3] com.example.demo.service.ErrorService : Error response generated successfully
                2023-06-01 12:00:04.000  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Application health check completed
                2023-06-01 12:00:05.000  INFO 12345 --- [           main] com.example.demo.DemoApplication         : Application shutdown hook registered
                Random content that does not match any log format.
                Another line of random content.
                """;
        assertThrows(UnsupportedLogFormatException.class, () -> logParserFactory.getParser(randomContent));
    }

}
