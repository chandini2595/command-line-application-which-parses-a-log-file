package com.logparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logparser.aggregator.ApmLogAggregator;
import com.logparser.aggregator.ApplicationLogAggregator;
import com.logparser.aggregator.LogAggregator;
import com.logparser.aggregator.RequestLogAggregator;
import com.logparser.model.LogEntry;
import com.logparser.parser.ApmLogParser;
import com.logparser.parser.ApplicationLogParser;
import com.logparser.parser.LogParser;
import com.logparser.parser.RequestLogParser;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static final List<LogParser> parsers = Arrays.asList(
            new ApmLogParser(),
            new ApplicationLogParser(),
            new RequestLogParser()
    );

    private static final Map<String, LogAggregator> aggregators = Map.of(
            "APM", new ApmLogAggregator(),
            "APPLICATION", new ApplicationLogAggregator(),
            "REQUEST", new RequestLogAggregator()
    );

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("f", "file", true, "Input log file path");
        options.addOption("o", "output-dir", true, "Output directory path (optional)");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            String inputFile = cmd.getOptionValue("file");
            String outputDir = cmd.getOptionValue("output-dir");

            if (inputFile == null) {
                formatter.printHelp("log-parser", options);
                System.exit(1);
            }

            processLogFile(inputFile, outputDir);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            formatter.printHelp("log-parser", options);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error processing log file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void processLogFile(String inputFile, String outputDir) throws IOException {
        // Read and parse log file
        List<LogEntry> entries = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Processing line: " + line);
                for (LogParser logParser : parsers) {
                    if (logParser.canParse(line)) {
                        LogEntry entry = logParser.parse(line);
                        System.out.println("Parsed entry type: " + entry.getType());
                        entries.add(entry);
                        break;
                    }
                }
            }
        }

        // Aggregate logs
        for (LogEntry entry : entries) {
            LogAggregator aggregator = aggregators.get(entry.getType());
            if (aggregator != null) {
                System.out.println("Aggregating entry of type: " + entry.getType());
                aggregator.aggregate(entry);
            }
        }

        // Create output directory if it doesn't exist
        Path outputPath = Paths.get(outputDir != null ? outputDir : "output");
        Files.createDirectories(outputPath);

        // Write results to JSON files
        ObjectMapper objectMapper = new ObjectMapper();
        
        Map<String, Object> apmResults = (Map<String, Object>) aggregators.get("APM").getResult();
        System.out.println("APM results: " + apmResults);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(
                outputPath.resolve("apm.json").toFile(), apmResults);
        
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(
                outputPath.resolve("application.json").toFile(), aggregators.get("APPLICATION").getResult());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(
                outputPath.resolve("request.json").toFile(), aggregators.get("REQUEST").getResult());
    }
} 