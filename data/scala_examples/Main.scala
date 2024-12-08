package br.avcaliani.hello_flink

import br.avcaliani.hello_flink.examples.Dummy
import br.avcaliani.hello_flink.helpers.{Arguments, Pipeline}

object Main extends App {

  println("Hello Flink o/")
  private val arguments = Arguments(args)
  private val pipeline: Pipeline = arguments.pipelineName match {
    case "dummy"     => Dummy
    case _           => throw new RuntimeException(s"The pipeline ${arguments.pipelineName} doesn't exist!")
  }
  pipeline.run(arguments)
}
