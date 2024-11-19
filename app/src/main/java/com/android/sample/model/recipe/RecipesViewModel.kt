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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

  private val _tmpFilter = MutableStateFlow(Filter())
  override val tmpFilter: StateFlow<Filter>
    get() = _tmpFilter

  private val _categories = MutableStateFlow<List<String>>(emptyList())
  override val categories: StateFlow<List<String>>
    get() = _categories

  init {
    viewModelScope.launch {
      getCategoryList()
      fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH)

      _loading.collect { isLoading ->
        if (!isLoading) {
          if (_recipes.value.isNotEmpty()) updateCurrentRecipe(_recipes.value.first())
          return@collect
        }
      }
    }
  }

  /** Fetches the list of categories from the repository. */
  override fun getCategoryList() {
    _categories.value = Recipe.getCategories()
  }

  /**
   * Updates the difficulty filter.
   *
   * @param difficulty The difficulty to filter by.
   */
  override fun updateDifficulty(difficulty: Difficulty) {
    _tmpFilter.value.difficulty = difficulty
  }

  /**
   * Updates the price range filter.
   *
   * @param min The minimum price.
   * @param max The maximum price.
   */
  override fun updatePriceRange(min: Float, max: Float) {
    _tmpFilter.value.priceRange.update(min, max)
  }

  /**
   * Updates the time range filter.
   *
   * @param min The minimum time.
   * @param max The maximum time.
   */
  override fun updateTimeRange(min: Float, max: Float) {
    _tmpFilter.value.timeRange.update(min, max)
  }

  /** Applies the changes made to the filters. */
  override fun applyChanges() {
    _filter.value = _tmpFilter.value
    viewModelScope.launch {
      _recipes.value = emptyList()
      _currentRecipe.value = null
      _nextRecipe.value = null
      fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH)
      _loading.collect { isLoading ->
        if (!isLoading) {
          if (_recipes.value.isNotEmpty()) updateCurrentRecipe(_recipes.value.first())
          return@collect
        }
      }
    }
  }

  /** Resets all filters to their default values. */
  override fun resetFilters() {
    _tmpFilter.value = Filter()
  }

  /** Initializes the filter. */
  override fun initFilter() {

    _tmpFilter.value =
        Filter(
            _filter.value.timeRange.copy(),
            _filter.value.priceRange.copy(),
            _filter.value.difficulty,
            _filter.value.category)
  }
  /**
   * Updates the category filter.
   *
   * @param category The category to filter by.
   */
  override fun updateCategory(category: String?) {
    _tmpFilter.value.category = category
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
    if (_filter.value.category != null) {
      repository.searchByCategory(
          _filter.value.category!!,
          onSuccess = { recipes ->
            _recipes.value += recipes
            _loading.value = false
          },
          onFailure = { exception ->
            _loading.value = false
            Log.e("RecipesViewModel", "Error fetching recipes", exception)
          })
    } else {
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
            val repository = FirestoreRecipesRepository(Firebase.firestore)
            return RecipesViewModel(repository) as T
          }
        }
  }
}
