# Log Parser and Aggregator

## Problem Description

The application solves the challenge of processing and analyzing heterogeneous log files from a distributed system. These logs contain different types of information:
1. **APM (Application Performance Monitoring) Logs**: Track system metrics like CPU and memory usage
2. **Application Logs**: Record application events, errors, and informational messages
3. **Request Logs**: Capture API call details including response times and status codes

The main challenges addressed are:
- Parsing different log formats efficiently
- Aggregating metrics across multiple log entries
- Generating meaningful statistics for each log type
- Maintaining extensibility for new log types
- Ensuring clean separation of concerns

## Design Patterns Used

1. **Strategy Pattern**
   - Used in the log parsing system where different parsers (`ApmLogParser`, `ApplicationLogParser`, `RequestLogParser`) implement the same interface (`LogParser`)
   - Allows for easy addition of new log types by implementing new parser strategies
   - Makes the parsing logic interchangeable and encapsulated
   - Each parser strategy knows how to handle its specific log format

2. **Factory Pattern**
   - Used implicitly through the `Main` class which creates and manages the appropriate parsers and aggregators
   - Provides a centralized way to create and manage different types of log processors
   - Encapsulates the creation logic and reduces coupling

3. **Template Method Pattern**
   - Used in the `LogEntry` hierarchy where common behavior is defined in the base class
   - Specific log entry types (`ApmLogEntry`, `ApplicationLogEntry`, `RequestLogEntry`) extend the base class
   - Provides a skeleton algorithm in the base class while letting subclasses override specific steps

## Consequences of Using These Patterns

### Strategy Pattern Benefits:
- Easy to add new log types without modifying existing code
- Each parser is encapsulated and can be tested independently
- Runtime flexibility in choosing the appropriate parser
- Promotes the Open/Closed Principle

### Strategy Pattern Drawbacks:
- Increased number of classes
- Clients must be aware of different strategies
- Potential overhead in strategy selection

### Factory Pattern Benefits:
- Centralized creation of objects
- Easy to manage dependencies
- Simplified client code
- Encapsulates object creation logic

### Factory Pattern Drawbacks:
- Additional complexity in the factory class
- Need to modify factory when adding new types

### Template Method Pattern Benefits:
- Code reuse through inheritance
- Consistent algorithm structure
- Easy to extend base behavior
- Reduces code duplication

### Template Method Pattern Drawbacks:
- Inheritance-based, which can be limiting
- Can be harder to understand than composition
- Changes to the template affect all subclasses

## Class Diagram

```
+----------------+     +----------------+     +----------------+
|    LogEntry    |<----|  ApmLogEntry   |     |  LogParser     |
+----------------+     +----------------+     +----------------+
| +timestamp     |     | +metric        |     | +parse()       |
| +host          |     | +value         |     | +canParse()    |
| +attributes    |     +----------------+     +----------------+
+----------------+           ^                        ^
       ^                    |                        |
       |                    |                        |
+----------------+     +----------------+     +----------------+
|ApplicationLog  |     | RequestLogEntry|     | ApmLogParser   |
+----------------+     +----------------+     +----------------+
| +level         |     | +requestMethod |     | +parse()       |
| +message       |     | +requestUrl    |     | +canParse()    |
+----------------+     | +responseStatus|     +----------------+
                       | +responseTimeMs|             ^
                       +----------------+             |
                                                     |
+----------------+     +----------------+     +----------------+
| LogAggregator  |<----| ApmAggregator  |     |AppLogParser    |
+----------------+     +----------------+     +----------------+
| +aggregate()   |     | +metricValues  |     | +parse()       |
| +getResult()   |     | +aggregate()   |     | +canParse()    |
| +reset()       |     | +getResult()   |     +----------------+
+----------------+     +----------------+
       ^
       |
+----------------+     +----------------+
|AppAggregator   |     |RequestAggregator|
+----------------+     +----------------+
| +levelCounts   |     | +routeStats    |
| +aggregate()   |     | +aggregate()   |
| +getResult()   |     | +getResult()   |
+----------------+     +----------------+
```

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven

### Building
```bash
mvn clean package
```

### Running
```bash
java -jar target/log-parser-1.0-SNAPSHOT-jar-with-dependencies.jar --file <input-file.txt>
```

### Output Files
The application generates three JSON files:
1. `apm.json` - Contains APM metrics statistics
2. `application.json` - Contains application log level counts
3. `request.json` - Contains request statistics per API route

## Example Input
```
timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72
timestamp=2024-02-24T16:22:20Z level=INFO message="Scheduled maintenance starting" host=webserver1
timestamp=2024-02-24T16:22:25Z request_method=POST request_url="/api/update" response_status=202 response_time_ms=200 host=webserver1
```

## Example Output

### apm.json
```json
{
    "cpu_usage_percent": {
        "minimum": 72,
        "median": 72,
        "average": 72,
        "max": 72
    }
}
```

### application.json
```json
{
    "INFO": 1
}
```

### request.json
```json
{
    "/api/update": {
        "response_times": {
            "min": 200,
            "50_percentile": 200,
            "90_percentile": 200,
            "95_percentile": 200,
            "99_percentile": 200,
            "max": 200
        },
        "status_codes": {
            "2XX": 1,
            "4XX": 0,
            "5XX": 0
        }
    }
}
``` 