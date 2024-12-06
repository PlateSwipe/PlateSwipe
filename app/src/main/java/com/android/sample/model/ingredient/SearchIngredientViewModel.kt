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
   * Perform an addition operation of the first integers of two string quantities
   *
   * @param quantity1: the first string quantity to add
   * @param quantity2: the second string quantity to add
   */
  private fun addFirstInt(quantity1: String?, quantity2: String?): String {
    if (quantity1 == null || quantity2 == null) {
      return quantity1 ?: quantity2 ?: ""
    }
    // Regular expression to find the first number in the string, considering possible decimal
    // separators
    val regex = Regex("""\d+[,.]?\d*""")

    val match1 = regex.find(quantity1)
    val match2 = regex.find(quantity2)

    return if (match1 != null && match2 != null) {
      // Replace ',' with '.' for both matches to standardize
      val number1 = match1.value.replace(',', '.').toDouble()
      val number2 = match2.value.replace(',', '.').toDouble()
      val addition = number1 + number2

      // Format the result, replace the first occurrence, delete .0 and ,0 and return
      quantity1
          .replaceFirst(match1.value, addition.toString())
          .replaceFirst(".0", "")
          .replaceFirst(",0", "")
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
        currentList + (ingredient to (ingredient.quantity?.replaceFirst(",", ".") ?: ""))
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
