package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.ERROR_LIST_INGREDIENT_EMPTY
import com.android.sample.resources.C.Tag.ERROR_STR_INSTR_BLANK
import com.android.sample.resources.C.Tag.ERROR_STR_MEAL_BLANK
import com.android.sample.resources.C.Tag.ERROR_STR_THUMBNAIL

/** Builder class for creating a Recipe instance. */
class RecipeBuilder {
  private var uid: String = ""
  private var name: String = ""
  private var category: String? = null
  private var origin: String? = null
  private var instructions: String = ""
  private var strMealThumbUrl: String = ""
  private val ingredientsAndMeasurements: MutableList<Pair<String, String>> = mutableListOf()
  private var time: String? = null
  private var difficulty: String? = null
  private var price: String? = null
  private var url: String? = null

  /**
   * Sets the ID of the recipe.
   *
   * @param idMeal The ID of the recipe.
   */
  fun setId(idMeal: String) = apply { this.uid = idMeal }

  /**
   * Sets the name of the recipe.
   *
   * @param strMeal The name of the recipe.
   */
  fun setName(strMeal: String) = apply { this.name = strMeal }

  /**
   * Sets the category of the recipe.
   *
   * @param category The category of the recipe.
   */
  fun setCategory(category: String) = apply { this.category = category }

  /**
   * Sets the area of the recipe.
   *
   * @param origin The area of the recipe.
   */
  fun setOrigin(origin: String) = apply { this.origin = origin }

  /**
   * Sets the instructions for the recipe. WARNING : This method should be updated in the next
   * version of the instruction implementation.
   *
   * @param strInstructions The instructions for the recipe.
   */
  fun setInstructions(strInstructions: String) = apply { this.instructions = strInstructions }

  /**
   * Sets the UID of the thumbnail image for the recipe.
   *
   * @param strMealThumbUrl The UID of the thumbnail image for the recipe, it correspond to the
   *   FireBase Image Store UID.
   */
  fun setPictureID(strMealThumbUrl: String) = apply { this.strMealThumbUrl = strMealThumbUrl }

  /**
   * Sets the time required to prepare the recipe. WARNING : This method should be updated in the
   * next version of the instruction implementation.
   *
   * @param time The time required to prepare the recipe.
   */
  fun setTime(time: String) = apply { this.time = time }

  /**
   * Sets the difficulty level of the recipe.
   *
   * @param difficulty The difficulty level of the recipe.
   */
  fun setDifficulty(difficulty: String) = apply { this.difficulty = difficulty }

  /**
   * Sets the price of the recipe.
   *
   * @param price The price of the recipe.
   */
  fun setPrice(price: String) = apply { this.price = price }

  /**
   * Sets the URL of the thumbnail image for the recipe.
   *
   * @param url The URL of the thumbnail image for the recipe.
   */
  fun setUrl(url: String) = apply { this.url = url }

  /**
   * Adds an ingredient and its measurement to the recipe.
   *
   * @param ingredient The ingredient to add.
   * @param measurement The measurement for the ingredient.
   */
  fun addIngredientAndMeasurement(ingredient: String, measurement: String) = apply {
    ingredientsAndMeasurements.add(ingredient to measurement)
  }

  /**
   * Deletes an ingredient and its measurement from the recipe.
   *
   * @param ingredient The ingredient to delete.
   * @param measurement The measurement for the ingredient.
   */
  fun deleteIngredientAndMeasurement(ingredient: String, measurement: String) = apply {
    ingredientsAndMeasurements.removeIf { it.first == ingredient && it.second == measurement }
  }

  /**
   * Updates an ingredient and its measurement in the recipe.
   *
   * @param ingredient The ingredient to update.
   * @param measurement The measurement for the ingredient.
   * @param newIngredient The new ingredient to update.
   * @param newMeasurement The new measurement for the ingredient.
   */
  fun updateIngredientAndMeasurement(
      ingredient: String,
      measurement: String,
      newIngredient: String,
      newMeasurement: String
  ) = apply {
    val index =
        ingredientsAndMeasurements.indexOfFirst {
          it.first == ingredient && it.second == measurement
        }
    if (index >= 0) {
      ingredientsAndMeasurements[index] = newIngredient to newMeasurement
    }
  }

  /**
   * Builds and returns a Recipe instance if all required fields are set. Ensures that essential
   * fields such as recipe name, instructions, and at least one ingredient are provided.
   *
   * @return A Recipe instance with the configured properties.
   * @throws IllegalArgumentException if any required field is missing or invalid.
   */
  fun build(): Recipe {
    // Validation for essential fields
    require(name.isNotBlank()) { ERROR_STR_MEAL_BLANK }
    require(instructions.isNotBlank()) { ERROR_STR_INSTR_BLANK }
    require(ingredientsAndMeasurements.isNotEmpty()) { ERROR_LIST_INGREDIENT_EMPTY }
    require(strMealThumbUrl.isNotBlank()) { ERROR_STR_THUMBNAIL }
    return Recipe(
        uid = uid,
        name = name,
        category = category,
        origin = origin,
        instructions = instructions,
        strMealThumbUrl = strMealThumbUrl,
        ingredientsAndMeasurements = ingredientsAndMeasurements,
        time = time,
        difficulty = difficulty,
        price = price,
        url = url)
  }

  /** Clears all fields in the builder. */
  fun clear() {
    this.uid = ""
    this.name = ""
    this.category = null
    this.origin = null
    this.instructions = ""
    this.strMealThumbUrl = ""
    this.ingredientsAndMeasurements.clear()
    this.time = null
    this.difficulty = null
    this.price = null
    this.url = null
  }

  /**
   * Returns the ID of the recipe.
   *
   * @return The ID of the recipe.
   */
  fun getId(): String = uid

  /**
   * Returns the name of the recipe.
   *
   * @return The name of the recipe.
   */
  fun getName(): String = name

  /**
   * Returns the category of the recipe.
   *
   * @return The category of the recipe.
   */
  fun getCategory(): String? = category

  /**
   * Returns the area of the recipe.
   *
   * @return The area of the recipe.
   */
  fun getArea(): String? = origin

  /**
   * Returns the instructions for the recipe.
   *
   * @return The instructions for the recipe.
   */
  fun getInstructions(): String = instructions

  /**
   * Returns the URL of the thumbnail image for the recipe.
   *
   * @return The URL of the thumbnail image for the recipe.
   */
  fun getPictureID(): String = strMealThumbUrl

  /**
   * Returns the time required to prepare the recipe. WARNING : This method should be updated in the
   * next version of the instruction implementation.
   *
   * @return The time required to prepare the recipe.
   */
  fun getTime(): String? = time

  /**
   * Returns the difficulty level of the recipe.
   *
   * @return The difficulty level of the recipe.
   */
  fun getDifficulty(): String? = difficulty

  /**
   * Returns the price of the recipe.
   *
   * @return The price of the recipe.
   */
  fun getPrice(): String? = price

  /**
   * Returns the URL of the thumbnail image for the recipe.
   *
   * @return The URL of the thumbnail image for the recipe.
   */
  fun getUrl(): String? = url

  /**
   * Returns the ingredients and their measurements for the recipe.
   *
   * @return The ingredients and their measurements for the recipe.
   */
  fun getIngredientsAndMeasurements(): List<Pair<String, String>> =
      ingredientsAndMeasurements.toList()

  /**
   * Gets the i th instruction of the recipe. WARNING : This method should be updated in the next
   * version of the instruction implementation.
   *
   * @param i The index of the instruction.
   */
  fun getInstruction(i: Int): String {
    return instructions
  }
}
