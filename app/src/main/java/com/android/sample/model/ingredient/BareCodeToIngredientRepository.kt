package com.android.sample.model.ingredient

interface BareCodeToIngredientRepository {
    fun get(barCode: Long, onSuccess: (Ingredient) -> Unit, onFailure: (Exception) -> Unit)
}