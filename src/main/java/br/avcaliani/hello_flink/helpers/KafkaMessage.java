package br.avcaliani.hello_flink.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Abstract class that sets default Kafka properties in a message.
 */
public abstract class KafkaMessage {

    @JsonIgnore @Getter @Setter
    protected byte[] key;

    /**
     * <a href="https://www.redpanda.com/guides/kafka-cloud-kafka-headers">Kafka Headers Guide</a>
     */
    @JsonIgnore @Getter
    protected Headers headers = addDefaultHeaders(new RecordHeaders());

    public void setHeaders(Headers headers) {
        this.headers = addDefaultHeaders(headers);
    }

    private static Headers addDefaultHeaders(Headers headers) {
        headers.add("content-type", "application/json".getBytes(UTF_8));
        headers.add("data-platform", "flink".getBytes(UTF_8));
        return headers;
    }
}
