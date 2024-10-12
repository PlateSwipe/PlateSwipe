package com.android.sample.model.recipe

interface RecipeRepository {

  fun random(nbOfElements: Long, onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit)
}
