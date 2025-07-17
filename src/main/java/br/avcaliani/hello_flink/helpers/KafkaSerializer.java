package br.avcaliani.hello_flink.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

/**
 * My generic Kafka serializer from POJOs to Kafka Message.
 *
 * @param <T> The class you want to deserialize.
 */
public class KafkaSerializer<T extends KafkaMessage> implements KafkaRecordSerializationSchema<T> {

    private static final ObjectMapper OBJECT_MAPPER =
            JsonMapper.builder()
                    .build()
                    .registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final String topic;

    public KafkaSerializer(String topic) {
        this.topic = topic;
    }

    @Nullable
    @Override
    public ProducerRecord<byte[], byte[]> serialize(T message, KafkaSinkContext context, Long timestamp) {
        try {
            var key = message.getKey();
            return new ProducerRecord<byte[], byte[]>(
                    topic,
                    null, // no specific partition
                    timestamp,
                    key == null ? null : key.getBytes(StandardCharsets.UTF_8),
                    OBJECT_MAPPER.writeValueAsBytes(message),
                    message.getHeaders()
            );
        } catch (JsonProcessingException | RuntimeException err) {
            throw new IllegalArgumentException("Not able to serialize the message -> " + message, err);
        }
    }
}
