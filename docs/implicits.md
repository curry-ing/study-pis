# 21. Implicit Conversions and Parameters
### to invoke someone else's libraries to your own code neatly
- ruby - modules
- Smalltalk - lets packages add to each other's classes 
- C# - static extension methods
- **Scala - Implicit conversions and parameters** 

## 21.1 Implicit conversions
- helpful: Working with two bodies of software that were developed without each other in mind.

### Java legacy
```java
val button = new Button
button.addActionListener(
  new ActionListener {
    def actionPerformed(event: ActionEvent) { 
      println("pressed!")
      }
    })
```
- too many boilerplates
	- for `actionPerformed` method we need to instantiate `ActionListener` class 
	- only `println` is needed in this action 

### Scala way! 
#### 1. use anonymous function
```scala
button.addActionListener( 
  (_: ActionEvent) => println("pressed!")
)
```

#### 2. function for type conversion 
```scala 
implicit def function2ActionListener(f: ActionEvent => Unit) = 
  new ActionListener {
    def actionPerformed(event: ActionEvent) = f(event)
  }

button.addActionListener(
  function2ActionListener(
    (_: ActionEvent) => println("pressed!")
  )
)
```

#### 3. implicit conversion magic
```scale
button.addActionListener(
  (_: ActionEvent) => println("pressed!")
)
```


## 21.2 Rules for implicit 
### Marking Rule
> Only definitions marked `implicit` are available
- can use for **variable**, **function** and **object**
- should **explicitly** mark `implicit` 

### Scope Rule
> An inserted implicit conversion must be in scope as a **single identifier** 
> or, be associated with the source of target type of the conversion

#### Exception for `Single Identifier` Rule
- **companion object** of a source or expected target types of the conversion
	- no need to import the conversion separately 

### One-at-a-time Rule
> Only one implicit is tried
- multiple implicit? => Hell
- possible to circumvent this, that by having implicit take implicit paramters. (?)

### Explicits-First Rule
> Whenever code type checks as it is written, no implicit are attempted
- you can always replace implicit identifiers by explicit ones

### Naming an implicit conversion
- can have arbitrary names.
- matters when...
	- want to write it explicitly
	- for determining when implicit conversions are available at any place in the program

```scala
object MyConversions { 
  implicit def stringWrapper(s: String): IndexedSeq[Char] = ...
  implicit def intToString(x: Int): String = ...
}
```
- when want to use `stringWrapper` and do not want convert Integers automatically:
	- `import MyConversions.stringWrapper`

### Where implicit are tried
- conversions to an expected type 
- conversions of the receiver of a selection 
- implicit parameter

## 21.3 Implicit conversions to an expected type 
### Rule: When compiler needs `Y` and it's `X` 
-  looking for implicit function  converts `X` to `Y`

```scala
implicit def doubleToInt(x: Double) = x.toInt
val i: Int = 3.5 // actually => doubleToInt(3.5) 
```

### Normally...
- Double to Int is not a dubious idea, but Int to Double makes sense. 
- General Implicit conversions (like, Int to Double) are defined in `scala.Predef`
	- `implicit def int2double(x: Int): Double = x.toDouble

## 21.4 Converting the receiver
### Receiver Conversion
1. allow smoother integration of a new class into existing class hierarchy ([Interoprating with new types]())
1. support writing domain-specific languages (DLSs) within the language. ([Simulating new syntax]())

#### Interoperating with new types 
```scala
class Rational(n: Int, d: Int) {
  ...
  def + (that: Rational): Rational = ???
  def + (that: Int): Rational = ???
}

