package com.android.sample.model.recipe

/** Builder class for creating a Recipe instance. */
class RecipeBuilder {
  private var idMeal: String = ""
  private var strMeal: String = ""
  private var strCategory: String? = null
  private var strArea: String? = null
  private var strInstructions: String = ""
  private var strMealThumbUrl: String = ""
  private val ingredientsAndMeasurements: MutableList<Pair<String, String>> = mutableListOf()
  private var time: String? = null
  private var difficulty: String? = null
  private var price: String? = null

  /**
   * Sets the ID of the recipe.
   *
   * @param idMeal The ID of the recipe.
   */
  fun setId(idMeal: String) = apply { this.idMeal = idMeal }

  /**
   * Sets the name of the recipe.
   *
   * @param strMeal The name of the recipe.
   */
  fun setName(strMeal: String) = apply { this.strMeal = strMeal }

  /**
   * Sets the category of the recipe.
   *
   * @param strCategory The category of the recipe.
   */
  fun setCategory(strCategory: String) = apply { this.strCategory = strCategory }

  /**
   * Sets the area of the recipe.
   *
   * @param strArea The area of the recipe.
   */
  fun setArea(strArea: String) = apply { this.strArea = strArea }

  /**
   * Sets the instructions for the recipe.
   *
   * @param strInstructions The instructions for the recipe.
   */
  fun setInstructions(strInstructions: String) = apply { this.strInstructions = strInstructions }

  /**
   * Sets the URL of the thumbnail image for the recipe.
   *
   * @param strMealThumbUrl The URL of the thumbnail image for the recipe.
   */
  fun setPictureID(strMealThumbUrl: String) = apply { this.strMealThumbUrl = strMealThumbUrl }

  /**
   * Sets the time required to prepare the recipe.
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
    require(strMeal.isNotBlank()) { "Recipe name is required and cannot be blank." }
    require(strInstructions.isNotBlank()) {
      "Recipe instructions are required and cannot be blank."
    }
    require(ingredientsAndMeasurements.isNotEmpty()) { "At least one ingredient is required." }

    return Recipe(
        idMeal = idMeal,
        strMeal = strMeal,
        strCategory = strCategory,
        strArea = strArea,
        strInstructions = strInstructions,
        strMealThumbUrl = strMealThumbUrl,
        ingredientsAndMeasurements = ingredientsAndMeasurements,
        time = time,
        difficulty = difficulty,
        price = price)
  }

  /** Clears all fields in the builder. */
  fun clear() {
    this.idMeal = ""
    this.strMeal = ""
    this.strCategory = null
    this.strArea = null
    this.strInstructions = ""
    this.strMealThumbUrl = ""
    this.ingredientsAndMeasurements.clear()
    this.time = null
    this.difficulty = null
    this.price = null
  }

  /** Returns the ID of the recipe. */
  fun getId(): String = idMeal

  /** Returns the name of the recipe. */
  fun getName(): String = strMeal

  /** Returns the category of the recipe. */
  fun getCategory(): String? = strCategory

  /** Returns the area of the recipe. */
  fun getArea(): String? = strArea

  /** Returns the instructions for the recipe. */
  fun getInstructions(): String = strInstructions

  /** Returns the URL of the thumbnail image for the recipe. */
  fun getPictureID(): String = strMealThumbUrl

  /** Returns the time required to prepare the recipe. */
  fun getTime(): String? = time

  /** Returns the difficulty level of the recipe. */
  fun getDifficulty(): String? = difficulty

  /** Returns the price of the recipe. */
  fun getPrice(): String? = price

  /** Returns the ingredients and their measurements for the recipe. */
  fun getIngredientsAndMeasurements(): List<Pair<String, String>> =
      ingredientsAndMeasurements.toList()
}
