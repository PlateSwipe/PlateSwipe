package com.android.sample.model.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for creating a new recipe.
 *
 * @property repository The repository used to add the recipe to Firestore.
 */
class CreateRecipeViewModel(private val repository: FirestoreRecipesRepository) : ViewModel() {

  private val recipeBuilder = RecipeBuilder()
  private val _publishError = MutableStateFlow<String?>(null)
  val publishError: StateFlow<String?>
    get() = _publishError

  fun updateRecipeName(name: String) {
    recipeBuilder.strMeal = name
  }

  fun updateRecipeCategory(category: String) {
    recipeBuilder.strCategory = category
  }

  fun updateRecipeInstructions(instructions: String) {
    recipeBuilder.strInstructions = instructions
  }

  fun addIngredient(ingredient: String, measurement: String) {
    recipeBuilder.addIngredient(ingredient, measurement)
  }

  fun updateRecipeThumbnail(url: String) {
    recipeBuilder.strMealThumbUrl = url
  }

  fun updateRecipeTime(time: String) {
    recipeBuilder.time = time
  }

  fun updateRecipeDifficulty(difficulty: String) {
    recipeBuilder.difficulty = difficulty
  }

  fun updateRecipePrice(price: String) {
    recipeBuilder.price = price
  }

  /** Publishes the recipe if all necessary fields are filled. */
  fun publishRecipe(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    // Ensure a unique ID is set before building the Recipe
    recipeBuilder.idMeal = repository.getNewUid()

    try {
      val recipe = recipeBuilder.build()
      repository.addRecipe(recipe, onSuccess = onSuccess, onFailure = onFailure)
    } catch (e: IllegalArgumentException) {
      _publishError.value = e.message
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firestore = FirebaseFirestore.getInstance()
            val repository = FirestoreRecipesRepository(firestore)
            return CreateRecipeViewModel(repository) as T
          }
        }
  }
}
