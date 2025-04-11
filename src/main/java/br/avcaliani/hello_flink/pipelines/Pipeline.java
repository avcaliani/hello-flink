package br.avcaliani.hello_flink.pipelines;

import br.avcaliani.hello_flink.cli.Args;

import java.util.Random;

public abstract class Pipeline {

    public Pipeline init(String pipelineName) {
        System.out.println("-------------------------------");
        System.out.println(" Starting '" + pipelineName + "' pipeline " + emoji());
        System.out.println("-------------------------------");
        return this;
    }

    /**
     * Main pipeline method.
     * Here you should implement all the logic you need.
     *
     * @param args App arguments.
     * @return Itself.
     * @throws Exception Something not expected can happen ¯\_(ツ)_/¯
     */
    public abstract Pipeline run(Args args) throws Exception;

    public void sunset() {
        System.out.println("-------------------------------");
        System.out.println(" Pipeline finished \"\uD83C\uDF04\"");
        System.out.println("-------------------------------");
    }

    private String emoji() {
        String[] emojis = {"🪴", "🌵", "🌻", "🌷", "🍀", "🍂", "🍁", "🌱"};
        return emojis[new Random().nextInt(emojis.length)];
    }
}
