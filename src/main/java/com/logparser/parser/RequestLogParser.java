package com.logparser.parser;

import com.logparser.model.LogEntry;
import com.logparser.model.RequestLogEntry;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RequestLogParser implements LogParser {
    @Override
    public LogEntry parse(String logLine) {
        Map<String, String> attributes = parseAttributes(logLine);
        Instant timestamp = Instant.parse(attributes.get("timestamp"));
        String host = attributes.get("host");
        return new RequestLogEntry(timestamp, host, attributes);
    }

    @Override
    public boolean canParse(String logLine) {
        return logLine.contains("request_method=") && logLine.contains("request_url=") 
               && logLine.contains("response_status=") && logLine.contains("response_time_ms=");
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