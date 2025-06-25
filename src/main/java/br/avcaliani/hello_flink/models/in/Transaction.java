package br.avcaliani.hello_flink.models.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Transaction {

    @JsonProperty("transaction_id")
    private String id;
    private String timestamp;
    private String from;
    private String to;
    private String currency;
    private Double amount;
    private Double fee;

}
