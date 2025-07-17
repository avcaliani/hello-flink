package br.avcaliani.hello_flink.models.in;

import br.avcaliani.hello_flink.helpers.KafkaMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class Transaction extends KafkaMessage {

    @JsonProperty("transaction_id")
    private String id;
    private String timestamp;
    private String from;
    private String to;
    private String currency;
    private Double amount;
    private Double fee;

}
