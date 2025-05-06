package com.logparser.model;

import java.time.Instant;
import java.util.Map;

public class ApmLogEntry extends LogEntry {
    private String metric;
    private double value;

    public ApmLogEntry(Instant timestamp, String host, Map<String, String> attributes) {
        super(timestamp, host, attributes);
        this.metric = attributes.get("metric");
        String valueStr = attributes.get("value");
        if (valueStr != null) {
            valueStr = valueStr.replaceAll("\"", "");
            this.value = Double.parseDouble(valueStr);
        }
    }

    public String getMetric() {
        return metric;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "APM";
    }
} 