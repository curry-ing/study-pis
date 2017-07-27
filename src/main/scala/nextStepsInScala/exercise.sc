import scala.io.Source

val oneTwo = List(1, 2)
val threeFour = List(3, 4)
val oneTwoThreeFour = oneTwo ::: threeFour

val twoThree = List(2, 3)
1 :: twoThree

val oneTwoThree = 1 :: 2 :: 3 :: Nil

oneTwoThreeFour.apply(1)
oneTwoThreeFour(1)

val pair = (99, "Luftballons")
println(pair._1)
println(pair._2)

('u', 'r', "the", 1, 4, "me")


val list1 = List(1, 2, 3, 4)
//list1 = List(2, 3, 4)
//list1(1) = 0

var list2 = List(1, 2, 3, 4)
list2 = List(2, 3, 4)
//list2(1) = 0

val array1 = Array(1, 2, 3, 4)
//array1 = Array(2, 3, 4)
array1(1) = 11
array1

var array2 = Array(1, 2, 3, 4)
array2 = Array(2, 3, 4)
array2(1) = 0
array2


var jetSet = Set("Boeing", "Airbus")
jetSet += "Lear"
jetSet
println(jetSet.contains("Cessna"))


val a = Source.fromFile("/Users/sunghoon/Documents/study/Scala/pis/docs/first_steps_in_scala.md").getLines.toStream
a.take(5).foreach(println)

