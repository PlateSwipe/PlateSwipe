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

  /** Builds and returns a Recipe instance if all required fields are set. */
  fun build(): Recipe {
    // Validation for essential fields
    require(strMeal.isNotBlank()) { "Recipe name cannot be blank" }
    require(strInstructions.isNotBlank()) { "Recipe instructions cannot be blank" }
    require(ingredientsAndMeasurements.isNotEmpty()) {
      "Ingredients and measurements must not be empty"
    }

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

  /** Returns the ID of the recipe. */
  fun getId(): String {
    return idMeal
  }
  /** Returns the name of the recipe. */
  fun getName(): String {
    return strMeal
  }

  /** Returns the category of the recipe. */
  fun getCategory(): String? {
    return strCategory
  }
  /** Returns the area of the recipe. */
  fun getArea(): String? {
    return strArea
  }
  /** Returns the instructions for the recipe. */
  fun getInstructions(): String {
    return strInstructions
  }
  /** Returns the URL of the thumbnail image for the recipe. */
  fun getPictureID(): String {
    return strMealThumbUrl
  }
  /** Returns the time required to prepare the recipe. */
  fun getTime(): String? {
    return time
  }
  /** Returns the difficulty level of the recipe. */
  fun getDifficulty(): String? {
    return difficulty
  }
  /** Returns the price of the recipe. */
  fun getPrice(): String? {
    return price
  }
  /** Returns the ingredients and their measurements for the recipe. */
  fun getIngredientsAndMeasurements(): MutableList<Pair<String, String>> {
    return ingredientsAndMeasurements
  }
}
