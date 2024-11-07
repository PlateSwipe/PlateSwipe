package com.android.sample.model.ingredient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.firestore
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

  fun fetchIngredient(barCode: Long) {
    if (_ingredient.value?.barCode == barCode) {
      return
    }
    repository.get(
        barCode,
        onSuccess = { ingredient -> _ingredient.value = ingredient },
        onFailure = { _ingredient.value = null })
  }

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IngredientViewModel(
                AggregatorIngredientRepository(
                    FirestoreIngredientRepository(com.google.firebase.Firebase.firestore),
                    OpenFoodFactsIngredientRepository(OkHttpClient())))
                as T
          }
        }
  }
}
