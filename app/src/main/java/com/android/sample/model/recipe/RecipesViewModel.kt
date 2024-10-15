package com.github.se.bootcamp.model.recipe

import androidx.lifecycle.ViewModel
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

  init {
    // Removed the fetchRandomRecipes() call from the init block for testing purposes
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
          _recipes.value = randomRecipes // Update the list of recipes
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
  }

  /** Clears the current selected recipe. */
  fun clearCurrentRecipe() {
    _currentRecipe.value = null
  }
}
