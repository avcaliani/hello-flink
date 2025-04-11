package br.avcaliani.hello_flink.cli;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Data
@NoArgsConstructor
public class Args {

    Args(StreamExecutionEnvironment env) {
        this.env = env;
    }

    private StreamExecutionEnvironment env;
    private String pipeline;
    private String bucket;

}
