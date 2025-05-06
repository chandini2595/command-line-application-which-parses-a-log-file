package com.logparser.model;

import java.time.Instant;
import java.util.Map;

public class RequestLogEntry extends LogEntry {
    private String requestMethod;
    private String requestUrl;
    private int responseStatus;
    private int responseTimeMs;

    public RequestLogEntry(Instant timestamp, String host, Map<String, String> attributes) {
        super(timestamp, host, attributes);
        this.requestMethod = attributes.get("request_method").replaceAll("\"", "");
        this.requestUrl = attributes.get("request_url").replaceAll("\"", "");
        this.responseStatus = Integer.parseInt(attributes.get("response_status").replaceAll("\"", ""));
        this.responseTimeMs = Integer.parseInt(attributes.get("response_time_ms").replaceAll("\"", ""));
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public int getResponseTimeMs() {
        return responseTimeMs;
    }

    @Override
    public String getType() {
        return "REQUEST";
    }
} 