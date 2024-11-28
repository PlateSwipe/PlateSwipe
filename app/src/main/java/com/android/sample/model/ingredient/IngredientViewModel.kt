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
import okhttp3.OkHttpClient

/**
 * Ingredient View Model
 *
 * @param repository
 * @constructor Create empty Ingredient view model
 */
class IngredientViewModel(private val repository: IngredientRepository) : ViewModel() {

  private val _ingredient = MutableStateFlow<Ingredient?>(null)
  val ingredient: StateFlow<Ingredient?>
    get() = _ingredient

  private val _isFetchingByBarcode = MutableStateFlow(false)
  val isFetchingByBarcode: StateFlow<Boolean>
    get() = _isFetchingByBarcode

  private val _ingredientList = MutableStateFlow<List<Ingredient>>(emptyList())
  val ingredientList: StateFlow<List<Ingredient>>
    get() = _ingredientList

  private val _searchingIngredientList = MutableStateFlow<List<Ingredient>>(emptyList())
  val searchingIngredientList: StateFlow<List<Ingredient>>
    get() = _searchingIngredientList

  private val _isFetchingByName = MutableStateFlow(false)
  val isFetchingByName: StateFlow<Boolean>
    get() = _isFetchingByName

  /**
   * Fetch ingredient
   *
   * @param barCode
   */
  fun fetchIngredient(barCode: Long) {
    if (_ingredient.value?.barCode == barCode) {
      return
    }

    _isFetchingByBarcode.value = true
    repository.get(
        barCode,
        onSuccess = { ingredient ->
          _ingredient.value = ingredient
          _isFetchingByBarcode.value = false
        },
        onFailure = {
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGREDIENT_NOT_FOUND_MESSAGE)

          _ingredient.value = null
          _isFetchingByBarcode.value = false
        })
  }

  /**
   * Add bar code ingredient
   *
   * @param ingredient
   */
  fun addIngredient(ingredient: Ingredient) {
    _ingredientList.value += ingredient
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
          if (it == ingredient) {
            it.copy(quantity = quantity)
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
  fun fetchIngredientByName(name: String) {
    _isFetchingByName.value = true
    repository.search(
        name,
        onSuccess = { ingredientList ->
          _isFetchingByName.value = false
          _searchingIngredientList.value = ingredientList
        },
        onFailure = {
          _isFetchingByName.value = false
          _searchingIngredientList.value = emptyList()
        })
  }

  /**
   * Remove ingredient
   *
   * @param ingredient
   */
  fun removeIngredient(ingredient: Ingredient) {
    _ingredientList.value = _ingredientList.value.filter { it != ingredient }
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
