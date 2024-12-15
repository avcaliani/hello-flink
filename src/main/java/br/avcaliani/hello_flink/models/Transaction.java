package br.avcaliani.hello_flink.models;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
public class Transaction {

    private String tid;

    @JsonProperty("user_id")
    private String userId;

    private String amount;

    @JsonProperty("created_at")
    private String createdAt;

    private List<String> tags;

}
