package br.avcaliani.hello_flink.models.out;

import br.avcaliani.hello_flink.models.in.Transaction;
import br.avcaliani.hello_flink.models.in.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<String> errors = new ArrayList<>();

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

    @JsonIgnore
    public boolean isValid() {
        return isValid;
    }

    @JsonIgnore
    public boolean isInvalid() {
        return !isValid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        DTOTransaction that = (DTOTransaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
