package br.avcaliani.hello_flink.cli;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ArgParser {

    /** Return an object with the arguments passed via CLI.
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

    private static StreamExecutionEnvironment flinkEnv() {
        return StreamExecutionEnvironment.getExecutionEnvironment();
    }

    private static void setArgument(Args args, String name, String value) {
        switch (name) {
            case "--pipeline" -> args.setPipeline(value);
            case "--bucket" -> args.setBucket(value);
            case "--kafka-brokers" -> args.setKafkaBrokers(value);
            default -> throw new RuntimeException("Unknown parameter passed: " + name);
        }
        ;
    }

}