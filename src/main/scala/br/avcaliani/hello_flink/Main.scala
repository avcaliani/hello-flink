package br.avcaliani.hello_flink

import br.avcaliani.hello_flink.examples.Dummy

object Main extends App {

  println("Hello Flink o/")
  if (args.isEmpty) {
    println("Missing Argument: pipeline name!")
    System.exit(1)
  }

  private val pipeline: Pipeline = args.head match {
    case "dummy"     => Dummy
    case _           => throw new RuntimeException(s"The pipeline ${args.head} doesn't exist!")
  }
  pipeline.run()
  System.exit(0)
}
