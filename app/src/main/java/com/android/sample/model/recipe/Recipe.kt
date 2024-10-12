package com.android.sample.model.recipe

data class Recipe(
    val idMeal: String, // Changed from Long to String
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String,
    val strMealThumbUrl: String,
    val ingredientsAndMeasurements: List<Pair<String, String>> // Combined field as a list of pairs
)
