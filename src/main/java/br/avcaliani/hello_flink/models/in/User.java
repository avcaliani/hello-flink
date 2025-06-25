package br.avcaliani.hello_flink.models.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * This POJO represents exactly the input data schema.
 */
@Data
@JsonPropertyOrder({"user_id", "user_name"})
public class User {

    @JsonProperty("user_id")
    private String id;

    @JsonProperty("user_name")
    private String name;

}
