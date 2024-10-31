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

  val recipeBuilder = RecipeBuilder()
  private val _publishError = MutableStateFlow<String?>(null)
  val publishError: StateFlow<String?>
    get() = _publishError

  fun updateRecipeId(id: String) {
    recipeBuilder.setId(id)
  }

  fun updateRecipeName(name: String) {
    recipeBuilder.setName(name)
  }

  fun updateRecipeCategory(category: String) {
    recipeBuilder.setCategory(category)
  }

  fun updateRecipeArea(area: String) {
    recipeBuilder.setArea(area)
  }

  fun updateRecipeInstructions(instructions: String) {
    recipeBuilder.setInstructions(instructions)
  }

  fun updateRecipeThumbnail(url: String) {
    recipeBuilder.setPictureID(url)
  }

  fun updateRecipeTime(time: String) {
    recipeBuilder.setTime(time)
  }

  fun updateRecipeDifficulty(difficulty: String) {
    recipeBuilder.setDifficulty(difficulty)
  }

  fun updateRecipePrice(price: String) {
    recipeBuilder.setPrice(price)
  }

  fun addIngredient(ingredient: String, measurement: String) {
    recipeBuilder.addIngredientAndMeasurement(ingredient, measurement)
  }

  fun clearPublishError() {
    _publishError.value = null
  }

  fun publishRecipe() {
    recipeBuilder.setId(repository.getNewUid())

    try {
      val recipe = recipeBuilder.build()
      repository.addRecipe(
          recipe,
          onSuccess = { _publishError.value = "Recipe published successfully!" },
          onFailure = { exception ->
            _publishError.value = "Failed to publish recipe: ${exception.message}"
          })
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
