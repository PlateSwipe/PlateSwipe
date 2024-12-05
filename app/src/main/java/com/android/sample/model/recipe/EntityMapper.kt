package com.android.sample.model.recipe

import com.android.sample.model.recipe.localData.RecipeEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Extension function to convert a RecipeEntity object to a Recipe object.
 *
 * @return The Recipe object created from the RecipeEntity object.
 */
fun RecipeEntity.toRecipe(): Recipe {
  val gson = Gson()
  return Recipe(
      uid = this.uid,
      name = this.name,
      category = this.category,
      origin = this.origin,
      instructions =
          gson.fromJson(this.instructions, object : TypeToken<List<Instruction>>() {}.type),
      strMealThumbUrl = this.strMealThumbUrl,
      ingredientsAndMeasurements =
          gson.fromJson(
              this.ingredientsAndMeasurements,
              object : TypeToken<List<Pair<String, String>>>() {}.type),
      time = this.time,
      difficulty = this.difficulty,
      price = this.price,
      url = this.url)
}

/**
 * Extension function to convert a Recipe object to a RecipeEntity object.
 *
 * @return The RecipeEntity object created from the Recipe object.
 */
fun Recipe.toEntity(): RecipeEntity {
  val gson = Gson()
  return RecipeEntity(
      uid = this.uid,
      name = this.name,
      category = this.category,
      origin = this.origin,
      instructions = gson.toJson(this.instructions),
      strMealThumbUrl = this.strMealThumbUrl,
      ingredientsAndMeasurements = gson.toJson(this.ingredientsAndMeasurements),
      time = this.time,
      difficulty = this.difficulty,
      price = this.price,
      url = this.url)
}
