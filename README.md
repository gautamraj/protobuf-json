## (WIP) Benchmarks of various approaches for converting Protobuf --> JSON in Java
This currently compares:
* The protobuf provided `JsonFormat.printer()`
* [HubSpot Jackson datatype protobuf module](https://github.com/HubSpot/jackson-datatype-protobuf)
* A custom implementation using Jackson streaming and hardcoded field mapping (i.e. something that could be code-generated).
* A custom implementation using Jackson streaming and the protobuf reflection
  API

### Current results
On an arbitrary test message, a Macbook M1 Max w/16 benchmark threads:
```
Benchmark                                                Mode  Cnt      Score      Error   Units
ProtoJsonBenchmark.benchJsonFormat                      thrpt    3   4207.261 ±  200.966  ops/ms
ProtoJsonBenchmark.benchHubspotJackson                  thrpt    3   2493.098 ±  957.209  ops/ms
ProtoJsonBenchmark.benchJacksonStreaming                thrpt    3  14810.663 ± 2176.974  ops/ms
ProtoJsonBenchmark.benchJacksonStreamingDynamic         thrpt    3  10764.557 ± 2604.497  ops/ms
```