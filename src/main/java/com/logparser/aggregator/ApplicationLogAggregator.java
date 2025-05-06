package com.logparser.aggregator;

import com.logparser.model.ApplicationLogEntry;
import com.logparser.model.LogEntry;

import java.util.HashMap;
import java.util.Map;

public class ApplicationLogAggregator implements LogAggregator {
    private Map<String, Integer> levelCounts = new HashMap<>();

    @Override
    public void aggregate(LogEntry entry) {
        if (entry instanceof ApplicationLogEntry) {
            ApplicationLogEntry appEntry = (ApplicationLogEntry) entry;
            levelCounts.merge(appEntry.getLevel(), 1, Integer::sum);
        }
    }

    @Override
    public Object getResult() {
        return new HashMap<>(levelCounts);
    }

    @Override
    public void reset() {
        levelCounts.clear();
    }
} 