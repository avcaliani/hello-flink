package br.avcaliani.hello_flink.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

/**
 * My generic deserializer for POJOs.
 * <p>
 * When reading Kafka topics, at first I was using the <code>new SimpleStringSchema()</code>,
 * however I'd like to manipulate the data, being easier to use a POJO.
 * <p>
 * I didn't find a simple way to just pass the POJO and be happy, so I implemented this guy.
 * <p>
 * This could be easily called as "JSONSerializer", but to make it easier to understand
 * that this component was created manually, I chose its current name.
 *
 * @param <T> The class you want to deserialize.
 */
public class MyDeserializer<T> implements KafkaRecordDeserializationSchema<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> classType;

    public MyDeserializer(Class<T> classType) {
        this.objectMapper = new ObjectMapper();
        this.classType = classType;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(this.classType);
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<T> out) throws IOException {
        var msgPayload = objectMapper.readValue(record.value(), this.classType);
        out.collect(msgPayload);
    }
}
