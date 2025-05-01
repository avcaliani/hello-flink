package br.avcaliani.hello_flink.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {

    @JsonProperty("user_id")
    private String id;

    @JsonProperty("user_name")
    private String name;

    /**
     * <b>Developer Note</b>
     * </br>
     * This field should be created dynamically by a Map, since it's not in the CSV file.</br>
     * I believe the best option would be creating a User, to represent the CSV schema,
     * and a User DTO that will have the extra fields.
     * </br></br>
     * However, since this is just a PoC I'm not going to create the DTO.
     * </br></br>
     * 💡 Do you have a different opinion? Isn't it the best approach? Let me know?
     */
    private String email;

}
