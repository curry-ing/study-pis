package modularProgramming

object GotApples2 {
  def main(args: Array[String]) = {
    val db: Database =
      if (args(0) == "student")
        StudentDatabase
      else
        SimpleDatabase

    object browser extends Browser {
      val database: db.type = db
    }

    for (category <- db.allCategories)
      browser.displayCategory(category)

    val apple = SimpleDatabase.foodNamed("Apple").get

    for (recipe <- browser.recipesUsing(apple))
      println(recipe)
  }
}
