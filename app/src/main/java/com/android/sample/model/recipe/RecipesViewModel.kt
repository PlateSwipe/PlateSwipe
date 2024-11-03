package com.android.sample.model.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.Filter
import com.android.sample.model.filter.FilterPageViewModel
import com.android.sample.resources.C.Tag.MINIMUM_RECIPES_BEFORE_FETCH
import com.android.sample.resources.C.Tag.NUMBER_RECIPES_TO_FETCH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

/**
 * ViewModel for managing recipes.
 *
 * @property repository The repository used to fetch recipe data.
 */
class RecipesViewModel(private val repository: RecipesRepository) :
    ViewModel(), FilterPageViewModel, RecipeOverviewViewModel {

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
  override val currentRecipe: StateFlow<Recipe?>
    get() = _currentRecipe

  private val _nextRecipe = MutableStateFlow<Recipe?>(null)
  val nextRecipe: StateFlow<Recipe?>
    get() = _nextRecipe

  private val _filter = MutableStateFlow(Filter())
  override val filter: StateFlow<Filter>
    get() = _filter

  private val _categories = MutableStateFlow<List<String>>(emptyList())
  override val categories: StateFlow<List<String>>
    get() = _categories

  init {
    viewModelScope.launch {
      getCategoryList()
      fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH)

      _loading.collect { isLoading ->
        if (!isLoading) {
          updateCurrentRecipe(_recipes.value.first())
          return@collect
        }
      }
    }
  }

  /** Fetches the list of categories from the repository. */
  override fun getCategoryList() {
    repository.listCategories(
        onSuccess = { categories -> _categories.value = categories },
        onFailure = { exception ->
          Log.e("RecipesViewModel", "Error fetching categories", exception)
        })
  }

  /**
   * Updates the difficulty filter.
   *
   * @param difficulty The difficulty to filter by.
   */
  override fun updateDifficulty(difficulty: Difficulty) {
    filter.value.difficulty = difficulty
  }

  /**
   * Updates the price range filter.
   *
   * @param min The minimum price.
   * @param max The maximum price.
   */
  override fun updatePriceRange(min: Float, max: Float) {
    filter.value.priceRange.update(min, max)
  }

  /**
   * Updates the time range filter.
   *
   * @param min The minimum time.
   * @param max The maximum time.
   */
  override fun updateTimeRange(min: Float, max: Float) {
    filter.value.timeRange.update(min, max)
  }

  /**
   * Updates the category filter.
   *
   * @param category The category to filter by.
   */
  override fun updateCategory(category: String?) {
    viewModelScope.launch {
      if (category == null) {
        _recipes.value = emptyList()
      }
      _filter.value.category = category
      fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH)
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
    // uncomment when backend is ready. Tested with hardcoded mealDB data
    /*if (_filter.value.category != null) {
      repository.searchByCategory(
          _filter.value.category!!,
          onSuccess = { recipes ->
            _recipes.value = recipes
            _loading.value = false
          },
          onFailure = { exception ->
            _loading.value = false
            Log.e("RecipesViewModel", "Error fetching recipes", exception)
          })
    }*/
    repository.random(
        nbOfElements = numberOfRecipes,
        onSuccess = { randomRecipes ->
          _recipes.value += randomRecipes // Add to the list of recipes
          _loading.value = false // Set loading to false after fetching
        },
        onFailure = { exception ->
          _loading.value = false // Set loading to false on failure
          Log.e("RecipesViewModel", "Error fetching recipes", exception)
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
      if (currentRecipes.size <= MINIMUM_RECIPES_BEFORE_FETCH) {
        viewModelScope.launch { fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH) }
      }
    }
  }

  /**
   * Updates the next recipe in the list of recipes.
   *
   * This method assumes that _currentRecipe is already set and valid. It should be called after
   * nextRecipe() to ensure the next recipe is updated correctly.
   */
  private fun updateNextRecipe() {
    val currentRecipes = _recipes.value
    if (currentRecipes.isNotEmpty()) {
      val nextRecipeIndex = (currentRecipes.indexOf(_currentRecipe.value) + 1) % currentRecipes.size
      _nextRecipe.value = currentRecipes[nextRecipeIndex]
    } else {
      _nextRecipe.value = null
    }
  }

  /**
   * Searches for a recipe by its meal ID.
   *
   * @param mealID The ID of the meal to search for.
   * @param onSuccess Callback to be invoked when the search is successful.
   * @param onFailure Callback to be invoked when the search fails.
   */
  fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    repository.search(
        mealID = mealID,
        onSuccess = { recipe -> onSuccess(recipe) },
        onFailure = { exception -> onFailure(exception) })
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val okHttpClient = OkHttpClient()
            val repository = MealDBRecipesRepository(okHttpClient)
            return RecipesViewModel(repository) as T
          }
        }
  }
}
