package com.logparser.parser;

import com.logparser.model.ApplicationLogEntry;
import com.logparser.model.LogEntry;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ApplicationLogParser implements LogParser {
    @Override
    public LogEntry parse(String logLine) {
        Map<String, String> attributes = parseAttributes(logLine);
        Instant timestamp = Instant.parse(attributes.get("timestamp"));
        String host = attributes.get("host");
        return new ApplicationLogEntry(timestamp, host, attributes);
    }

    @Override
    public boolean canParse(String logLine) {
        return logLine.contains("level=") && logLine.contains("message=");
    }

    private Map<String, String> parseAttributes(String logLine) {
        Map<String, String> attributes = new HashMap<>();
        String[] parts = logLine.split(" ");
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                attributes.put(keyValue[0], keyValue[1]);
            }
        }
        return attributes;
    }
} 