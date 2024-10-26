package com.android.sample.model.filter

open class Filter(
    val time: Int,
    val price: Int,
    val type: TypeOfRecipe,
    val Difficulty: Difficulty,
    val diet: String
) {}

object TypeOfRecipe {
  const val Breakfast = 1
  const val Lunch = 2
  const val Dinner = 3
  const val Dessert = 4
  const val Snack = 5
  const val NotDefined = -1
}

object Difficulty {
  const val EASY = 1
  const val MEDIUM = 2
  const val HARD = 3
  const val Expert = 4
  const val NotDefined = -1
}
