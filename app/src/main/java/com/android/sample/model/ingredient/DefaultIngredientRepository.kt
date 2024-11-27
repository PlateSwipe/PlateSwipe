package com.android.sample.model.ingredient

import com.android.sample.model.ingredient.localData.IngredientLocalRepository
import com.android.sample.model.ingredient.networkData.IngredientNetworkRepository

open class DefaultIngredientRepository(
    private val localRepository: IngredientLocalRepository,
    private val networkRepository: IngredientNetworkRepository
) : IngredientRepository {

  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    networkRepository.get(barCode, onSuccess, onFailure)
  }

  override fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    networkRepository.search(name, onSuccess, onFailure, count)
  }

  override fun addDownload(ingredient: Ingredient) {
    localRepository.add(ingredient)
  }

  override fun updateDownload(ingredient: Ingredient) {
    localRepository.update(ingredient)
  }

  override fun deleteDownload(ingredient: Ingredient) {
    localRepository.delete(ingredient)
  }

  override fun getAllDownload(
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    localRepository.getAll(onSuccess, onFailure)
  }
}
