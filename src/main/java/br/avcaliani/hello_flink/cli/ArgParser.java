package br.avcaliani.hello_flink.cli;


import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

import static org.apache.flink.configuration.CheckpointingOptions.*;
import static org.apache.flink.configuration.ExternalizedCheckpointRetention.RETAIN_ON_CANCELLATION;
import static org.apache.flink.core.execution.CheckpointingMode.EXACTLY_ONCE;

public class ArgParser {

    /**
     * Return an object with the arguments passed via CLI.
     *
     * @param arguments Array of arguments passed to the application.
     * @return Arguments parsed.
     */
    public static Args parse(String[] arguments) {

        if (arguments == null || arguments.length < 2)
            throw new RuntimeException("You must pass at least the pipeline argument!");

        var args = new Args(flinkEnv());
        for (int i = 0; i < arguments.length; i = i + 2) {
            setArgument(args, arguments[i].trim(), arguments[i + 1].trim());
        }
        return args;
    }

    private static void setArgument(Args args, String name, String value) {
        switch (name) {
            case "--pipeline" -> args.setPipeline(value);
            case "--bucket" -> args.setBucket(value);
            case "--kafka-brokers" -> args.setKafkaBrokers(value);
            default -> throw new RuntimeException("Unknown parameter passed: " + name);
        }
    }

    /**
     * Return a Flink Streaming Environment instance.
     * <br/>
     * It also sets Flink checkpoint directory. <br/>
     * More Details here 👉
     * <a href="https://nightlies.apache.org/flink/flink-docs-release-2.0/docs/ops/state/checkpoints/">About Flink Checkpoint</a>
     * and here
     * <a href="https://nightlies.apache.org/flink/flink-docs-release-2.0/docs/dev/datastream/fault-tolerance/checkpointing/">Enabling Checkpoint</a>
     *
     * @return Streaming environment.
     */
    private static StreamExecutionEnvironment flinkEnv() {

        var env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Flink Checkpoint 👇
        //   TL;DR;
        //   Checkpointing is very important to enable you not reprocess everything.
        //   And it is mandatory in EXACTLY_ONCE cases, which we have in this project.
        Configuration config = new Configuration();
        config.set(CHECKPOINT_STORAGE, "filesystem");
        config.set(CHECKPOINTS_DIRECTORY, "file:///data/flink/checkpoint");
        config.set(CHECKPOINTING_CONSISTENCY_MODE, EXACTLY_ONCE);
        config.set(CHECKPOINTING_TIMEOUT, Duration.ofMinutes(1));
        config.set(MIN_PAUSE_BETWEEN_CHECKPOINTS, Duration.ofSeconds(30));
        config.set(TOLERABLE_FAILURE_NUMBER, 2);
        config.set(MAX_CONCURRENT_CHECKPOINTS, 1);
        config.set(EXTERNALIZED_CHECKPOINT_RETENTION, RETAIN_ON_CANCELLATION);

        env.configure(config);
        env.enableCheckpointing(60 * 1000); // Every 1 minute.
        return env;
    }

}