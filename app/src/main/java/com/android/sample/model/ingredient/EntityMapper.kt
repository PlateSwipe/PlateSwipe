package com.android.sample.model.ingredient

import com.android.sample.model.ingredient.localData.IngredientEntity
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

/**
 * Extension function to convert an Ingredient object to an IngredientEntity object.
 *
 * @return The IngredientEntity object created from the Ingredient object.
 */
fun Ingredient.toEntity(): IngredientEntity {
  return IngredientEntity(
      uid = this.uid ?: "",
      barCode = this.barCode,
      name = this.name,
      brands = this.brands,
      quantity = this.quantity,
      categories = Gson().toJson(this.categories),
      images = Gson().toJson(this.images))
}

/**
 * Extension function to convert an IngredientEntity object to an Ingredient object.
 *
 * @return The Ingredient object created from the IngredientEntity object.
 */
fun IngredientEntity.toIngredient(): Ingredient {
  return Ingredient(
      uid = this.uid,
      barCode = this.barCode,
      name = this.name,
      brands = this.brands,
      quantity = this.quantity,
      categories = Gson().fromJson(this.categories, object : TypeToken<List<String>>() {}.type),
      images =
          Gson().fromJson(this.images, object : TypeToken<MutableMap<String, String>>() {}.type))
}
