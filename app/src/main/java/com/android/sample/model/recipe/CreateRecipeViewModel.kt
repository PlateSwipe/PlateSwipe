package com.android.sample.model.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.resources.C.Tag.RECIPE_PUBLISHED_SUCCESS_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISH_ERROR_MESSAGE
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

  fun updateRecipeName(name: String) {
    require(name.isNotBlank()) { "Recipe name must not be blank." }
    recipeBuilder.setName(name)
  }

  fun updateRecipeCategory(category: String) {
    recipeBuilder.setCategory(category)
  }

  fun updateRecipeArea(area: String) {
    recipeBuilder.setArea(area)
  }

  fun updateRecipeInstructions(instructions: String) {
    require(instructions.isNotBlank()) { "Instructions must not be blank." }
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
    require(ingredient.isNotBlank()) { "Ingredient must not be blank." }
    require(measurement.isNotBlank()) { "Measurement must not be blank." }
    recipeBuilder.addIngredientAndMeasurement(ingredient, measurement)
  }

  fun clearPublishError() {
    _publishError.value = null
  }

  fun publishRecipe() {
    val newUid = repository.getNewUid()
    require(newUid.isNotBlank()) { "Recipe ID must not be blank." }
    recipeBuilder.setId(newUid)

    try {
      val recipe = recipeBuilder.build()
      repository.addRecipe(
          recipe,
          onSuccess = {
            _publishError.value = RECIPE_PUBLISHED_SUCCESS_MESSAGE
            recipeBuilder.clear()
          },
          onFailure = { exception ->
            _publishError.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
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
