# 15. Case Classes and Pattern Matching
> for writing regular, non-encapsulated data structure

## 15.1 A simple example
[Expr.scala](../src/main/scala/caseClassesAndPatternMatching/Expr.scala)

#### Case Classes
- adds same name of **Factory Methods**
- arguments: become `val` => maintained as field
- add some _natural_  implementation of methods: `toString`, `hashCode` & `equals`
- **`copy`** method: make modified copies [example](https://gitlab.ridi.io/da/da-scala/commit/2158072e)

##### price vs advantage
- **price**: objects become a bit larger
- **adv**: supports **pattern matching**

#### Pattern Matching
```scala
UnOp("-", UnOp("-"), e)  => e
BinOp("+", e, Number(0)) => e
BinOp("*", e, Number(1)) => e
```

[`def simplifyTop`](../src/main/scala/caseClassesAndPatternMatching/Expr.scala)

##### kinds of patterns  => ([#15.2](#15.2))
- constant pattern
- variable pattern
- wildcard pattern
- constructor pattern: arguments to the constructor are themselves patterns


#### difference between `match`(scala) vs `switch`(java)
1. `match` is an **expression**
2. alternative expressions never **fall through**
3. match nothing => throws `MatchError`

```scala
expr match {
  case BinOp(op, left, right) => println(s"$expr is a binary operation")
  case _ =>
}
```

- second case (`case _ => `)
	- runs nothing
	- make sure all cases are covered
	- if it's not there, `MatchError` would be thrown
	- returns unit value `()`

## 15.2 Kinds of patterns
### Wildcard patterns
- usually used as **default**: matches any object whatever
- ignore parts of an object (do not want to care about)
```scala
case BinOp(op, left, right) => ???
case BinOp(_, _, _) => ???
```

### Constant patterns
- matches any **literal** or any **val** or any **singleton object** can be used
```scala
def descript(x: Any) = x match {
  case 5 => "five"
  case true => "truth"
  case "hello" => "hi!"
  case Nil => "the empty list"
  case _ => "something else"
}
```

### Variable patterns
- matches any **object** like wildcard
	- unlike wildcard -> Scala binds the variable to whatever the object is. (??)
```scala
expr match {
  case 0 => "zero"     // special cases for zero
  case somethingElse => "not zero: " + somethingElse    // default case for all other value (variable pattern, can use that value)
}
```

#### variable or constant?
##### simple rule
- starting with a **lowercase** letter: pattern variable
- **all other** references: constants

##### to use lowercase name for a pattern constant
- if the constant is a field of some obj, it can be prefixed with a qualifier
- use back ticks

### Constructor patterns
- consists of
	- a **name**(`BinOp`) and
	- number of **patterns within parenthesis**(`"+"`, `e`, `Number(0)`)
- **very powerful** with Scala's _deep matches_
	- _not only_ check the top-level object supplied,
	- _but also_ check the contents of the object against further patterns.
```scala
expr match {
  case BinOp("+", e, Number(0)) => println(" a deep match")
  case _ =>
}
```
- top-level object: `BinOp`
- third constructor param: `Number` that's field is `0`

### Sequence patterns
- match against sequence types (List, Array)
- can limit the number of elements (e.g. `case List(0, _, _) => ???` => 3 length list)
	- do not want specifying how long it can be, use `_*` at the last (e.g. `case List(0, _*) => ???`

### Tuple patterns
- can match against tuples

### Typed patterns
- convenient replacement for **type tests** and **type casts**.
```scala
def generalSize(x: Any) = x match {
  case s: String => s.length
  case m: Map[_, _] => m.size
  case _ => 1
```

- `isInstanceOf[A]`, `asInstanceOf[A]`

#### Type erasure
- `case m: Map[Int, Int]` => occurs **unchecked warnings**
	- with `-unchecked` option, compiler gives more detail
- no information about **type** argument is maintained at _runtime_
- only-exception: **`Array`** type -> can match inner element's type

### Variable binding
- use sign `@` to write variable name
- if the pattern succeeds, set the variable to the matched object
```scala
expr match {
  case UnOp("abs", e @ UnOp("abs", _)) => e
  case _ =>
}
```

- `UnOp("abs", _)`is made available as variable `e`

## 15.3 Pattern guards
```scala
BinOp("+", Var("x"), Var("x"))
BinOp("*", Var("x"), Number(2))
```

- both expressions are same so we can think like...
	- -> `case BinOp("+", x, x) => BinOp("*", x, Number(2))` : occurs error
- Scala restricts patterns to be **linear**
	- a pattern variable may only appear once in a pattern
- to solve this we can use **pattern guard**
	-  boolean expression starts with an `if`
```scala
case BinOp("+", x, y) if x == y => BinOp("*", x, Number(2))
```

## 15.4 Pattern overlaps
[Expr.scala # `def simplifyAll`](../src/main/scala/caseClassesAndPatternMatching/Expr.scala)

#### `case UnOp(op, e) => UnOp(op, simplifyAll(e))`
- matches every unary operation
- operand => `op` & other unary operation => `e` (apply recursively)

#### `case BinOp(op, l, r) => BinOp(op, simplifyAll(l), simplifyAll(r))`
- catch all cases for arbitrary binary operations
- catch-all cases com after the more specific simplification rules
- specific rules can be escape condition for recursive functions
- if preceding case matches anything, compiler returns error (`unreachable code`)

## 15.5 Sealed classes
- When write _pattern match_ make sure to have covered all of the possible cases
	- use default case at the end of match
- if there's no sensible default behavior:
	- compiler's help? no, new case class can be defined at any time in arbitrary  compilation units.

### seal
- add `sealed` keyword in front of the class name (which is top of the hierarchy)
- cannot have any new subclass added except the ones in the same file.
- when write a hierarchy of classes intended to be **pattern matched**, consider **sealing them**

```scala
sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr
```

### unchecked case
- gets warning(`match is not exhaustive`) when it has possibilities to `MatchError`
- to avoid this:
	- use default case that throws a `RuntimeException`
	- **use `@unchecked` annotation to the selector expression of the match** : cool

#### default case with throwing `RuntimeException`
```scala
def describe(e: Expr): String = e match {
  case Number(_) => "a number"
  case Var(_) => "a variable"
  case _ => throw new RuntimeException // Should not happen
}
```

#### `@unchecked` annotation
```scala
def describe(e: Expr): String = (e: @unchecked) match {
  case Number(_) => "a number"
  case Var(_) => "a variable"
}
```

## 15.6 The `Option` type
- for optional values
	- `Some(x)`: `x` is a actual value
	- `None`: missing value
- frequently used in Scala
	- `get` of `Map` returns Option
	- vs JAVA: returns value or null (with latent `NullPointerException`)
- **advantages**
	- obvious to readers of code that a variable
	- can recognize about null errors earlier during compile time

#### pattern matching in `Option`
```scala
def show(x: Option[String]) = x match {
  case Some(s) => s
  case None => "?"
}
```

## 15.7 Patterns everywhere
### Patterns in variable definitions
- can use Tuple to assign multiple values
- when we know the precise case class, we can deconstruct it with a pattern
```scala
scala> val exp = new BinOp(" * ", Number(5), Number(1))
exp: BinOp = BinOp( * ,Number(5.0),Number(1.0))

scala> val BinOp(op, left, right) = exp
op: String = *
left: Expr = Number(5.0)
right: Expr = Number(1.0)
```
### Case sequences as partial functions
#### function literal
- **sequence of cases** in curly braces can be used _anywhere_ a **function literal** can be used
	- each case: Entry point to function
	- body of each entry: right-hand side of the case
```scala
val withDefault: Option[Int] => Int = {
  case Some(x) => x
  case None => 0        // 2 `Option[Int] => Int`  type functions
}
```

#### useful for actors library
```scala
react {
  case (name: String, actor: Actor) => {
    actor ! getip(name)
    act()
  }
  case msg => {
    println("Unhandled message: " + msg)
    act()
  }
}
```

#### partial functions
##### run-time exception
- occurs when use unsupported left side value
```scala
val second: List[Int] => Int = {
  case x :: y :: _ => y  // match is not exhaustive warning occurs
}
```
```scala
scala> second(List(1, 2, 3))
scala> second(List())		// Match error occurs
```

##### `PartialFunction[A, B]`
- tell the compiler that's a partial function
```scala
val second: PartialFunction[List[Int], Int] = {
  case x :: y :: _ => y  // match is not exhaustive warning occurs
}
```

##### `isDefinedAt`
- tests whether the function is defined at a particular value
- scala compilers translate partial function by patterns  twice
	- for the implementation of the real function
	- to test whether the function is defined or not
```scala
new PartialFunction[List[Int], Int] {
  def apply(xs: List[Int]) = xs match {
    case x :: y :: _ => y
  }
  def isDefinedAt(xs: List[Int]) = xs match {
    case x :: y :: _ => true
    case _ => false
  }
}
```
- this kind of translation take effects only the functions literal is **`PartialFunction`**
	- if its type is `Function1` or missing, translates to complete function

##### When to use `Partial Functions`?
- try to work with complete function,
	- because partial functions allows for runtime errors
- but, sometimes it's really helpful so if you have to use it
	- be sure that unhandled value never be supplied
	- use `isDefinedAt` before calling function (when using framework expects partial functions, like `react`)

### Patterns in `for` expressions
```scala
scala> val results = List(Some(“apple”), None, Some(“orange”))
scala> for (Some(fruit) <- results) println(fruit)
apple
orange
```
- generated values that do not match the pattern are discarded

## 15.8 A larger example
[ExprFormatter.scala](../src/main/scala/caseClassesAndPatternMatching/ExprFormatter.scala)

[Express.scala](../src/main/scala/caseClassesAndPatternMatching/Express.scala)

## 15.9 Conclusion
### learned about
- case class & pattern matching is gorgeous
- most of OOP languages cannot support feature like this
- patterns matching goes furthermore
	- can use pattern-match to `class` (not `case class`) by `extractors` [Chapter 26](./extractors.md)

### will learn
- lists
