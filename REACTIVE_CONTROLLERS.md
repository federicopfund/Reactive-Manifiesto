# Reactive Controllers - Documentation

## Overview
This application implements reactive programming patterns following the Reactive Manifesto principles:
- **Responsive**: The system responds in a timely manner
- **Resilient**: The system stays responsive in the face of failure
- **Elastic**: The system stays responsive under varying workload
- **Message Driven**: The system relies on asynchronous message-passing

## New Reactive Controllers

### 1. AsyncDataController
Demonstrates asynchronous data fetching patterns with non-blocking I/O.

#### Endpoints:

**GET /api/data**
- Returns async data with timeout management
- Demonstrates: Non-blocking I/O, Future handling, timeout patterns
- Response: JSON with data array and timestamp

```bash
curl http://localhost:9000/api/data
```

**GET /api/data/combined**
- Combines multiple async data sources in parallel
- Demonstrates: Future composition, parallel execution
- Response: Combined JSON with users, posts, and comments

```bash
curl http://localhost:9000/api/data/combined
```

**GET /api/data/with-error-handling**
- Shows reactive error handling with fallback patterns
- Demonstrates: Error recovery, fallback strategies
- Response: Success or error with fallback data

```bash
curl http://localhost:9000/api/data/with-error-handling
```

### 2. StreamController
Implements Server-Sent Events (SSE) for real-time data streaming using Akka Streams.

#### Endpoints:

**GET /api/stream/events**
- Real-time event stream (30 events, 1 per second)
- Demonstrates: Akka Streams, Server-Sent Events
- Content-Type: text/event-stream

```bash
curl http://localhost:9000/api/stream/events
```

**GET /api/stream/sensors**
- Simulated sensor data stream (60 readings, 2 per second)
- Demonstrates: Stream transformations, real-time data
- Content-Type: text/event-stream

```bash
curl http://localhost:9000/api/stream/sensors
```

**GET /api/stream/notifications**
- Real-time notification stream (20 notifications, 1 every 2 seconds)
- Demonstrates: Different event types, filtering
- Content-Type: text/event-stream

```bash
curl http://localhost:9000/api/stream/notifications
```

**GET /api/stream/backpressure**
- Stream with backpressure management (15 events, throttled to 1 per second)
- Demonstrates: Buffer management, flow control, throttling
- Content-Type: text/event-stream

```bash
curl http://localhost:9000/api/stream/backpressure
```

## Reactive Patterns Implemented

### 1. Asynchronous Processing
All controllers use `Action.async` instead of blocking `Action`, ensuring non-blocking request handling.

### 2. Future Composition
The `getCombinedData` endpoint demonstrates parallel execution and composition of multiple async operations using `for-comprehension`.

### 3. Error Handling & Resilience
The `getDataWithErrorHandling` endpoint shows reactive error recovery with fallback patterns using `Future.recover`.

### 4. Timeout Management
The `getData` endpoint implements timeout patterns using `Future.firstCompletedOf` to prevent hanging requests.

### 5. Backpressure
The `streamWithBackpressure` endpoint demonstrates Akka Streams backpressure with buffering and overflow strategies.

### 6. Real-time Streaming
All Stream endpoints use Server-Sent Events (SSE) for unidirectional real-time data push from server to client.

## Testing

Run the tests:
```bash
sbt test
```

Test specific controller:
```bash
sbt "testOnly controllers.AsyncDataControllerSpec"
sbt "testOnly controllers.StreamControllerSpec"
```

## Architecture

### Dependencies
- **Play Framework**: Web framework with built-in async support
- **Akka Typed Actors**: Message-driven architecture (existing in ContactController)
- **Akka Streams**: Reactive stream processing for SSE endpoints
- **Scala Futures**: Async computation primitives

### Flow Diagram

```
Client Request
    ↓
Play Router
    ↓
Controller (Action.async)
    ↓
Future[Result] or Source[Data]
    ↓
Non-blocking I/O
    ↓
Response (JSON or SSE)
```

## Use Cases

1. **Real-time Dashboards**: Use `/api/stream/*` endpoints for live data updates
2. **API Integrations**: Use `/api/data/*` endpoints for async data fetching
3. **Monitoring Systems**: Use sensor stream for real-time metrics
4. **Notification Systems**: Use notification stream for push notifications
5. **Data Analytics**: Use combined data endpoint for aggregated views

## Best Practices

1. **Always use Action.async** for I/O operations
2. **Implement timeouts** to prevent hanging requests
3. **Handle errors gracefully** with fallback strategies
4. **Use backpressure** in streams to manage load
5. **Limit stream duration** to prevent infinite streams
6. **Use parallel execution** when combining independent data sources

## Performance Benefits

- **Non-blocking I/O**: Server can handle more concurrent requests
- **Parallel Execution**: Multiple operations run simultaneously
- **Resource Efficiency**: Threads aren't blocked waiting for I/O
- **Scalability**: System can handle increased load without linear resource growth
- **Resilience**: Timeouts and error handling prevent cascading failures

## Monitoring & Observability

All endpoints include timestamps in responses for tracking and monitoring. Consider adding:
- Response time logging
- Error rate metrics
- Stream connection monitoring
- Backpressure indicators

## Future Enhancements

- WebSocket support for bidirectional streaming
- Reactive database integration (e.g., Slick, ReactiveMongo)
- Circuit breaker pattern for external service calls
- Distributed tracing for async operations
- Metrics and health check endpoints
