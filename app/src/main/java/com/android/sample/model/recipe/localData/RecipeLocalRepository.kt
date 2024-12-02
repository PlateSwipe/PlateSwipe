package com.android.sample.model.recipe.localData

import com.android.sample.model.recipe.Recipe

/** Interface for local repository operations on recipes. */
interface RecipeLocalRepository {

  /**
   * Adds a recipe to the local repository.
   *
   * @param recipe The recipe to add.
   */
  fun add(recipe: Recipe)

  /**
   * Updates an existing recipe in the local repository.
   *
   * @param recipe The recipe to update.
   */
  fun update(recipe: Recipe)

  /**
   * Deletes a recipe from the local repository.
   *
   * @param recipe The recipe to delete.
   */
  fun delete(recipe: Recipe)

  /**
   * Deletes all recipes from the local repository.
   *
   * @param onSuccess Callback to be invoked when the deletion is successful.
   * @param onFailure Callback to be invoked when the deletion fails.
   */
  fun deleteAll(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves all recipes from the local repository.
   *
   * @param onSuccess Callback to be invoked when the retrieval is successful.
   * @param onFailure Callback to be invoked when the retrieval fails.
   */
  fun getAll(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit)
}
