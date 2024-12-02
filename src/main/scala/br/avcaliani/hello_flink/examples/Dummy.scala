package br.avcaliani.hello_flink.examples

import br.avcaliani.hello_flink.Pipeline

object Dummy extends Pipeline {

  override def run(): Unit =
    println("Dummy pipeline here o/")
}
