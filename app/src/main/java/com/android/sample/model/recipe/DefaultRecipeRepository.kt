package com.android.sample.model.recipe

import com.android.sample.model.recipe.localData.RecipeLocalRepository
import com.android.sample.model.recipe.networkData.RecipeNetworkRepository

class DefaultRecipeRepository(
    private val localRepository: RecipeLocalRepository,
    private val networkRepository: RecipeNetworkRepository
) : RecipesRepository {

  override fun getNewUid(): String {
    return networkRepository.getNewUid()
  }

  override fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.addRecipe(recipe, onSuccess, onFailure)
  }

  override fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.updateRecipe(recipe, onSuccess, onFailure)
  }

  override fun deleteRecipe(idMeal: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.deleteRecipe(idMeal, onSuccess, onFailure)
  }

  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    networkRepository.random(nbOfElements, onSuccess, onFailure)
  }

  override fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.search(mealID, onSuccess, onFailure)
  }

  override fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit,
      limit: Int
  ) {
    networkRepository.searchByCategory(category, onSuccess, onFailure, limit)
  }

  override fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
    networkRepository.listCategories(onSuccess, onFailure)
  }

  override fun addDownload(recipe: Recipe) {
    localRepository.add(recipe)
  }

  override fun updateDownload(recipe: Recipe) {
    localRepository.update(recipe)
  }

  override fun deleteDownload(recipe: Recipe) {
    localRepository.delete(recipe)
  }

  override fun getAllDownload(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
    localRepository.getAll(onSuccess, onFailure)
  }

  override fun deleteAllDownloads(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    localRepository.deleteAll(onSuccess, onFailure)
  }
}
