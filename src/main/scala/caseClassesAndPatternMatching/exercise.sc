import caseClassesAndPatternMatching._

val f = new ExprFormatter

val e1 = BinOp("*", BinOp("/", Number(1), Number(2)), BinOp("+", Var("x"), Number(1)))
val e2 = BinOp("+", BinOp("/", Var("x"), Number(2)), BinOp("/", Number(1.5), Var("x")))
val e3 = BinOp("/", e1, e2)

def show(e: Expr) = println(f.format(e) + "\n\n")

for (e <- Array(e1, e2, e3)) show(e)

show(BinOp("/", BinOp("/", Number(1), Number(2)), Number(3)))

val m = Map("a" -> 1, "b" -> 2, "c" -> 3)
m.get("a")
m.getOrElse("d", 0)
//m("d")

val (a, b) = ("1" ,"2")


List(1,22,3,4).init
//List.empty[Int].tail


List(List(List(1,2), List(3)), List(4, 5), List(6)).flatten
var sum = 0
List(1,2,3,4,5) foreach (sum += _)

trait Printer[T] {
  def print(t: T): String
}

implicit val sp: Printer[Int] = new Printer[Int] {
  def print(i :Int) = i.toString
}

def foo[T](t: T)(implicit p: Printer[T]) = p.print(t)

//val res = foo(3)
val res = foo(false)
println(s"res: ${res}")

