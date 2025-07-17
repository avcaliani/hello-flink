package br.avcaliani.hello_flink;

import br.avcaliani.hello_flink.cli.ArgParser;
import br.avcaliani.hello_flink.pipelines.Dummy;
import br.avcaliani.hello_flink.pipelines.ValidateTransactions;

public class App {

    public static void main(String[] arguments) throws Exception {

        var args = ArgParser.parse(arguments);
        var pipelineName = args.getPipeline();

        var pipeline = switch (pipelineName) {
            case "dummy" -> new Dummy();
            case "validate-transactions" -> new ValidateTransactions();
            default -> throw new RuntimeException("Pipeline doesn't exist! Name: " + pipelineName);
        };
        pipeline
            .init(pipelineName)
            .run(args)
            .sunset(pipelineName);
    }
}
