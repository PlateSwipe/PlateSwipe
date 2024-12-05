package com.android.sample.model.recipe

import com.android.sample.model.recipe.networkData.RecipeNetworkRepository

/** Interface for retrieving recipe data. */
interface RecipesRepository : RecipeNetworkRepository {
  /**
   * Adds a recipe to the list of downloads.
   *
   * @param recipe The recipe to add to downloads.
   */
  fun addDownload(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates a recipe in the list of downloads.
   *
   * @param recipe The recipe to update in downloads.
   */
  fun updateDownload(recipe: Recipe)

  /**
   * Deletes a recipe from the list of downloads.
   *
   * @param recipe The recipe to delete from downloads.
   */
  fun deleteDownload(recipe: Recipe)

  /**
   * Retrieves all downloaded recipes.
   *
   * @param onSuccess Callback that returns the list of downloaded recipes.
   * @param onFailure Callback that is called when an error occurs.
   */
  fun getAllDownload(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes all downloaded recipes.
   *
   * @param onSuccess Callback that is called when all downloads are deleted.
   * @param onFailure Callback that is called when an error occurs.
   */
  fun deleteAllDownloads(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
