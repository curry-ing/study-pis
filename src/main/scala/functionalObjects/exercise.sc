import functionalObjects.Rational

val oneHalf = new Rational(1, 2)

println(oneHalf)

val x = new Rational(1, 3)

val y = new Rational(5, 7)

val increase: (Int) => Int = (x : Int) => x + 1

def increase2(x: Int): Int = x + 1

1 to 5 map increase
1 to 5 map increase2

increase(10)
increase2(10)

increase(11)
increase2(11)
