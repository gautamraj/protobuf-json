package protojson.bench;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import proto.protojson.Test.TestMessagePb;
import proto.protojson.Test.TestMessagePb.TestEnumPb;
import protojson.ProtoToJson;

/**
 * Simple example benchmark. Run with:
 *
 * <pre>
 * bazel run //src/test/java/protojson/bench
 * </pre>
 *
 * <p>To generate profiles, download async-profiler and run:
 *
 * <pre>
 * bazel run //src/test/java/protojson/bench -- -prof async:libPath=/path/to/async-profiler/build/libasyncProfiler.so
 * </pre>
 */
@Fork(value = 1)
@Threads(value = 16)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 3, time = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ProtoJsonBenchmark {

  private TestMessagePb message;
  private ObjectMapper mapper;
  private Printer printer;
  private JsonFactory jsonFactory;

  @Setup
  public void setup() {
    message =
        TestMessagePb.newBuilder()
            .setBytesField(ByteString.copyFromUtf8("hello world"))
            .setInt32Field(123)
            .setInt64Field(1234567890L)
            .setStringField("hello world")
            .setDoubleField(1.23456789)
            .setFloatField(1.23456789f)
            .setEnumField(TestEnumPb.TEST_ENUM_PB_0)
            .addAllRepeatedStringField(
                Lists.newArrayList("hello", "world", "hello", "world", "hello", "world"))
            .build();
    mapper = new ObjectMapper();
    mapper.registerModule(new ProtobufModule());
    printer = JsonFormat.printer().omittingInsignificantWhitespace().includingDefaultValueFields();
    jsonFactory = new JsonFactory();
  }

  @Benchmark
  public String benchJsonFormat() throws InvalidProtocolBufferException {
    return printer.print(message);
  }

  @Benchmark
  public String benchHubspotJackson() throws JsonProcessingException {
    return mapper.writeValueAsString(message);
  }

  @Benchmark
  public String benchJacksonStreaming() throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("int32Field", message.getInt32Field());
    jsonGenerator.writeNumberField("int64Field", message.getInt64Field());
    jsonGenerator.writeStringField("stringField", message.getStringField());
    jsonGenerator.writeBinaryField("bytesField", message.getBytesField().toByteArray());
    jsonGenerator.writeBooleanField("boolField", message.getBoolField());
    jsonGenerator.writeNumberField("floatField", message.getFloatField());
    jsonGenerator.writeNumberField("doubleField", message.getDoubleField());
    jsonGenerator.writeStringField("enumField", message.getEnumField().name());
    jsonGenerator.writeFieldName("repeatedStringField");
    jsonGenerator.writeStartArray();
    for (int i = 0; i < message.getRepeatedStringFieldCount(); i++) {
      jsonGenerator.writeString(message.getRepeatedStringField(i));
    }
    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndObject();
    jsonGenerator.close();

    return stream.toString(StandardCharsets.UTF_8);
  }

  @Benchmark
  public String benchJacksonStreamingDynamic() throws IOException {
    return ProtoToJson.toJson(message);
  }
}
