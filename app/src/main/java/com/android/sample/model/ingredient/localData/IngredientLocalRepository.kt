package com.android.sample.model.ingredient.localData

import com.android.sample.model.ingredient.Ingredient

interface IngredientLocalRepository {
  /**
   * Add an ingredient to the DB.
   *
   * @param ingredient The ingredient to add.
   */
  fun add(ingredient: Ingredient)

  /**
   * Update an ingredient in the DB.
   *
   * @param ingredient The ingredient to update.
   */
  fun update(ingredient: Ingredient)

  /**
   * Delete an ingredient from the DB.
   *
   * @param ingredient The ingredient to delete.
   */
  fun delete(ingredient: Ingredient)

  /**
   * Get all ingredients from the DB.
   *
   * @param onSuccess The callback to be called with the resulting ingredients.
   * @param onFailure The callback to be called with an exception if the search fails.
   */
  fun getAll(onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Get an ingredient by barcode.
   *
   * @param barCode barcode of the ingredient
   * @param onSuccess callback with the ingredient
   * @param onFailure callback with an exception
   */
  fun getByBarcode(barCode: Long, onSuccess: (Ingredient?) -> Unit, onFailure: (Exception) -> Unit)
}
