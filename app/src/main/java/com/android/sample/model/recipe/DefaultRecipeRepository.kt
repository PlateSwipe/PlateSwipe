package com.android.sample.model.recipe

import com.android.sample.model.recipe.localData.RecipeLocalRepository
import com.android.sample.model.recipe.networkData.RecipeNetworkRepository

/**
 * Default implementation of the RecipesRepository interface. This class is responsible for handling
 * the remote and local repositories.
 *
 * @property localRepository The local repository for managing downloaded recipes.
 * @property networkRepository The network repository for managing recipes from the network.
 */
class DefaultRecipeRepository(
    private val localRepository: RecipeLocalRepository,
    private val networkRepository: RecipeNetworkRepository
) : RecipesRepository {

  /**
   * Retrieves a new unique identifier for a recipe.
   *
   * @return A new unique identifier.
   */
  override fun getNewUid(): String {
    return networkRepository.getNewUid()
  }

  /**
   * Adds a recipe to the network repository.
   *
   * @param recipe The recipe to add.
   * @param onSuccess Callback to be invoked when the addition is successful.
   * @param onFailure Callback to be invoked when the addition fails.
   */
  override fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.addRecipe(recipe, onSuccess, onFailure)
  }

  /**
   * Updates an existing recipe in the network repository.
   *
   * @param recipe The recipe to update.
   * @param onSuccess Callback to be invoked when the update is successful.
   * @param onFailure Callback to be invoked when the update fails.
   */
  override fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.updateRecipe(recipe, onSuccess, onFailure)
  }

  /**
   * Deletes a recipe from the network repository.
   *
   * @param idMeal The identifier of the recipe to delete.
   * @param onSuccess Callback to be invoked when the deletion is successful.
   * @param onFailure Callback to be invoked when the deletion fails.
   */
  override fun deleteRecipe(idMeal: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.deleteRecipe(idMeal, onSuccess, onFailure)
  }

  /**
   * Retrieves a random list of recipes from the network repository.
   *
   * @param nbOfElements The number of random recipes to retrieve.
   * @param onSuccess Callback to be invoked when the retrieval is successful.
   * @param onFailure Callback to be invoked when the retrieval fails.
   */
  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    networkRepository.random(nbOfElements, onSuccess, onFailure)
  }

  /**
   * Searches for a recipe by its identifier in the network repository.
   *
   * @param mealID The identifier of the recipe to search for.
   * @param onSuccess Callback to be invoked when the search is successful.
   * @param onFailure Callback to be invoked when the search fails.
   */
  override fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.search(mealID, onSuccess, onFailure)
  }

  /**
   * Searches for recipes by category in the network repository.
   *
   * @param category The category to search for.
   * @param onSuccess Callback to be invoked when the search is successful.
   * @param onFailure Callback to be invoked when the search fails.
   * @param limit The maximum number of recipes to retrieve.
   */
  override fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit,
      limit: Int
  ) {
    networkRepository.searchByCategory(category, onSuccess, onFailure, limit)
  }

  /**
   * Lists all recipe categories from the network repository.
   *
   * @param onSuccess Callback to be invoked when the listing is successful.
   * @param onFailure Callback to be invoked when the listing fails.
   */
  override fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.listCategories(onSuccess, onFailure)
  }

  /**
   * Adds a recipe to the local repository.
   *
   * @param recipe The recipe to add.
   */
  override fun addDownload(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    localRepository.add(recipe, onSuccess, onFailure)
  }

  /**
   * Updates a recipe in the local repository.
   *
   * @param recipe The recipe to update.
   */
  override fun updateDownload(recipe: Recipe) {
    localRepository.update(recipe)
  }

  /**
   * Deletes a recipe from the local repository.
   *
   * @param recipe The recipe to delete.
   */
  override fun deleteDownload(recipe: Recipe) {
    localRepository.delete(recipe)
  }

  /**
   * Retrieves all downloaded recipes from the local repository.
   *
   * @param onSuccess Callback to be invoked when the retrieval is successful.
   * @param onFailure Callback to be invoked when the retrieval fails.
   */
  override fun getAllDownload(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
    localRepository.getAll(onSuccess, onFailure)
  }

  /**
   * Deletes all downloaded recipes from the local repository.
   *
   * @param onSuccess Callback to be invoked when the deletion is successful.
   * @param onFailure Callback to be invoked when the deletion fails.
   */
  override fun deleteAllDownloads(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    localRepository.deleteAll(onSuccess, onFailure)
  }
}
