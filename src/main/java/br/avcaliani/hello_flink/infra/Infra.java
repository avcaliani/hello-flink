package br.avcaliani.hello_flink.infra;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

abstract class Infra {

    private final String sourceType;
    final StreamExecutionEnvironment env;

    public Infra(StreamExecutionEnvironment env, String sourceType) {
        this.env = env;
        this.sourceType = sourceType;
    }

    /**
     * The source name is always "source_type-source--suffix".
     *
     * @param suffix Source Suffix.
     * @return Flink source name.
     */
    String sourceName(String suffix) {
        return this.sourceType + "-source--" + suffix.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
    }

}
