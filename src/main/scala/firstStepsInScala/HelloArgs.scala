package firstStepsInScala

object HelloArgs {
  def main(args: Array[String]) = {
    println("Hello, " + args(0) + "!")
  }
}
