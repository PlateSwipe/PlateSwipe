package com.android.sample.model.ingredient

import kotlinx.coroutines.flow.StateFlow

interface SearchIngredientViewModel {
  val ingredient: StateFlow<Ingredient?>

  val searchingIngredientList: StateFlow<List<Ingredient>>

  val ingredientList: StateFlow<List<Ingredient>>

  fun fetchIngredient(barCode: Long)

  fun addIngredient(ingredient: Ingredient)

  fun fetchIngredientByName(name: String)
}
