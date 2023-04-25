package protojson;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import proto.protojson.Test.TestMessagePb;
import proto.protojson.Test.TestMessagePb.TestEnumPb;

@RunWith(JUnit4.class)
public class ProtoToJsonTest {

  /** Verify our Test message serializes the same as JsonFormat. */
  @Test
  public void test() throws IOException {
    TestMessagePb testMessagePb =
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

    String reference =
        JsonFormat.printer()
            .includingDefaultValueFields()
            .omittingInsignificantWhitespace()
            .print(testMessagePb);
    assertThat(ProtoToJson.toJson(testMessagePb)).isEqualTo(reference);
  }
}
