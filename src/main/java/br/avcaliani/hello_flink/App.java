package br.avcaliani.hello_flink;

import br.avcaliani.hello_flink.examples.Dummy;
import br.avcaliani.hello_flink.models.Args;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class App {

    private static Args getArgs(String[] args) {
        var pipelineName = args[0];
        var bucketPath = args[1];
        return new Args(StreamExecutionEnvironment.getExecutionEnvironment(), pipelineName, bucketPath);
    }

    public static void main(String[] arguments) throws Exception {

        if (arguments.length < 2) throw new RuntimeException("Pipeline name and bucket are required!");

        var args = getArgs(arguments);
        var name = args.getPipeline();

        var pipeline = switch (name) {
            case "dummy" -> new Dummy();
            default -> throw new RuntimeException("Pipeline doesn't exist! Name: " + name);
        };
        pipeline
            .init(name)
            .run(args)
            .sunset();
    }
}
