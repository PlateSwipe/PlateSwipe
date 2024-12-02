package com.android.sample.model.recipe.localData

import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.toEntity
import com.android.sample.model.recipe.toRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RoomRecipeRepository(
    private val recipeDAO: RecipeDAO,
    private val dispatcher: CoroutineDispatcher
) : RecipeLocalRepository {
  override fun add(recipe: Recipe) {
    CoroutineScope(dispatcher).launch { recipeDAO.insert(recipe.toEntity()) }
  }

  override fun update(recipe: Recipe) {
    CoroutineScope(dispatcher).launch { recipeDAO.update(recipe.toEntity()) }
  }

  override fun delete(recipe: Recipe) {
    CoroutineScope(dispatcher).launch { recipeDAO.delete(recipe.toEntity()) }
  }

  override fun deleteAll(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    CoroutineScope(dispatcher).launch {
      try {
        recipeDAO.deleteAll()
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  override fun getAll(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
    CoroutineScope(dispatcher).launch {
      try {
        val recipes = recipeDAO.getAll().map { it.toRecipe() }
        onSuccess(recipes)
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }
}
