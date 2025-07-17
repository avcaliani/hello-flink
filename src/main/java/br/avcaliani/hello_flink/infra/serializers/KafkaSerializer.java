package br.avcaliani.hello_flink.infra.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

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
                    .configure(WRITE_DATES_AS_TIMESTAMPS, false);

    private final String topic;

    public KafkaSerializer(String topic) {
        this.topic = topic;
    }

    @Override @Nullable
    public ProducerRecord<byte[], byte[]> serialize(T message, KafkaSinkContext context, Long timestamp) {
        try {
            return new ProducerRecord<>(
                    topic,
                    null, // no specific partition
                    timestamp,
                    message.getKey(),
                    OBJECT_MAPPER.writeValueAsBytes(message),
                    message.getHeaders()
            );
        } catch (JsonProcessingException | RuntimeException err) {
            throw new IllegalArgumentException("Not able to serialize the message -> " + message, err);
        }
    }
}
