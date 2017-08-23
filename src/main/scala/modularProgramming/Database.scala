package modularProgramming

abstract class Database {
  def allFoods: List[Food]

  def allRecipes: List[Recipe]

  def foodNamed(name: String): Option[Food] =
    allFoods.find(f => f.name == name)

  case class FoodCategory(name: String, foods: List[Food])

  def allCategories: List[FoodCategory]
}
