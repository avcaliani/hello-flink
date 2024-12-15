package br.avcaliani.hello_flink.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Data
@AllArgsConstructor
public class Args {

    private StreamExecutionEnvironment env;
    private String pipeline;
    private String bucket;

}
