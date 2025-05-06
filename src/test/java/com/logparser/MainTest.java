package com.logparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @TempDir
    Path tempDir;
    private File inputFile;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        // Create test log file
        inputFile = tempDir.resolve("test.log").toFile();
        String testLog = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72\n" +
                "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Scheduled maintenance starting\" host=webserver1\n" +
                "timestamp=2024-02-24T16:22:25Z request_method=POST request_url=\"/api/update\" response_status=202 response_time_ms=200 host=webserver1\n" +
                "timestamp=2024-02-24T16:22:30Z metric=memory_usage_percent host=webserver1 value=85\n" +
                "timestamp=2024-02-24T16:22:35Z level=ERROR message=\"Update process failed\" error_code=5012 host=webserver1\n" +
                "timestamp=2024-02-24T16:22:40Z request_method=GET request_url=\"/api/status\" response_status=200 response_time_ms=100 host=webserver1\n";
        Files.write(inputFile.toPath(), testLog.getBytes());
        
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLogProcessing() throws IOException {
        // Run the main program with output directory specified
        Main.main(new String[]{"--file", inputFile.getAbsolutePath(), "--output-dir", tempDir.toString()});

        // Verify APM logs
        File apmFile = tempDir.resolve("apm.json").toFile();
        assertTrue(apmFile.exists());
        Map<String, Map<String, Object>> apmResults = objectMapper.readValue(apmFile, Map.class);
        assertTrue(apmResults.containsKey("cpu_usage_percent"), "Should contain CPU usage metric");
        assertTrue(apmResults.containsKey("memory_usage_percent"), "Should contain memory usage metric");
        
        Map<String, Object> cpuStats = apmResults.get("cpu_usage_percent");
        assertEquals(72.0, cpuStats.get("average"));
        assertEquals(72.0, cpuStats.get("median"));
        assertEquals(72.0, cpuStats.get("minimum"));
        assertEquals(72.0, cpuStats.get("max"));

        Map<String, Object> memoryStats = apmResults.get("memory_usage_percent");
        assertEquals(85.0, memoryStats.get("average"));
        assertEquals(85.0, memoryStats.get("median"));
        assertEquals(85.0, memoryStats.get("minimum"));
        assertEquals(85.0, memoryStats.get("max"));

        // Verify Application logs
        File appFile = tempDir.resolve("application.json").toFile();
        assertTrue(appFile.exists());
        Map<String, Integer> appResults = objectMapper.readValue(appFile, Map.class);
        assertEquals(1, appResults.get("INFO"));
        assertEquals(1, appResults.get("ERROR"));

        // Verify Request logs
        File requestFile = tempDir.resolve("request.json").toFile();
        assertTrue(requestFile.exists());
        Map<String, Object> requestResults = objectMapper.readValue(requestFile, Map.class);
        assertTrue(requestResults.containsKey("/api/update"));
        assertTrue(requestResults.containsKey("/api/status"));

        // Clean up
        apmFile.delete();
        appFile.delete();
        requestFile.delete();
    }
} 