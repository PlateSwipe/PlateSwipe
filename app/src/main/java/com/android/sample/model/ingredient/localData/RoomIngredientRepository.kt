package com.android.sample.model.ingredient.localData

import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.toEntity
import com.android.sample.model.ingredient.toIngredient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RoomIngredientRepository(
    private val ingredientDAO: IngredientDAO,
    private val dispatcher: CoroutineDispatcher
) : IngredientLocalRepository {

  /**
   * Adds a new ingredient to the database.
   *
   * @param ingredient The ingredient to add.
   */
  override fun add(ingredient: Ingredient) {
    CoroutineScope(dispatcher).launch { ingredientDAO.insert(ingredient.toEntity()) }
  }

  /**
   * Updates an existing ingredient in the database.
   *
   * @param ingredient The ingredient to update.
   */
  override fun update(ingredient: Ingredient) {
    CoroutineScope(dispatcher).launch { ingredientDAO.update(ingredient.toEntity()) }
  }

  /**
   * Deletes an ingredient from the database.
   *
   * @param ingredient The ingredient to delete.
   */
  override fun delete(ingredient: Ingredient) {
    CoroutineScope(dispatcher).launch { ingredientDAO.delete(ingredient.toEntity()) }
  }

  /**
   * Retrieves all ingredients from the database.
   *
   * @param onSuccess Callback function to be invoked with the list of retrieved ingredients.
   * @param onFailure Callback function to be invoked if an error occurs.
   */
  override fun getAll(onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit) {
    CoroutineScope(dispatcher).launch {
      val ingredients = ingredientDAO.getAll().map { it.toIngredient() }
      onSuccess(ingredients)
    }
  }

  /**
   * Retrieves an ingredient by its barcode.
   *
   * @param barCode The barcode of the ingredient to retrieve.
   * @param onSuccess Callback function to be invoked with the retrieved ingredient, or null if not
   *   found.
   * @param onFailure Callback function to be invoked if an error occurs.
   */
  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(dispatcher).launch {
      val ingredient = ingredientDAO.get(barCode).toIngredient()
      onSuccess(ingredient)
    }
  }
}
