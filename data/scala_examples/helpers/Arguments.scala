package br.avcaliani.hello_flink.helpers

case class Arguments(pipelineName: String, lakePath: String)

object Arguments {

  def apply(args: Array[String]): Arguments = {

    if (args.isEmpty || args.length < 2)
      throw new RuntimeException("Missing Arguments: pipeline name and lake path!")

    Arguments(
      pipelineName = args.head,
      lakePath = args(1)
    )
  }
}
