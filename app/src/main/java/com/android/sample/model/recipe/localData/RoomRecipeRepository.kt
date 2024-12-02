package com.android.sample.model.recipe.localData

import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.toEntity
import com.android.sample.model.recipe.toRecipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Repository implementation for managing recipes using Room database.
 *
 * @property recipeDAO The DAO for accessing recipe data.
 * @property dispatcher The coroutine dispatcher for executing database operations.
 */
class RoomRecipeRepository(
    private val recipeDAO: RecipeDAO,
    private val dispatcher: CoroutineDispatcher
) : RecipeLocalRepository {

  /**
   * Adds a recipe to the repository.
   *
   * @param recipe The recipe to add.
   */
  override fun add(recipe: Recipe) {
    CoroutineScope(dispatcher).launch { recipeDAO.insert(recipe.toEntity()) }
  }

  /**
   * Updates an existing recipe in the repository.
   *
   * @param recipe The recipe to update.
   */
  override fun update(recipe: Recipe) {
    CoroutineScope(dispatcher).launch { recipeDAO.update(recipe.toEntity()) }
  }

  /**
   * Deletes a recipe from the repository.
   *
   * @param recipe The recipe to delete.
   */
  override fun delete(recipe: Recipe) {
    CoroutineScope(dispatcher).launch { recipeDAO.delete(recipe.toEntity()) }
  }

  /**
   * Deletes all recipes from the repository.
   *
   * @param onSuccess Callback to be invoked when the deletion is successful.
   * @param onFailure Callback to be invoked when the deletion fails.
   */
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

  /**
   * Retrieves all recipes from the repository.
   *
   * @param onSuccess Callback to be invoked when the retrieval is successful.
   * @param onFailure Callback to be invoked when the retrieval fails.
   */
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
