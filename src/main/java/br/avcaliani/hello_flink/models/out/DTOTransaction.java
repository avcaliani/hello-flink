package br.avcaliani.hello_flink.models.out;

import br.avcaliani.hello_flink.models.in.Transaction;
import br.avcaliani.hello_flink.models.in.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DTOTransaction {

    @JsonProperty("transaction_id")
    private String id;
    private String timestamp;
    private User sender;
    private User receiver;
    private String currency;
    private Double amount;
    private Double fee;
    private Boolean isValid = true;

    @JsonIgnore
    private String from;
    @JsonIgnore
    private String to;

    public DTOTransaction(Transaction tx, User sender, User receiver) {
        // Core Fields
        this.id = tx.getId();
        this.timestamp = tx.getTimestamp();
        this.currency = tx.getCurrency();
        this.amount = tx.getAmount();
        this.fee = tx.getFee();
        this.sender = sender;
        this.receiver = receiver;
        // Helper Fields
        this.from = tx.getFrom();
        this.to = tx.getTo();
    }

}
