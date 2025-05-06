package com.logparser.aggregator;

import com.logparser.model.LogEntry;
import com.logparser.model.RequestLogEntry;

import java.util.*;

public class RequestLogAggregator implements LogAggregator {
    private Map<String, RouteStats> routeStats = new HashMap<>();

    private static class RouteStats {
        List<Integer> responseTimes = new ArrayList<>();
        Map<String, Integer> statusCodes = new HashMap<>();

        void addResponseTime(int time) {
            responseTimes.add(time);
        }

        void incrementStatusCode(int statusCode) {
            String category = getStatusCodeCategory(statusCode);
            statusCodes.merge(category, 1, Integer::sum);
        }

        private String getStatusCodeCategory(int statusCode) {
            if (statusCode >= 200 && statusCode < 300) return "2XX";
            if (statusCode >= 400 && statusCode < 500) return "4XX";
            if (statusCode >= 500) return "5XX";
            return "OTHER";
        }

        Map<String, Object> getStats() {
            Map<String, Object> stats = new HashMap<>();
            
            // Response time statistics
            Collections.sort(responseTimes);
            Map<String, Integer> responseTimeStats = new HashMap<>();
            responseTimeStats.put("min", responseTimes.get(0));
            responseTimeStats.put("max", responseTimes.get(responseTimes.size() - 1));
            responseTimeStats.put("50_percentile", getPercentile(50));
            responseTimeStats.put("90_percentile", getPercentile(90));
            responseTimeStats.put("95_percentile", getPercentile(95));
            responseTimeStats.put("99_percentile", getPercentile(99));
            
            stats.put("response_times", responseTimeStats);
            stats.put("status_codes", new HashMap<>(statusCodes));
            
            return stats;
        }

        private int getPercentile(int percentile) {
            int index = (int) Math.ceil(percentile / 100.0 * responseTimes.size()) - 1;
            return responseTimes.get(index);
        }
    }

    @Override
    public void aggregate(LogEntry entry) {
        if (entry instanceof RequestLogEntry) {
            RequestLogEntry requestEntry = (RequestLogEntry) entry;
            RouteStats stats = routeStats.computeIfAbsent(requestEntry.getRequestUrl(), k -> new RouteStats());
            
            stats.addResponseTime(requestEntry.getResponseTimeMs());
            stats.incrementStatusCode(requestEntry.getResponseStatus());
        }
    }

    @Override
    public Object getResult() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (Map.Entry<String, RouteStats> entry : routeStats.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getStats());
        }
        return result;
    }

    @Override
    public void reset() {
        routeStats.clear();
    }
} 