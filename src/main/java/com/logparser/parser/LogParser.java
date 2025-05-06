package com.logparser.parser;

import com.logparser.model.LogEntry;

public interface LogParser {
    LogEntry parse(String logLine);
    boolean canParse(String logLine);
} 