package modularProgramming

trait SimpleFoods {
  object Pear extends Food("Pear")

  def allFoods = List(Apple, Pear)

  def allCategories = Nil
}

trait SimpleRecipes {
  this: SimpleFoods =>

  object FruitSalad extends Recipe(
    "fruit salad",
    List(Apple, Pear),      // Now Pear is in scope
    "Mix it all together"
  )

  def allRecipes = List(FruitSalad)
}

object SimpleDatabase extends Database with SimpleFoods with SimpleRecipes

object simpleBrowser extends Browser{
  val database = SimpleDatabase
}
