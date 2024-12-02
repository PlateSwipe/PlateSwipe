package com.android.sample.model.recipe.localData

import com.android.sample.model.recipe.Recipe

interface RecipeLocalRepository {

  fun add(recipe: Recipe)

  fun update(recipe: Recipe)

  fun delete(recipe: Recipe)

  fun deleteAll(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun getAll(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit)
}
