package com.android.sample.model.recipe

data class Recipe(
    val idMeal: Long,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String,
    val strMealThumbUrl: String,
    val ingredients: List<Long>,
    val measurements: List<String>
)
