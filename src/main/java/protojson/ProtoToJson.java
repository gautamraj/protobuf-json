package protojson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A simple dynamic proto -> json converter using reflection and the Jackson streaming API.
 */
public class ProtoToJson {

  private static final JsonFactory JSON_FACTORY = new JsonFactory();
  private static final LoadingCache<Message, List<FieldDescriptor>> FIELD_DESCRIPTOR_CACHE =
      Caffeine.newBuilder().build(m -> m.getDescriptorForType().getFields());

  // Construct a list of Functions from iterating the fields, getting values from the proto
  // Map<FieldDescriptor, Function<>> map = buildLambdaMap();
  public static String toJson(Message message) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator = JSON_FACTORY.createGenerator(stream, JsonEncoding.UTF8);

    writeJsonMessage(message, jsonGenerator);
    jsonGenerator.close();

    return stream.toString(StandardCharsets.UTF_8);
  }

  private static void writeJsonMessage(Message message, JsonGenerator jsonGenerator)
      throws IOException {
    jsonGenerator.writeStartObject();
    for (FieldDescriptor fd : FIELD_DESCRIPTOR_CACHE.get(message)) {
      jsonGenerator.writeFieldName(fd.getJsonName());
      if (fd.isRepeated()) {
        writeRepeatedField(message, jsonGenerator, fd);
      } else {
        Object obj = message.getField(fd);
        writeSingularField(jsonGenerator, fd, obj);
      }
    }
    jsonGenerator.writeEndObject();
  }

  private static void writeRepeatedField(Message message, JsonGenerator jsonGenerator,
      FieldDescriptor fd) throws IOException {
    jsonGenerator.writeStartArray();
    for (Object obj : (List<?>) message.getField(fd)) {
      writeSingularField(jsonGenerator, fd, obj);
    }
    jsonGenerator.writeEndArray();
  }

  private static void writeSingularField(JsonGenerator jsonGenerator, FieldDescriptor fd,
      Object obj)
      throws IOException {
    switch (fd.getJavaType()) {
      case FLOAT -> jsonGenerator.writeNumber((float) obj);
      case DOUBLE -> jsonGenerator.writeNumber((double) obj);
      case INT -> jsonGenerator.writeNumber((int) obj);
      case LONG -> jsonGenerator.writeString(String.valueOf((long) obj));
      case BOOLEAN -> jsonGenerator.writeBoolean((boolean) obj);
      case STRING -> jsonGenerator.writeString((String) obj);
      case BYTE_STRING -> jsonGenerator.writeBinary(((ByteString) obj).toByteArray());
      case MESSAGE -> writeJsonMessage((Message) obj, jsonGenerator);
      case ENUM -> jsonGenerator.writeString(((EnumValueDescriptor) obj).getName());
    }
  }
}
