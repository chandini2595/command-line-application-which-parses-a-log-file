package com.logparser.model;

import java.time.Instant;
import java.util.Map;

public class ApplicationLogEntry extends LogEntry {
    private String level;
    private String message;

    public ApplicationLogEntry(Instant timestamp, String host, Map<String, String> attributes) {
        super(timestamp, host, attributes);
        this.level = attributes.get("level");
        this.message = attributes.get("message");
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getType() {
        return "APPLICATION";
    }
} 