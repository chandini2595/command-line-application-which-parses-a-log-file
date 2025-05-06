package com.logparser.model;

import java.time.Instant;
import java.util.Map;

public abstract class LogEntry {
    private Instant timestamp;
    private String host;
    private Map<String, String> attributes;

    public LogEntry(Instant timestamp, String host, Map<String, String> attributes) {
        this.timestamp = timestamp;
        this.host = host;
        this.attributes = attributes;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public abstract String getType();
} 