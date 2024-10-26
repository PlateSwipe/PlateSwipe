package com.android.sample.model.filter

open class Filter(
    val time: Int,
    val price: Int,
    val type: TypeOfRecipe,
    val Difficulty: List<Difficulty>,
    val diet: List<String>
)

object TypeOfRecipe {
  const val Breakfast = "Breakfast"
  const val Lunch = "Lunch"
  const val Dinner = "Dinner"
  const val Dessert = "Dessert"
  const val Snack = "Snack"
  const val Undefined = "Undefined"
}

object Difficulty {
  const val EASY = "Easy"
  const val MEDIUM = "Medium"
  const val HARD = "Hard"
  const val Expert = "Expert"
  const val Undefined = "Undefined"
}
