package com.android.sample.model.recipe

/**
 * Data class representing a recipe.
 *
 * @property idMeal The unique identifier for the meal, represented as a String.
 * @property strMeal The name of the meal.
 * @property strCategory The category of the meal (e.g., Vegetarian, Non-Vegetarian). Nullable.
 * @property strArea The area or cuisine of the meal (e.g., Italian, Indian). Nullable.
 * @property strInstructions Instructions on how to prepare the meal.
 * @property strMealThumbUrl URL of the thumbnail image of the meal.
 * @property ingredientsAndMeasurements A list of ingredient and measurement pairs for the recipe.
 * @property time The time required to prepare the meal. Nullable.
 * @property difficulty The difficulty level of the meal. Nullable.
 * @property price The price of the meal. Nullable.
 */
data class Recipe(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String,
    val strMealThumbUrl: String,
    val ingredientsAndMeasurements: List<Pair<String, String>>,
    val time: String? = null,
    val difficulty: String? = null,
    val price: String? = null
) {
  init {
    require(ingredientsAndMeasurements.isNotEmpty()) {
      "Ingredients and measurements must not be empty"
    }
  }
}
