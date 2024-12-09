package com.android.sample.model.ingredient

interface IngredientRepository {

  /**
   * Get an ingredient by its barcode.
   *
   * @param barCode The barcode of the ingredient. Can be any format of barcode (ex:EAN,UPC).
   * @param onSuccess The callback to be called when the ingredient is found. It's null if it isn't
   *   found.
   * @param onFailure The callback to be called when an error occurs during the get operation. when
   *   retrieving it.
   */
  fun get(barCode: Long, onSuccess: (Ingredient?) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Search for ingredients by name.
   *
   * @param name The name of the ingredient to search for.
   * @param onSuccess The callback to be called with the resulting ingredients.
   * @param onFailure The callback to be called with an exception if the search fails.
   * @param count The maximum number of ingredients to return.
   */
  fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int = 20
  )

  /**
   * Add an ingredient to the DB.
   *
   * @param ingredient The ingredient to add.
   */
  fun addDownload(ingredient: Ingredient)

  /**
   * Update an ingredient in the DB.
   *
   * @param ingredient The ingredient to update.
   */
  fun updateDownload(ingredient: Ingredient)

  /**
   * Delete an ingredient from the DB.
   *
   * @param ingredient The ingredient to delete.
   */
  fun deleteDownload(ingredient: Ingredient)

  /**
   * Get all ingredients from the DB.
   *
   * @param onSuccess The callback to be called with the resulting ingredients.
   * @param onFailure The callback to be called with an exception if the search fails.
   */
  fun getAllDownload(onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Get an ingredient by barcode.
   *
   * @param barCode barcode of the ingredient
   * @param onSuccess callback with the ingredient
   * @param onFailure callback with an exception
   */
  fun getByBarcode(barCode: Long, onSuccess: (Ingredient?) -> Unit, onFailure: (Exception) -> Unit)
}
