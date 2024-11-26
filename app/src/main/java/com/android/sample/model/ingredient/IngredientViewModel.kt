package com.android.sample.model.ingredient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.resources.C.Tag.INGREDIENT_NOT_FOUND_MESSAGE
import com.android.sample.resources.C.Tag.INGREDIENT_VIEWMODEL_LOG_TAG
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.OkHttpClient

/**
 * Ingredient View Model
 *
 * @param repository
 * @constructor Create empty Ingredient view model
 */
class IngredientViewModel(private val repository: IngredientRepository) :
    ViewModel(), SearchIngredientViewModel {

  private val _ingredient = MutableStateFlow<Pair<Ingredient?, String?>>(Pair(null, null))
  override val ingredient: StateFlow<Pair<Ingredient?, String?>>
    get() = _ingredient

  private val _ingredientList = MutableStateFlow<List<Pair<Ingredient, String?>>>(emptyList())
  override val ingredientList: StateFlow<List<Pair<Ingredient, String?>>>
    get() = _ingredientList

  private val _searchingIngredientList =
      MutableStateFlow<List<Pair<Ingredient, String?>>>(emptyList())
  override val searchingIngredientList: StateFlow<List<Pair<Ingredient, String?>>>
    get() = _searchingIngredientList

  /**
   * Fetch ingredient
   *
   * @param barCode
   */
  override fun fetchIngredient(barCode: Long) {
    if (_ingredient.value.first?.barCode == barCode) {
      return
    }
    repository.get(
        barCode,
        onSuccess = { ingredient ->
          if (ingredient != null) {
            _ingredient.value = Pair(ingredient, ingredient.quantity)
          }
        },
        onFailure = {
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGREDIENT_NOT_FOUND_MESSAGE)
          _ingredient.value = Pair(null, null)
        })
  }
  /** Clear ingredient after use */
  override fun clearIngredient() {
    _ingredient.value = Pair(null, null)
  }

  /**
   * Add the first integer in the two strings
   *
   * @param quantity1
   * @param quantity2
   */
  private fun addFirstInt(quantity1: String?, quantity2: String?): String {
    if (quantity1 == null || quantity2 == null) {
      return quantity1 ?: quantity2 ?: ""
    }

    val regex = Regex("""\d+""")
    val match1 = regex.find(quantity1)
    val match2 = regex.find(quantity2)

    return if (match1 != null && match2 != null) {
      val addition = match1.value.toInt() + match2.value.toInt()
      quantity1.replaceFirst(match1.value, addition.toString())
    } else {
      quantity1
    }
  }

  /**
   * Add bar code ingredient
   *
   * @param ingredient
   */
  override fun addIngredient(ingredient: Ingredient) {
    _ingredientList.update { currentList ->
      val existingItemIndex = currentList.indexOfFirst { it.first == ingredient }

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
   * Update quantity
   *
   * @param ingredient
   * @param quantity
   */
  fun updateQuantity(ingredient: Ingredient, quantity: String) {
    _ingredientList.value =
        _ingredientList.value.map {
          if (it.first == ingredient) {
            Pair(it.first, quantity)
          } else {
            it
          }
        }
  }

  /**
   * Fetch ingredient by name
   *
   * @param name
   */
  override fun fetchIngredientByName(name: String) {
    repository.search(
        name,
        onSuccess = { ingredientList ->
          _searchingIngredientList.value = ingredientList.map { Pair(it, it.quantity) }
        },
        onFailure = { _searchingIngredientList.value = emptyList() })
  }

  /**
   * Remove ingredient
   *
   * @param ingredient
   */
  fun removeIngredient(ingredient: Ingredient) {
    _ingredientList.value = _ingredientList.value.filter { it.first != ingredient }
  }

  /** Clear search */
  fun clearSearch() {
    _searchingIngredientList.value = emptyList()
  }

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IngredientViewModel(
                AggregatorIngredientRepository(
                    FirestoreIngredientRepository(Firebase.firestore),
                    OpenFoodFactsIngredientRepository(OkHttpClient()),
                    ImageRepositoryFirebase(Firebase.storage),
                    ImageUploader()))
                as T
          }
        }
  }
}
