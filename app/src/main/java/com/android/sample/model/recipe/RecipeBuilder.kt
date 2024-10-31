package com.android.sample.model.recipe


/** Builder class for creating a Recipe instance. */
class RecipeBuilder {
    var idMeal: String = ""
    var strMeal: String = ""
    var strCategory: String? = null
    var strArea: String? = null
    var strInstructions: String = ""
    var strMealThumbUrl: String = ""
    val ingredientsAndMeasurements: MutableList<Pair<String, String>> = mutableListOf()
    var time: String? = null
    var difficulty: String? = null
    var price: String? = null

    /** Adds an ingredient and its measurement to the recipe. */
    fun addIngredient(ingredient: String, measurement: String) = apply {
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