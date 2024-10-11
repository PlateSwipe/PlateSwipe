package com.android.sample.model.recipe

interface RecipeRepository {

  fun random(nbOfElements: Long, onSuccess: (Recipe?) -> Unit, onFailure: (Exception) -> Unit)
}
