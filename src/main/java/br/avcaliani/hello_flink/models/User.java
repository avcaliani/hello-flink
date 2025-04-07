package br.avcaliani.hello_flink.models;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
public class User {

    @JsonProperty("user_id")
    private String id;

    @JsonProperty("user_name")
    private String name;

}
