package com.android.sample.model.categoryIngredient

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.SearchIngredientViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CategoryIngredientSearchViewModel(private val repository: CategoryIngredientRepository) :
    ViewModel(), SearchIngredientViewModel {

  private val _ingredient = MutableStateFlow<Pair<Ingredient?, String?>>(Pair(null, null))
  override val ingredient: StateFlow<Pair<Ingredient?, String?>> = _ingredient

  private val _isFetchingByBarcode = MutableStateFlow(false)
  override val isFetchingByBarcode: StateFlow<Boolean> = _isFetchingByBarcode

  private val _ingredientList = MutableStateFlow<List<Pair<Ingredient, String?>>>(emptyList())
  override val ingredientList: StateFlow<List<Pair<Ingredient, String?>>> = _ingredientList

  private val _searchingIngredientList =
      MutableStateFlow<List<Pair<Ingredient, String?>>>(emptyList())
  override val searchingIngredientList: StateFlow<List<Pair<Ingredient, String?>>> =
      _searchingIngredientList

  private val _isSearching = MutableStateFlow(false)
  override val isFetchingByName: StateFlow<Boolean> = _isSearching

  /**
   * Do not call this method from a CategoryIngredientSearchViewModel It's only made to search by
   * name
   *
   * @param barCode is unused in this case
   * @throws NotImplementedError
   */
  override fun fetchIngredient(barCode: Long) {
    throw NotImplementedError()
  }

  /**
   * Because there is no searching by barcode in this ViewModel, this method is not implemented
   *
   * @throws NotImplementedError
   */
  override fun clearIngredient() {
    throw NotImplementedError()
  }

  override fun clearSearchingIngredientList() {
    _searchingIngredientList.update { emptyList() }
  }

  override fun clearIngredientList() {
    _ingredientList.update { emptyList() }
  }

  override fun addIngredient(ingredient: Ingredient) {
    addIngredientToList(ingredient, _ingredientList)
  }

  override fun fetchIngredientByName(name: String) {
    _isSearching.value = true
    repository.searchCategory(
        name,
        { categories ->
          _searchingIngredientList.value =
              categories.map { Pair(Ingredient(name = it, categories = emptyList()), null) }
          _isSearching.value = false
        },
        { exception ->
          Log.e("CategoryIngredientSearchViewModel", "Error searching for category: $exception")
          _searchingIngredientList.value = emptyList()
          _isSearching.value = false
        },
        20)
  }

  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return CategoryIngredientSearchViewModel(
              LocalCategoryIngredientRepository(context.applicationContext))
              as T
        }
      }
    }
  }
}
