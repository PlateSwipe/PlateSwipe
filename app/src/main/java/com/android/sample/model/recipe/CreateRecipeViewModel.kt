package com.android.sample.model.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.resources.C.Tag.RECIPE_PUBLISHED_SUCCESS_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISH_ERROR_MESSAGE
import com.android.sample.ui.createRecipe.IconType
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

  // Fields for the selected Instruction when modifying a recipe
  private val selectedDescription = MutableStateFlow<Int?>(null)
  private val selectedIcon = MutableStateFlow<IconType?>(null)

  fun selectDescription(index: Int) {
    selectedDescription.value = index
  }

  private val _publishStatus = MutableStateFlow<String?>(null)
  val publishStatus: StateFlow<String?>
    get() = _publishStatus

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

  fun removeIngredientAndMeasurement(ingredient: String, measurement: String) {
    recipeBuilder.deleteIngredientAndMeasurement(ingredient = ingredient, measurement = measurement)
  }

  fun updateIngredientAndMeasurement(
      ingredient: String,
      measurement: String,
      newIngredient: String,
      newMeasurement: String
  ) {
    recipeBuilder.updateIngredientAndMeasurement(
        ingredient = ingredient,
        measurement = measurement,
        newIngredient = newIngredient,
        newMeasurement = newMeasurement)
  }

  fun getIngredientsAndMeasurements(): List<Pair<String, String>> {
    return recipeBuilder.getIngredientsAndMeasurements()
  }

  fun getRecipeName(): String {
    return recipeBuilder.getName()
  }

  fun getRecipeInstructions(): String {
    return recipeBuilder.getInstructions()
  }

  fun getRecipeCategory(): String? {
    return recipeBuilder.getCategory()
  }

  fun getRecipeArea(): String? {
    return recipeBuilder.getArea()
  }

  fun getRecipeThumbnail(): String {
    return recipeBuilder.getPictureID()
  }

  fun getRecipeTime(): String? {
    return recipeBuilder.getTime()
  }

  fun getRecipeDifficulty(): String? {
    return recipeBuilder.getDifficulty()
  }

  fun getRecipePrice(): String? {
    return recipeBuilder.getPrice()
  }

  fun clearPublishError() {
    _publishStatus.value = null
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
            _publishStatus.value = RECIPE_PUBLISHED_SUCCESS_MESSAGE
            recipeBuilder.clear()
          },
          onFailure = { exception ->
            _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
          })
    } catch (e: IllegalArgumentException) {
      _publishStatus.value = e.message
    }
  }

  fun selectIcon(icon: IconType) {
    selectedIcon.value = icon
  }

  fun getSelectedIcon(): IconType? {
    return selectedIcon.value
  }

  fun getIcon(index: Int): IconType? {
    return selectedIcon.value
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
