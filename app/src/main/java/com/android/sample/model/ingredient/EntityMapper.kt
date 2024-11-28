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
  val gson = Gson()
  return IngredientEntity(
      uid = this.uid ?: "",
      barCode = this.barCode,
      name = this.name,
      brands = this.brands,
      quantity = this.quantity,
      categories = gson.toJson(this.categories),
      images = gson.toJson(this.images))
}

/**
 * Extension function to convert an IngredientEntity object to an Ingredient object.
 *
 * @return The Ingredient object created from the IngredientEntity object.
 */
fun IngredientEntity.toIngredient(): Ingredient {
  val gson = Gson()
  return Ingredient(
      uid = this.uid,
      barCode = this.barCode,
      name = this.name,
      brands = this.brands,
      quantity = this.quantity,
      categories = gson.fromJson(this.categories, object : TypeToken<List<String>>() {}.type),
      images = gson.fromJson(this.images, object : TypeToken<MutableMap<String, String>>() {}.type))
}
