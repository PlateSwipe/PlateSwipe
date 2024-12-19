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

  /** Clear list of results from the search */
  override fun clearSearchingIngredientList() {
    _searchingIngredientList.update { emptyList() }
  }

  /** Clear the list of ingredients used by the recipe */
  override fun clearIngredientList() {
    _ingredientList.update { emptyList() }
  }

  /**
   * Add an ingredient to the list of ingredients used by the recipe
   *
   * @param ingredient The ingredient to add
   */
  override fun addIngredient(ingredient: Ingredient) {
    addIngredientToList(ingredient, _ingredientList)
  }

  /**
   * Update quantity for a specific
   *
   * @param ingredient: the ingredient to update
   * @param quantity: the quantity to update
   */
  override fun updateQuantity(ingredient: Ingredient, quantity: String) {
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
   * Remove ingredient
   *
   * @param ingredient
   */
  override fun removeIngredient(ingredient: Ingredient) {
    _ingredientList.value = _ingredientList.value.filter { it.first != ingredient }
  }

  /**
   * Search for an ingredient using it's name
   *
   * @param name The name of the ingredient to search for
   */
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
