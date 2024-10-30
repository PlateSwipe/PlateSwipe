package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME

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
    val strCategory: String? = null,
    val strArea: String? = null,
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

  // Method to convert Recipe to a Firestore-compatible map with custom field names
  fun toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FIRESTORE_RECIPE_NAME to strMeal,
        FIRESTORE_RECIPE_CATEGORY to strCategory,
        FIRESTORE_RECIPE_AREA to strArea,
        FIRESTORE_RECIPE_PICTURE_ID to strMealThumbUrl,
        FIRESTORE_RECIPE_INSTRUCTIONS to strInstructions,
        FIRESTORE_RECIPE_INGREDIENTS to ingredientsAndMeasurements.map { it.first },
        FIRESTORE_RECIPE_MEASUREMENTS to ingredientsAndMeasurements.map { it.second },
        FIRESTORE_RECIPE_TIME to time,
        FIRESTORE_RECIPE_DIFFICULTY to difficulty,
        FIRESTORE_RECIPE_PRICE to price)
  }
}
