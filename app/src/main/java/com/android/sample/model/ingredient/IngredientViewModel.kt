package com.android.sample.model.ingredient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.image.ImageRepositoryFirebase
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

  private val _ingredientList = MutableStateFlow<List<Ingredient>>(emptyList())
  val ingredientList: StateFlow<List<Ingredient>>
    get() = _ingredientList

  fun fetchIngredient(barCode: Long) {
    if (_ingredient.value?.barCode == barCode) {
      return
    }
    repository.get(
        barCode,
        onSuccess = { ingredient -> _ingredient.value = ingredient },
        onFailure = { _ingredient.value = null })
  }

  fun addBarCodeIngredient(ingredient: Ingredient) {
    _ingredientList.value += ingredient
  }

  fun fetchIngredientByName(name: String) {
    repository.search(
        name,
        onSuccess = { ingredientList -> _ingredientList.value = ingredientList },
        onFailure = { _ingredientList.value = emptyList() })
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
                    OpenFoodFactsIngredientRepository(
                        OkHttpClient(), ImageRepositoryFirebase(Firebase.storage))))
                as T
          }
        }
  }
}
