package com.android.sample.model.categoryIngredient

import android.content.Context

interface CategoryIngredientRepository {

    /**
     * Search for categories by name.
     *
     * @param query The name of the category to search for.
     * @return The list of categories that match the query.
     */
    fun searchCategory(query: String, onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit, count: Int = 10)
}