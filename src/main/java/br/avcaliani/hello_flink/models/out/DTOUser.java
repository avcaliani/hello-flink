package br.avcaliani.hello_flink.models.out;

import br.avcaliani.hello_flink.models.in.User;
import lombok.Data;

@Data
public class DTOUser {

    /* Original Fields */
    private String id;
    private String name;

    /* New Fields */
    private String email;

    public DTOUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}
