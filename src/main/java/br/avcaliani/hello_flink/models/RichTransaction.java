package br.avcaliani.hello_flink.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RichTransaction {

    @JsonProperty("transaction_id")
    private String id;
    private String timestamp;
    private User from;
    private User to;
    private String currency;
    private Double amount;
    private Double fee;
    private Boolean isValid;

    @JsonIgnore
    private String fromId;
    @JsonIgnore
    private String toId;

    public RichTransaction(Transaction tx) {
        // Core Fields
        this.id = tx.getId();
        this.timestamp = tx.getTimestamp();
        this.currency = tx.getCurrency();
        this.amount = tx.getAmount();
        this.fee = tx.getFee();
        // Helper Fields
        this.fromId = tx.getFrom();
        this.toId = tx.getTo();
    }

}
