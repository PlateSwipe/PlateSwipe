package com.android.sample.model.ingredient

interface IngredientRepository {
    fun searchIngredients(name: String, onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit)
}