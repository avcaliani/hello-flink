package br.avcaliani.hello_flink.infra;

import br.avcaliani.hello_flink.infra.serializers.KafkaDeserializer;
import br.avcaliani.hello_flink.infra.serializers.KafkaMessage;
import br.avcaliani.hello_flink.infra.serializers.KafkaSerializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static org.apache.flink.connector.base.DeliveryGuarantee.AT_LEAST_ONCE;
import static org.apache.flink.connector.base.DeliveryGuarantee.EXACTLY_ONCE;

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

    /**
     * Create a Kafka Sink for a specific topic.
     * <br/>
     * <a href="https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/datastream/kafka/">
     * Flink + Kafka Documentation
     * <a/>
     * <br/><br/>
     * <b>Something about EXACTLY_ONCE...</b><br/>
     * <p><i>
     * "Kafka brokers by default have transaction.max.timeout.ms set to 15 minutes.<br/>
     * This property will not allow to set transaction timeouts for the producers larger than its value.<br/>
     * FlinkKafkaProducer by default sets the transaction.timeout.ms property in producer config to 1 hour,
     * thus transaction.max.timeout.ms should be increased before using the Semantic.EXACTLY_ONCE mode."<i/> -
     * <a href="https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/connectors/datastream/kafka/">Reference Doc</a>
     * </p>
     * Another reference on
     * <a href="https://flink.apache.org/2018/02/28/an-overview-of-end-to-end-exactly-once-processing-in-apache-flink-with-apache-kafka-too/">
     * how exactly once works.
     * </a>
     *
     * @param topic    Kafka Topic.
     * @param delivery Delivery Strategy.
     * @return Kafka Sink.
     */
    public <T extends KafkaMessage> Sink<T> sink(String topic, DeliveryGuarantee delivery) {
        var sinkBuilder = KafkaSink.<T>builder()
                .setBootstrapServers(this.brokers)
                .setRecordSerializer(new KafkaSerializer<>(topic))

                .setDeliveryGuarantee(delivery);

        if (delivery == EXACTLY_ONCE) {
            sinkBuilder
                    .setProperty("transaction.timeout.ms", "900000")
                    .setTransactionalIdPrefix("flink-app--"); // Mandatory when using Exactly Once
        }
        return sinkBuilder.build();
    }

    /**
     * Documentation 👉 {@link #sink(String, DeliveryGuarantee)}
     */
    public <T extends KafkaMessage> Sink<T> sink(String topic) {
        return sink(topic, AT_LEAST_ONCE);
    }

}
