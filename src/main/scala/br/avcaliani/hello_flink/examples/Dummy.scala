package br.avcaliani.hello_flink.examples

import br.avcaliani.hello_flink.helpers.{Arguments, Pipeline}
import org.apache.flink.api.scala._

object Dummy extends Pipeline {

  private lazy val env = ExecutionEnvironment.getExecutionEnvironment

  override def run(args: Arguments): Unit = {

    val data: DataSet[String] = env.readTextFile(s"${args.lakePath}/dummy.txt")
    println(s"count: ${data.count()}")

    println("File Content")
    data.print()
  }
}
