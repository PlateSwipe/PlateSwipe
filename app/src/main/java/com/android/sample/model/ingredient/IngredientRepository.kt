package com.android.sample.model.ingredient

interface IngredientRepository {
  fun get(barCode: Long, onSuccess: (Ingredient) -> Unit, onFailure: (Exception) -> Unit)

  fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int = 20
  )
}
