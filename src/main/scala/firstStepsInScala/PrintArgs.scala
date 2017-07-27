package firstStepsInScala

object PrintArgs {
  def main(args: Array[String]) = {
    var i = 0
    while (i < args.length) {
      println(args(i))
      i += 1
    }
  }
}
