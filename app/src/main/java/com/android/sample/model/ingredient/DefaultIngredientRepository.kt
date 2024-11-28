package com.android.sample.model.ingredient

import com.android.sample.model.ingredient.localData.IngredientLocalRepository
import com.android.sample.model.ingredient.networkData.IngredientNetworkRepository

/**
 * Default implementation of the IngredientRepository interface.
 *
 * @property localRepository The local repository for ingredient data.
 * @property networkRepository The network repository for ingredient data.
 */
class DefaultIngredientRepository(
    private val localRepository: IngredientLocalRepository,
    private val networkRepository: IngredientNetworkRepository
) : IngredientRepository {

  /**
   * Retrieves an ingredient by its barcode.
   *
   * @param barCode The barcode of the ingredient to retrieve.
   * @param onSuccess Callback function to be invoked with the retrieved ingredient, or null if not
   *   found.
   * @param onFailure Callback function to be invoked if an error occurs.
   */
  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    networkRepository.get(barCode, onSuccess, onFailure)
  }

  /**
   * Searches for ingredients by name.
   *
   * @param name The name of the ingredients to search for.
   * @param onSuccess Callback function to be invoked with the list of retrieved ingredients.
   * @param onFailure Callback function to be invoked if an error occurs.
   * @param count The maximum number of ingredients to retrieve.
   */
  override fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    networkRepository.search(name, onSuccess, onFailure, count)
  }

  /**
   * Adds a new ingredient to the local repository.
   *
   * @param ingredient The ingredient to add.
   */
  override fun addDownload(ingredient: Ingredient) {
    localRepository.add(ingredient)
  }

  /**
   * Updates an existing ingredient in the local repository.
   *
   * @param ingredient The ingredient to update.
   */
  override fun updateDownload(ingredient: Ingredient) {
    localRepository.update(ingredient)
  }

  /**
   * Deletes an ingredient from the local repository.
   *
   * @param ingredient The ingredient to delete.
   */
  override fun deleteDownload(ingredient: Ingredient) {
    localRepository.delete(ingredient)
  }

  /**
   * Retrieves all ingredients from the local repository.
   *
   * @param onSuccess Callback function to be invoked with the list of retrieved ingredients.
   * @param onFailure Callback function to be invoked if an error occurs.
   */
  override fun getAllDownload(
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    localRepository.getAll(onSuccess, onFailure)
  }
}
