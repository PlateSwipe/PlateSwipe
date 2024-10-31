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


    fun setId(idMeal: String) = apply { this.idMeal = idMeal }

    fun setName(strMeal: String) = apply { this.strMeal = strMeal }

    fun setCategory(strCategory: String) = apply { this.strCategory = strCategory }

    fun setArea(strArea: String) = apply { this.strArea = strArea }

    fun setInstructions(strInstructions: String) = apply { this.strInstructions = strInstructions }

    fun setPictureID(strMealThumbUrl: String) = apply { this.strMealThumbUrl = strMealThumbUrl }

    fun setTime(time: String) = apply { this.time = time }

    fun setDifficulty(difficulty: String) = apply { this.difficulty = difficulty }

    fun setPrice(price: String) = apply { this.price = price }

    /** Adds an ingredient and its measurement to the recipe. */
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
}