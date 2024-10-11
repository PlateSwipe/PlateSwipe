package com.android.sample.model.recipe

data class Recipe(
    val idMeal: Long,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String,
    val ingredients: List<String>, // List of ingredients
    val measurements: List<String> // Corresponding measurements
)
