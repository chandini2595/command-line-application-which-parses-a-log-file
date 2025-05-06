package com.logparser.aggregator;

import com.logparser.model.ApmLogEntry;
import com.logparser.model.LogEntry;

import java.util.*;

public class ApmLogAggregator implements LogAggregator {
    private Map<String, List<Double>> metricValues = new HashMap<>();

    @Override
    public void aggregate(LogEntry entry) {
        if (entry instanceof ApmLogEntry) {
            ApmLogEntry apmEntry = (ApmLogEntry) entry;
            metricValues.computeIfAbsent(apmEntry.getMetric(), k -> new ArrayList<>())
                    .add(apmEntry.getValue());
        }
    }

    @Override
    public Object getResult() {
        Map<String, Map<String, Double>> result = new HashMap<>();
        
        for (Map.Entry<String, List<Double>> entry : metricValues.entrySet()) {
            List<Double> values = entry.getValue();
            Collections.sort(values);
            
            Map<String, Double> stats = new HashMap<>();
            stats.put("minimum", values.get(0));
            stats.put("max", values.get(values.size() - 1));
            
            double sum = values.stream().mapToDouble(Double::doubleValue).sum();
            stats.put("average", sum / values.size());
            
            int middle = values.size() / 2;
            if (values.size() % 2 == 0) {
                stats.put("median", (values.get(middle - 1) + values.get(middle)) / 2.0);
            } else {
                stats.put("median", values.get(middle));
            }
            
            result.put(entry.getKey(), stats);
        }
        
        return result;
    }

    @Override
    public void reset() {
        metricValues.clear();
    }
} 