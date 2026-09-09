package org.logInsightEngine.document.analyzer.impl;

import org.junit.jupiter.api.Test;
import org.logInsightEngine.dtos.result.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultFindingAnalyzerTest {
    private final DefaultFindingAnalyzer defaultFindingAnalyzer = new DefaultFindingAnalyzer();

    @Test
    public void testNullInput() {
        // Test with null input
        assertThrows(NullPointerException.class, () -> defaultFindingAnalyzer.analyze(null));
    }

    @Test
    public void testEmptyInput() {
        // Test with empty input
        assertEquals(new ArrayList<>(), defaultFindingAnalyzer.analyze(new ArrayList<>()));
    }

    @Test
    public void testNullErrorGroupInsideList(){
        // Test with a list containing a null ErrorGroup
        List<ErrorGroup> errorGroups = new java.util.ArrayList<>();
        errorGroups.add(null);
        assertThrows(NullPointerException.class, () -> defaultFindingAnalyzer.analyze(errorGroups));
    }

    private void setErrorGroupDetails(ErrorGroup errorGroup){
        Impact impact = Impact.createImpact();
        impact.setThreads(Set.of("thread-1"));
        impact.setLoggers(Set.of("com.example.MyClass"));
        impact.setServices(Set.of("my-service"));
        errorGroup.setImpact(impact);
        errorGroup.setFirstOccurrence(Instant.ofEpochMilli(1690000000000L));
        errorGroup.setLastOccurrence(Instant.ofEpochMilli(1690000005000L));
        errorGroup.setOccurrenceCount(1);
        errorGroup.setId("error-group-1");
    }

    private void assertFindingDetails(List<Finding> findings, ErrorGroup errorGroup){
        assertEquals(findings.getFirst().getFirstOccurrence(), Instant.ofEpochMilli(1690000000000L)); // Since we didn't set timestamps in the ErrorGroup
        assertEquals(findings.getFirst().getLastOccurrence(), Instant.ofEpochMilli(1690000005000L)); // Since we didn't set timestamps in the ErrorGroup
        assertNotNull(findings.getFirst().getImpact()); // Impact should be initialized
        assertEquals(findings.getFirst().getImpact().getThreads(), Set.of("thread-1")); // The threads should match the ErrorGroup's impact
        assertEquals(findings.getFirst().getImpact().getLoggers(), Set.of("com.example.MyClass")); // The loggers should match the ErrorGroup's impact
        assertEquals(findings.getFirst().getImpact().getServices(), Set.of("my-service")); // The services should match the ErrorGroup's impact
        assertTrue(findings.getFirst().getRelatedErrorGroupIds().contains(errorGroup.getId()));
        assertEquals(1, findings.getFirst().getOccurrenceCount());
        assertEquals(1, findings.getFirst().getRelatedErrorGroupIds().size());
    }

    @Test
    public void testDatabaseIssueDetection() {
        // Test with a database issue ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("SQLTransientConnectionException: Connection failed");
        errorGroup.setExceptionType("SQLTransientConnectionException");
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.ERROR, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.HIGH, findings.getFirst().getPriorityLevel());
        assertEquals("Database connection failure", findings.getFirst().getTitle());
        assertEquals("Database-related failures were detected while processing the application logs.", findings.getFirst().getDescription());
    }

    @Test
    public void testClientAbortDetection() {
        // Test with a client abort ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("ClientAbortException: Client disconnected");
        errorGroup.setExceptionType("ClientAbortException");
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertEquals(FindingCategory.LIKELY_BENIGN, findings.getFirst().getCategory()); // The related error group IDs should contain the ID of the error group
        assertFindingDetails(findings, errorGroup);
        assertEquals(PriorityLevel.LOW, findings.getFirst().getPriorityLevel());
        assertEquals("Client disconnected during request", findings.getFirst().getTitle());
        assertEquals("Client-side aborts were detected while processing the application logs.", findings.getFirst().getDescription());
    }

    @Test
    public void testTimeoutDetection() {
        // Test with a timeout ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("TimeoutException: received timed out");
        errorGroup.setExceptionType("TimeoutException");
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.ERROR, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.HIGH, findings.getFirst().getPriorityLevel());
        assertEquals("External service timeout", findings.getFirst().getTitle());
        assertEquals("Request timeouts were detected while processing the application logs.", findings.getFirst().getDescription());

    }

    @Test
    public void testDatabaseWithNullExceptionType() {
        // Test with a database issue ErrorGroup with null exception type
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("Unable to acquire JDBC connection");
        errorGroup.setExceptionType(null);
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.ERROR, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.HIGH, findings.getFirst().getPriorityLevel());
        assertEquals("Database connection failure", findings.getFirst().getTitle());
        assertEquals("Database-related failures were detected while processing the application logs.", findings.getFirst().getDescription());
    }


    @Test
    public void testBrokenPipeDetection() {
        // Test with a broken pipe ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("Broken pipe: Connection reset by peer");
        errorGroup.setExceptionType(null);
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.LIKELY_BENIGN, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.LOW, findings.getFirst().getPriorityLevel());
        assertEquals("Client disconnected during request", findings.getFirst().getTitle());
        assertEquals("Client-side aborts were detected while processing the application logs.", findings.getFirst().getDescription());
    }

    @Test
    public void testSocketTimeoutErrorDetection() {
        // Test with a socket timeout ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("SocketTimeoutException: Read timed out");
        errorGroup.setExceptionType("SocketTimeoutException");
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.ERROR, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.HIGH, findings.getFirst().getPriorityLevel());
        assertEquals("External service timeout", findings.getFirst().getTitle());
        assertEquals("Request timeouts were detected while processing the application logs.", findings.getFirst().getDescription());
    }

    @Test
    public void testReadTimeOutDetection() {
        // Test with a read timeout ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("Read timed out");
        errorGroup.setExceptionType(null);
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.ERROR, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.HIGH, findings.getFirst().getPriorityLevel());
        assertEquals("External service timeout", findings.getFirst().getTitle());
        assertEquals("Request timeouts were detected while processing the application logs.", findings.getFirst().getDescription());
    }

    @Test
    public void testGenericErrorDetection() {
        // Test with a generic ErrorGroup
        ErrorGroup errorGroup = new ErrorGroup();
        errorGroup.setMessage("Some generic error message");
        setErrorGroupDetails(errorGroup);
        List<ErrorGroup> errorGroups = List.of(errorGroup);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);
        assertFindingDetails(findings, errorGroup);
        assertEquals(FindingCategory.ERROR, findings.getFirst().getCategory());
        assertEquals(PriorityLevel.MEDIUM, findings.getFirst().getPriorityLevel());
        assertEquals("Application error detected", findings.getFirst().getTitle());
        assertEquals("A generic error was detected while processing the application logs.", findings.getFirst().getDescription());
    }

    @Test
    public void testMultipleErrorGroupsDetection() {
        // Test with multiple ErrorGroups
        ErrorGroup errorGroup1 = new ErrorGroup();
        errorGroup1.setMessage("SQLTransientConnectionException: Connection failed");
        errorGroup1.setExceptionType("SQLTransientConnectionException");
        setErrorGroupDetails(errorGroup1);

        ErrorGroup errorGroup2 = new ErrorGroup();
        errorGroup2.setMessage("ClientAbortException: Client disconnected");
        errorGroup2.setExceptionType("ClientAbortException");
        setErrorGroupDetails(errorGroup2);

        List<ErrorGroup> errorGroups = List.of(errorGroup1, errorGroup2);
        List<Finding> findings = defaultFindingAnalyzer.analyze(errorGroups);

        assertEquals(2, findings.size());
        assertFindingDetails(findings, errorGroup1);
        assertFindingDetails(findings, errorGroup2);
    }


    @Test
    public void testMultipleErrorGroupsDetectionWithNullEntry() {
        // Test with multiple ErrorGroups
        ErrorGroup errorGroup1 = new ErrorGroup();
        errorGroup1.setMessage("SQLTransientConnectionException: Connection failed");
        errorGroup1.setExceptionType("SQLTransientConnectionException");
        setErrorGroupDetails(errorGroup1);

        ErrorGroup errorGroup2 = new ErrorGroup();
        errorGroup2.setMessage("ClientAbortException: Client disconnected");
        errorGroup2.setExceptionType("ClientAbortException");
        setErrorGroupDetails(errorGroup2);
        assertThrows(NullPointerException.class, () -> defaultFindingAnalyzer.analyze( List.of(errorGroup1, null, errorGroup2)));

    }
}
