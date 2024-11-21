package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS_TEXT
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTION_ICON
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTION_TIME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_URL

/**
 * Data class representing a recipe.
 *
 * @property uid The unique identifier for the meal, represented as a String.
 * @property name The name of the meal.
 * @property category The category of the meal (e.g., Vegetarian, Non-Vegetarian). Nullable.
 * @property origin The area or cuisine of the meal (e.g., Italian, Indian). Nullable.
 * @property instructions Instructions on how to prepare the meal.
 * @property strMealThumbUrl URL of the thumbnail image of the meal.
 * @property ingredientsAndMeasurements A list of ingredient and measurement pairs for the recipe.
 * @property time The time required to prepare the meal. Nullable.
 * @property difficulty The difficulty level of the meal. Nullable.
 * @property price The price of the meal. Nullable.
 */
data class Recipe(
    val uid: String,
    val name: String,
    val category: String? = null,
    val origin: String? = null,
    val instructions: List<Instruction>,
    val strMealThumbUrl: String,
    val ingredientsAndMeasurements: List<Pair<String, String>>,
    val time: String? = null,
    val difficulty: String? = null,
    val price: String? = null,
    var url: String? = null
) {

  init {
    require(ingredientsAndMeasurements.isNotEmpty()) {
      "Ingredients and measurements must not be empty"
    }
  }

  /**
   * Converts the recipe object to a Firestore map.
   *
   * @return A map representing the recipe object.
   */
  fun toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        FIRESTORE_RECIPE_NAME to name,
        FIRESTORE_RECIPE_CATEGORY to category,
        FIRESTORE_RECIPE_AREA to origin,
        FIRESTORE_RECIPE_PICTURE_ID to strMealThumbUrl,
        FIRESTORE_RECIPE_INSTRUCTIONS_TEXT to instructions.map { it.description },
        FIRESTORE_RECIPE_INSTRUCTION_TIME to instructions.map { it.time ?: "" },
        FIRESTORE_RECIPE_INSTRUCTION_ICON to instructions.map { it.iconType ?: "" },
        FIRESTORE_RECIPE_INGREDIENTS to ingredientsAndMeasurements.map { it.first },
        FIRESTORE_RECIPE_MEASUREMENTS to ingredientsAndMeasurements.map { it.second },
        FIRESTORE_RECIPE_TIME to time,
        FIRESTORE_RECIPE_DIFFICULTY to difficulty,
        FIRESTORE_RECIPE_PRICE to price,
        FIRESTORE_RECIPE_URL to url,
    )
  }

  /** object to get the list of categories. */
  companion object {
    // unused categories: "Goat" because have only 1 recipe
    private val listCategories =
        listOf(
            "Beef",
            "Breakfast",
            "Chicken",
            "Dessert",
            "Lamb",
            "Miscellaneous",
            "Pasta",
            "Pork",
            "Seafood",
            "Side",
            "Starter",
            "Vegan",
            "Vegetarian")

    /** Returns the list of categories. */
    fun getCategories(): List<String> {
      return listCategories
    }
  }
}
