package com.android.sample.model.recipe

data class Recipe(
    val idMeal: Long,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String,
    val strMealThumbUrl: String,
    val ingredients: List<Long>,
    val measurements: List<String>,
    val rating: Double, // for stars
    val preparationTime: PreparationTime,
    val cost: Int // for dollars
)

data class PreparationTime(val hours: Int, val minutes: Int) {
  override fun toString(): String {
    return "${hours} h ${minutes} min"
  }
}
