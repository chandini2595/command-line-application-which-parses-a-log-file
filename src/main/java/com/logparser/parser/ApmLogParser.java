package com.logparser.parser;

import com.logparser.model.ApmLogEntry;
import com.logparser.model.LogEntry;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ApmLogParser implements LogParser {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    @Override
    public LogEntry parse(String logLine) {
        Map<String, String> attributes = parseAttributes(logLine);
        Instant timestamp = Instant.parse(attributes.get("timestamp"));
        String host = attributes.get("host");
        return new ApmLogEntry(timestamp, host, attributes);
    }

    @Override
    public boolean canParse(String logLine) {
        return logLine.contains("metric=") && logLine.contains("value=");
    }

    private Map<String, String> parseAttributes(String logLine) {
        Map<String, String> attributes = new HashMap<>();
        StringBuilder currentPart = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : logLine.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                currentPart.append(c);
            } else if (c == ' ' && !inQuotes) {
                processPart(currentPart.toString(), attributes);
                currentPart.setLength(0);
            } else {
                currentPart.append(c);
            }
        }
        
        // Process the last part
        if (currentPart.length() > 0) {
            processPart(currentPart.toString(), attributes);
        }
        
        return attributes;
    }

    private void processPart(String part, Map<String, String> attributes) {
        if (!part.trim().isEmpty()) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                attributes.put(keyValue[0], keyValue[1]);
            }
        }
    }
} 