val oneHalf = new Rational(1, 2)
oneHalf + oneHalf // good 
oneHalf + 1                // good
1 + oneHalf // ?? (1 is Int and Int does not have + method that takes Rational)
```
##### to allow this kind of arithmetic (`1 + Rational`) define next implicit conversion 
```scala
implicit def intToRational(x: Int) = new Rational(x, 1) 
```
- tries to type check the expression `1 + oneHalf` as it is 
- when it fails, compiler searches for an implicit conversion from `Int` to another type that has a `+` method

##### if finds conversion and applies like this
```scala
intToRational(1) + oneHalf
```

#### Simulating new syntax
##### ArrowAssoc
```scala
Map(1 -> "one", 2 -> "two", 3 -> "three")  // how '->' is supported?
```
- `->` is not a syntax, just a method of a class `ArrowAssoc` in `scalaPredef`

##### Rich Wrapper pattern 
```scala 
package scala
object Predef {
  class ArrowAssoc[A](x: A) {
   def -> [B](y: B): Tuple2[A, B] = Tuple2(x, y)
  }
  implicit def any2ArrowAssoc[A](x: A): ArrowAssoc[A] = new ArrowAssoc(x) 
   ...
}
```
- common in libraries that provide syntax-like extensions to the language
- should be ready to recognize the pattern 
	- someone calling method that appears not to exist in the receiver class, probably implicit.
	- when see class named `RichSomething`, it likely adding syntax-like method to type `Something` (e.g. `RichInt`, RichBoolean`)

## 21.5 Implicit parameters
- we can call `someCall(a)(b, c, d)` as `someCall(a)` & `SomeClass(a)(b)` as `SomeClass(a)`
	- not only marked as **implicit** where they defined 
	- but also, mark as implicit last parameter list in function or class definition 
	- must be in context, if not, import it (the implicit value)

```scala
class PreferredPrompt(val preference: String)
class PreferredDrink(val preference: String)

object Greeter { 
  def greet(name: String)(implicit prompt: PreferredPrompt, drink: PreferredDrink) {
    println(“Welcome, “ + name + “. The system is ready.”)
    print(“But while you work, “)
    println(“why not enjoy a cup of “ + drink.preference + “?”)
    println(prompt.preference)
  }
}

object JoesPrefs { 
  implicit val prompt = new PreferredPrompt(“Yes, master> “)
  implicit val drink = new PreferredDrink(“tea”)
}
```
##### doesn’t work
```scala
Greeter.greet(“Joe”)
```
##### works
```scala
import JoesPrefs._

Greeter.greet(“Joe”)(prompt, drink)
Greeter.greet(“Joe”) 
```

#### Why we used `PreferredThing` instead of `String` ?
- compiler selects implicit by matching **types of params** against **types of values** in scope
- implicit parameter usually have **rare** or **special** enough type to prevent accidental matches

#### A style rule for implicit parameters
-  `String`(x) => `PreferredDrink`(o)
- `implicit orderer: (T, T) => Boolean`(x) => `implicit orderer: T => Ordered[T]`(o)
- **Rule: use at least one role-determining name within the type of an implicit parameter**  


## 21.6 View bounds 
#### in Previous example 
- not only, will the compiler try to _supply_ that parameter with an implicit value
- but the compiler will also _use_ that parameter as an available implicit in the body of the method 
- => both uses of `orderer` within the body of the method can be left out 

##### Normal way
```scala
def maxList[T](elements: List[T])(implicit orderer: T => Ordered[T]): T = 
  elements match { 
    case Nil => throw new IllegalArgumentException("empty list!")
    case List(x) => x 
    case x :: rest => 
      val maxRest = maxList(rest)         // (orderer) is implicit
      if (x > maxRest) x else maxRest   // orderer(x) is implicit
  }
```

##### View bound
```scala
def maxList[T <% Ordered[T]](elements: List[T]): T = elements match {
  case Nil => throw new IlleganArgumentException("empty list!")
  case List(x) => x
  case x :: rest => 
    val maxRest = maxList(rest)
    if (x > maxRest) x else maxRest
}
```
- `T <% Ordered` means I can use any `T`, so long as `T` can be treated as an `Ordered[T]`
	- this is not same with `T <: Ordered[T]` which means  `T` is an `Ordered[T]`
	- 

## 21.7 When multiple conversions apply
```scala
def printLength(seq: Seq[Int]) = println(seq.length)

implicit def intToRange(i: Int) = 1 to i

implicit def intToDigits(i: Int) = i.toString.toList.map(_.toInt)
```

#### ~ scala v2.7

#### scala v2.8 ~
## 21.8 Debugging implicit 

## 21.9 Conclusion
