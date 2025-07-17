package br.avcaliani.hello_flink.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract class that sets default Kafka properties in a message.
 */
public abstract class KafkaMessage {

    private static final Set<Header> DEFAULT_HEADERS = parse(Map.of(
            "content-type", "application/json",
            "data-platform", "flink"
    ));

    @JsonIgnore @Getter @Setter
    private String key;

    @JsonIgnore @Setter
    private Set<Header> headers = Collections.emptySet();

    public Set<Header> getHeaders() {
        return Stream.concat(DEFAULT_HEADERS.stream(), headers.stream())
                .collect(Collectors.toSet());
    }

    /**
     * Parses a Map of key/value pairs into the expected Kafka Headers type.
     *
     * @param headers Key/Value Pairs.
     * @return Set of Headers.
     */
    private static Set<Header> parse(Map<String, String> headers) {
        return headers.entrySet().stream().map(entry -> new Header() {

            @Override
            public String key() {
                return entry.getKey();
            }

            @Override
            public byte[] value() {
                return entry.getValue().getBytes(StandardCharsets.UTF_8);
            }

        }).collect(Collectors.toSet());
    }
}
