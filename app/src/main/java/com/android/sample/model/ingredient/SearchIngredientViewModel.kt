package com.android.sample.model.ingredient

import kotlinx.coroutines.flow.StateFlow

interface SearchIngredientViewModel {
  val ingredient: StateFlow<Pair<Ingredient?, String?>>

  val searchingIngredientList: StateFlow<List<Pair<Ingredient, String?>>>

  val ingredientList: StateFlow<List<Pair<Ingredient, String?>>>

  fun fetchIngredient(barCode: Long)

  fun addIngredient(ingredient: Ingredient)

  fun fetchIngredientByName(name: String)
}
