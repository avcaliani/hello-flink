package br.avcaliani.hello_flink.infra;

import br.avcaliani.hello_flink.infra.serializers.KafkaDeserializer;
import br.avcaliani.hello_flink.infra.serializers.KafkaMessage;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Kafka extends Infra {

    private final String brokers;

    public Kafka(StreamExecutionEnvironment env, String brokers) {
        super(env, "kafka");
        this.brokers = brokers;
    }

    /**
     * Create a Data Stream based on Kafka Source.
     *
     * @param topic   Kafka Topic.
     * @param groupId Kafka Consumer - Group ID.
     * @param clazz   POJO Class Kafka Message (also the 'T')
     * @return Data Stream.
     */
    public <T extends KafkaMessage> DataStream<T> read(String topic, String groupId, Class<T> clazz) {

        KafkaSource<T> source = KafkaSource.<T>builder()
                .setBootstrapServers(this.brokers)
                .setTopics(topic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new KafkaDeserializer<>(clazz))
                .build();

        return this.env.fromSource(source, WatermarkStrategy.noWatermarks(), sourceName(topic));
    }

}
