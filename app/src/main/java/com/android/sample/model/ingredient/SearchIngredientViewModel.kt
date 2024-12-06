package com.android.sample.model.ingredient

import android.util.Log
import com.android.sample.resources.C.Tag.INGREDIENT_NOT_FOUND_MESSAGE
import com.android.sample.resources.C.Tag.INGREDIENT_VIEWMODEL_LOG_TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface SearchIngredientViewModel {
  // The ingredient to search with the barcode
  val ingredient: StateFlow<Pair<Ingredient?, String?>>

  // The state of searching by barcode
  val isFetchingByBarcode: StateFlow<Boolean>

  // The list of ingredients to display after fetching in the database
  val searchingIngredientList: StateFlow<List<Pair<Ingredient, String?>>>

  // The list of ingredient whe select
  val ingredientList: StateFlow<List<Pair<Ingredient, String?>>>

  // The state of searching by name
  val isFetchingByName: StateFlow<Boolean>

  /**
   * Fetch ingredient
   *
   * @param barCode
   */
  fun fetchIngredient(barCode: Long)

  /** Clear ingredient after use when search the barcode */
  fun clearIngredient()

  /** Clear searching ingredient list */
  fun clearSearchingIngredientList()

  /** Clear ingredient list */
  fun clearIngredientList()

  /**
   * Add ingredient to the ingredient list
   *
   * @param ingredient: the ingredient to add
   */
  fun addIngredient(ingredient: Ingredient)

  /**
   * Fetch ingredient by name
   *
   * @param name: the name of the ingredient to search for
   */
  fun fetchIngredientByName(name: String)

  /**
   * Add the first integer in the two strings
   *
   * @param quantity1: the first string quantity to add
   * @param quantity2: the second string quantity to add
   */
  private fun addFirstInt(quantity1: String?, quantity2: String?): String {
    if (quantity1 == null || quantity2 == null) {
      return quantity1 ?: quantity2 ?: ""
    }

    // Regular expression to find the first integer in the string
    val regex = Regex("""\d+""")
    val match1 = regex.find(quantity1)
    val match2 = regex.find(quantity2)

    // If both strings contain an integer, add them together
    return if (match1 != null && match2 != null) {
      val addition = match1.value.toInt() + match2.value.toInt()
      quantity1.replaceFirst(match1.value, addition.toString())
    } else if (match1 != null) {
      quantity1
    } else {
      quantity2
    }
  }

  /**
   * Add ingredient to the list
   *
   * @param ingredient: new ingredient to add
   * @param ingredientList: the list of ingredients to add to
   */
  fun addIngredientToList(
      ingredient: Ingredient,
      ingredientList: MutableStateFlow<List<Pair<Ingredient, String?>>>
  ) {
    ingredientList.update { currentList ->
      val existingItemIndex = currentList.indexOfFirst { it.first.barCode == ingredient.barCode }

      if (existingItemIndex != -1) {
        // Ingredient already exists; update its associated String value
        currentList.toMutableList().apply {
          this[existingItemIndex] =
              this[existingItemIndex].copy(
                  second = addFirstInt(this[existingItemIndex].second, ingredient.quantity))
        }
      } else {
        // Ingredient doesn't exist; add it to the list
        currentList + (ingredient to ingredient.quantity)
      }
    }
  }

  /**
   * Fetch ingredient by name and add to the list
   *
   * @param name: the name of the ingredient to search for
   * @param searchingIngredientList: the list of ingredients to add to
   * @param ingredientRepository: the repository to fetch the ingredient from
   * @param onStart: the function to call when the search starts
   * @param onFinished: the function to call when the search finishes
   */
  fun fetchIngredientByNameAndAddToList(
      name: String,
      searchingIngredientList: MutableStateFlow<List<Pair<Ingredient, String?>>>,
      ingredientRepository: IngredientRepository,
      onStart: () -> Unit,
      onFinished: () -> Unit,
  ) {
    onStart()
    ingredientRepository.search(
        name,
        onSuccess = { ingredientList ->
          onFinished()
          searchingIngredientList.value = ingredientList.map { Pair(it, it.quantity) }
        },
        onFailure = {
          onFinished()
          searchingIngredientList.value = emptyList()
        })
  }

  /**
   * Fetch ingredient by barcode and add to the list
   *
   * @param barCode: the barcode of the ingredient to search for
   * @param fetchedIngredient: the ingredient to add to
   * @param ingredientRepository: the repository to fetch the ingredient from
   * @param onStart: the function to call when the search starts
   * @param onFinished: the function to call when the search finishes
   */
  fun fetchIngredientByBarcodeAndAddToList(
      barCode: Long,
      fetchedIngredient: MutableStateFlow<Pair<Ingredient?, String?>>,
      ingredientRepository: IngredientRepository,
      onStart: () -> Unit,
      onFinished: () -> Unit,
  ) {
    // Fetch ingredient from repository
    onStart()
    ingredientRepository.get(
        barCode,
        onSuccess = { ingredient ->
          if (ingredient != null) {
            fetchedIngredient.value = Pair(ingredient, ingredient.quantity)
          }

          onFinished()
        },
        onFailure = {
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGREDIENT_NOT_FOUND_MESSAGE)
          fetchedIngredient.value = Pair(null, null)
          onFinished()
        })
  }
}
