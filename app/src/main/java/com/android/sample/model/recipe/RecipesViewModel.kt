package com.android.sample.model.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

/**
 * ViewModel for managing recipes.
 *
 * @property repository The repository used to fetch recipe data.
 */
class RecipesViewModel(private val repository: RecipeRepository) : ViewModel() {

  // StateFlow to monitor the list of recipes
  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes: StateFlow<List<Recipe>>
    get() = _recipes

  // StateFlow for loading/error states
  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean>
    get() = _loading

  // StateFlow for the current selected recipe
  private val _currentRecipe = MutableStateFlow<Recipe?>(null)
  val currentRecipe: StateFlow<Recipe?>
    get() = _currentRecipe

  private val _nextRecipe = MutableStateFlow<Recipe?>(null)
  val nextRecipe: StateFlow<Recipe?>
    get() = _nextRecipe

  init {
    viewModelScope.launch {
      fetchRandomRecipes(3)

      _loading.collect { isLoading ->
        if (!isLoading) {
          updateCurrentRecipe(_recipes.value.first())
          return@collect
        }
      }
    }
  }

  /**
   * Fetches a specified number of random recipes from the repository.
   *
   * @param numberOfRecipes The number of random recipes to fetch.
   */
  fun fetchRandomRecipes(numberOfRecipes: Int) {
    require(numberOfRecipes >= 1) { "Number of fetched recipes must be at least 1" }
    _loading.value = true // Set loading to true while fetching
    repository.random(
        nbOfElements = numberOfRecipes,
        onSuccess = { randomRecipes ->
          _recipes.value += randomRecipes // Add to the list of recipes
          _loading.value = false // Set loading to false after fetching
        },
        onFailure = { exception ->
          _loading.value = false // Set loading to false on failure
          // Handle error (e.g., log it or show a message)
        })
  }

  /**
   * Updates the current selected recipe.
   *
   * @param recipe The recipe to set as the current recipe.
   */
  fun updateCurrentRecipe(recipe: Recipe) {
    _currentRecipe.value = recipe
    updateNextRecipe()
  }

  /** Clears the current selected recipe. */
  fun clearCurrentRecipe() {
    _currentRecipe.value = null
  }

  /** Gives the next recipe in the list of recipes. */
  fun nextRecipe() {
    val currentRecipes = _recipes.value.toMutableList()
    if (currentRecipes.isNotEmpty() && _currentRecipe.value != null) {
      // Get the index of the current recipe
      val currentIndex = currentRecipes.indexOf(_currentRecipe.value)

      // Calculate the next recipe index
      val nextRecipeIndex = (currentIndex + 1) % currentRecipes.size
      val nextRecipe = currentRecipes[nextRecipeIndex]

      // Remove the current recipe from the list
      currentRecipes.removeAt(currentIndex)
      _recipes.value = currentRecipes

      // Update the current recipe to the next one
      _currentRecipe.value = nextRecipe

      // Update the next recipe
      updateNextRecipe()

      // Check if there are only 3 recipes left and fetch 2 new recipes
      if (currentRecipes.size <= 3) {
        viewModelScope.launch { fetchRandomRecipes(2) }
      }
    }
  }

  /** Updates the next recipe in the list of recipes. */
  fun updateNextRecipe() {
    val currentRecipes = _recipes.value
    if (currentRecipes.isNotEmpty()) {
      val nextRecipeIndex = (currentRecipes.indexOf(_currentRecipe.value) + 1) % currentRecipes.size
      _nextRecipe.value = currentRecipes[nextRecipeIndex]
    } else {
      _nextRecipe.value = null
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val okHttpClient = OkHttpClient()
            val repository = MealDBRecipeRepository(okHttpClient)
            return RecipesViewModel(repository) as T
          }
        }
  }
}
