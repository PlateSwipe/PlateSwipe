package com.android.sample.model.ingredient

import kotlinx.coroutines.flow.StateFlow

interface SearchIngredientViewModel {
  // The ingredient to search with the barcode
  val ingredient: StateFlow<Pair<Ingredient?, String?>>

  // The list of ingredients to display after fetching in the database
  val searchingIngredientList: StateFlow<List<Pair<Ingredient, String?>>>

  // The list of ingredient whe select
  val ingredientList: StateFlow<List<Pair<Ingredient, String?>>>

  val isSearching: StateFlow<Boolean>

  /**
   * Fetch ingredient
   *
   * @param barCode
   */
  fun fetchIngredient(barCode: Long)

  /** Clear ingredient after use when search the barcode */
  fun clearIngredient()

  /**
   * Add ingredient to the ingredient list
   *
   * @param ingredient
   */
  fun addIngredient(ingredient: Ingredient)

  /**
   * Fetch ingredient by name
   *
   * @param name
   */
  fun fetchIngredientByName(name: String)
}
