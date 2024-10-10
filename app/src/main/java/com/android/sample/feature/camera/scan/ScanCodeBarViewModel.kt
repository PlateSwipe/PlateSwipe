package com.android.sample.feature.camera.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientRepository
import kotlinx.coroutines.launch

class ScanCodeBarViewModel(private val repository: IngredientRepository) : ViewModel() {

  private val _ingredient = MutableLiveData<Ingredient?>()
  val ingredient: LiveData<Ingredient?>
    get() = _ingredient

  private val _error = MutableLiveData<Exception?>()
  val error: LiveData<Exception?>
    get() = _error

  fun fetchIngredient(barCode: Long) {
    viewModelScope.launch {
      repository.get(
          barCode,
          onSuccess = { ingredient -> _ingredient.postValue(ingredient) },
          onFailure = { exception -> _error.postValue(exception) })
    }
  }
}
