package firstStepsInScala

object Forargs {
  def main(args: Array[String]) = {
    for (arg <- args)
      println(arg)
  }
}
