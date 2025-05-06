package com.logparser.aggregator;

import com.logparser.model.LogEntry;

public interface LogAggregator {
    void aggregate(LogEntry entry);
    Object getResult();
    void reset();
} 