# 29 Modular Programming Using Objects

### Programming in the large
- organizing and assembling the smaller pieces 
- package & access modifiers  ([chapter 13](https://ridicorp.atlassian.net/wiki/spaces/DevSpace/pages/113803391/Scala+language+2))
- use Scala's OOP feature to make program more modular
	1. simple singleton object
	1. traits and classes for abstraction
	1. reconfigure to multiple modules 
	1. pragmatic technique for using traits to device a module across multiple files 

## 29.1 The Problem
### Goal
- being able to compile different modules 
- being able to unplug one implementation of a module and plug in another

### Approaches
#### DI (dependency injection)
- Specify dependencies between modules 
- **wire** an application together vi external XML
- reference: http://www.vogella.com/tutorials/SpringDependencyInjection/article.html

## 29.2 A recipe application
### Layers
#### Domain Layer
- define domain objects
- capture business concepts and rules and encapsulate state 
- presented to an external database 

#### Application Layer
- provide an API to clients
- implement services to the object of domain layer

### Supposition - mock
> plug in real or mock versions of certain objects

- more easy to write **unit tests** 
- treat the objects wants to mock as **modules** 

#### Scala's Scalability
- same constructs are used for structures both small & large
	- no need for objects to be **small** things
	- no need to use some other kind of construct for **big** things like modules 
- mock the object that represent database
	- make that one of the modules 
	- treat a **database browser object** as a module from application layer
	- browser help search and browse the database 

### Recipe
[Food.scala](https://github.com/masunghoon/study-pis/blob/18a625457e9e3d306bc1acf833a69ba310d45747/src/main/scala/modularProgramming/Food.scala)  
[Recipe.scala](https://github.com/masunghoon/study-pis/blob/18a625457e9e3d306bc1acf833a69ba310d45747/src/main/scala/modularProgramming/Recipe.scala)
- represent entities (that will be persisted in the database) 

[FruitSalad.scala](https://github.com/masunghoon/study-pis/blob/18a625457e9e3d306bc1acf833a69ba310d45747/src/main/scala/modularProgramming/FruitSalad.scala)
- singleton instances of these classes 

[Simple.scala](https://github.com/masunghoon/study-pis/blob/18a625457e9e3d306bc1acf833a69ba310d45747/src/main/scala/modularProgramming/Simple.scala)
- Scala uses **object** for **modules** 
	- start modularizing by making smaller singleton objects
```scala
val apple = SimpleDatabase.foodNamed("Apple").get
SimpleBrowser.recipesUsing(apple)
```

### Category
[Simple.scala](https://github.com/masunghoon/study-pis/blob/14891ed7037a35a3b99a21ff785c7e59ff175b1d/src/main/scala/modularProgramming/Simple.scala) - ([diff](https://github.com/masunghoon/study-pis/commit/14891ed7037a35a3b99a21ff785c7e59ff175b1d#diff-06f9d335be3278ec357abd822b156fc3))
- **`private var categories`**
	- items marked `private` are part of the implementation of a module
	- particularly easy to change without affecting other modules

## 29.3 Abstraction
### Problems
- **Hard Link**
	- `recipesUsing` method directly call `SimpleDatabase`  [Simple.scala](https://github.com/masunghoon/study-pis/blob/14891ed7037a35a3b99a21ff785c7e59ff175b1d/src/main/scala/modularProgramming/Simple.scala#L23)
-  No clear way to enable the user interface layer
	- e.g.> use different implementations of the browser module

### Solutions
- avoid duplicating code
	- codes are almost same except database implementation
- if module is an **object**, then a template for a module is a **class**
	- class can describe the parts of a module that are common to all of its possible configurations

[Browser.scala](https://github.com/masunghoon/study-pis/blob/151e33236325e6a3507e67deb2baa70d0db13228/src/main/scala/modularProgramming/Browser.scala)
- browser becomes class 
- database to use is is specified as an abstract member 

[Database.scala](https://github.com/masunghoon/study-pis/blob/151e33236325e6a3507e67deb2baa70d0db13228/src/main/scala/modularProgramming/Database.scala)
- also becomes a class 
- defines some common methods: `allFoods`, `allRecipes` and `allCategories`
- some methods can be defined (not abstract member): `foodNames`

[Simple.scala](https://github.com/masunghoon/study-pis/blob/151e33236325e6a3507e67deb2baa70d0db13228/src/main/scala/modularProgramming/Simple.scala) ([diff](https://github.com/masunghoon/study-pis/commit/151e33236325e6a3507e67deb2baa70d0db13228#diff-43afd56f43194631d1bd79643ca2f931))
- `Simple` things extends their abstract classes
- `SimpleBrowser` module
	- instantiating the `Browser` class 
	- specifying which database to use: `val database = SimpleDatabase`

[Student.scala](https://github.com/masunghoon/study-pis/blob/d1a3bdcc22382986d6fbc37219f8f59ebf077cac/src/main/scala/modularProgramming/Student.scala)
- create second mock database, and use the same browser class with it

## 29.4 Splitting modules into traits
### Problems
- Modules often becomes too large 

### Solutions
- use **trait**s to split module into separate files

[FoodCategories.scala](https://github.com/masunghoon/study-pis/blob/ff3a9fdca9a28557331cd192b46acc60de3c58c3/src/main/scala/modularProgramming/FoodCategories.scala) ([diff](https://github.com/masunghoon/study-pis/commit/ff3a9fdca9a28557331cd192b46acc60de3c58c3#diff-9faf6886fa53c4b5de537667d3b77eb0))
- separated as a **trait**

[Database.scala](https://github.com/masunghoon/study-pis/blob/ff3a9fdca9a28557331cd192b46acc60de3c58c3/src/main/scala/modularProgramming/Database.scala) ([diff](https://github.com/masunghoon/study-pis/commit/ff3a9fdca9a28557331cd192b46acc60de3c58c3#diff-e2d5db7c03fc36b64bad708ceb6daabd))
- mixin the `FoodCategories` trait
- remove `FoodCategory` class and `allCategories` method from body

[Simple.scala](https://github.com/masunghoon/study-pis/blob/b3c733c36034faff4835481e5a2c1315992e4d7f/src/main/scala/modularProgramming/Simple.scala) ([diff](https://github.com/masunghoon/study-pis/commit/b3c733c36034faff4835481e5a2c1315992e4d7f))
- divided into two traits each concerned with foods and recipes 

#### little problem
- when `SimpleRecipes` try to use `Pear` object in `SimpleFoods`, fails to compile(`not found: value Pear`)
	- `Pear` is in different trait from the one that uses it 
	- compiler does not have idea that `SimpleRecipes` is only ever mixed together with `SimpleFoods`

#### Solution
##### Self type
- an assumed type for `this` whenever `this` is mentioned within the class  
- **specifies the requirements on any concrete class the trait is mixed into**  
- [Reference(?)](https://github.com/masunghoon/study_programming-scala/blob/master/docs/14_Scala%20Type%20Systems%2C%20Part%20I.md#146-자기-타입-표기)

[Simple.scala](https://github.com/masunghoon/study-pis/blob/e545469eca7862d26dd9148e6174be7e3e3b263c/src/main/scala/modularProgramming/Simple.scala) ([diff](https://github.com/masunghoon/study-pis/commit/e545469eca7862d26dd9148e6174be7e3e3b263c))
- `Pear` is avalable
- `Pear` is thought of as `this.Pear`, implicitly
- any concrete class that mixes in `SimpleRecipes` must also be a subtype of `SimpleFoods`


## 29.5 Runtime linking
- Scala modules can be linked together at runtime 
	- can decide which modules will link to which depending on runtime computations

[GotApples.sacala](https://github.com/masunghoon/study-pis/blob/f3f8caeed793ea30df948b9233f0fb11ef0ad2f8/src/main/scala/modularProgramming/GotApples.scala)


## 29.6 Tracking module instances 
### Problems(?)
- different contents in same code 
	- each module has its own contents, including any nested classes
	- `SimpleDatabase.FoodCategory` != `StudentDatabase.FoodCategory` 
```scala
val category = StudentDatabase.allCategories.head
SimpleBrowser.displayCategory(category) 
```
- It's true that they are different, but if you prefer all `FoodCategory`s to be the same 


### Solutions(?)
- move the definition of `FoodCategory` outside of any class or trait

#### singleton types : `.type`
- extremely specific and holds only one object
- too specific to be useful, so compiler is reluctant to insert them automatically
- but sometimes useful to avoid to compiler's type checking error

[GotAppels2.scala](https://github.com/masunghoon/study-pis/blob/d702a30f15bce3646923d2b4f4d42a4e89d852d0/src/main/scala/modularProgramming/GotApples2.scala)
- the programmer exactly knows that `browser.databases` are same 
- but the complier complaining that they are not the same

[GotAppels2.scala](https://github.com/masunghoon/study-pis/blob/913c2167f186268bc219052cb1074095358b3e71/src/main/scala/modularProgramming/GotApples2.scala) - revised ([diff](https://github.com/masunghoon/study-pis/commit/913c2167f186268bc219052cb1074095358b3e71))
- inform the type checker that they are the same object
- used singleton type (`db.type`)
	- compilers to know that `db` and `browser.database` are the same object
	

## 29.7 Conclusion

#### `Module`
- How to use Scala's objects as module
- variaty of ways to create abstract, reconfigurable modules
- Anything that works on a class used to implement a module
