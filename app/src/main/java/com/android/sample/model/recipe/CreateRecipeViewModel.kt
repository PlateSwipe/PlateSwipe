package com.android.sample.model.recipe

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.feature.camera.rotateBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepository
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.resources.C.Tag.RECIPE_PUBLISHED_SUCCESS_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISH_ERROR_MESSAGE
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for creating a new recipe.
 *
 * @property repository The repository used to add the recipe to Firestore.
 */
class CreateRecipeViewModel(
    private val repository: RecipesRepository,
    private val repoImg: ImageRepository
) : ViewModel() {

  val recipeBuilder = RecipeBuilder()
  private val _publishStatus = MutableStateFlow<String?>(null)
  val publishStatus: StateFlow<String?>
    get() = _publishStatus

  // MutableStateFlow to hold the Bitmap photo
  private val _photo = MutableStateFlow<Bitmap?>(null)

  /** StateFlow to expose the photo as an immutable state. */
  val photo: StateFlow<Bitmap?>
    get() = _photo

  /**
   * Sets the Bitmap photo and rotate it if needed.
   *
   * @param bitmap The Bitmap to set.
   * @param rotation The rotation needed for the bitmap to be in the right orientation.
   */
  fun setBitmap(bitmap: Bitmap, rotation: Int) {
    if (_photo.value != bitmap) {
      rotateBitmap(bitmap, rotation).let { rotatedBitmap -> _photo.value = rotatedBitmap }
    }
  }

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
    println("CreateRecipeViewModel: publishRecipe: newUid: $newUid")
    try {
      if (_photo.value != null) {
        repoImg.uploadImage(
            newUid,
            "BOSS",
            ImageDirectoryType.USER,
            _photo.value!!.asImageBitmap(),
            onSuccess = {
              // Set the Image UID to the Builder
              recipeBuilder.setPictureID(newUid)
              val recipe = recipeBuilder.build()
              Log.d(
                  "CreateRecipeViewModel", "Recipe built successfully : ${recipe.strMealThumbUrl}")
              repository.addRecipe(
                  recipe,
                  onSuccess = {
                    _publishStatus.value = RECIPE_PUBLISHED_SUCCESS_MESSAGE
                    recipeBuilder.clear()
                  },
                  onFailure = { exception ->
                    _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
                  })
            },
            onFailure = {
              // Throw an error if the image upload fails
              throw IllegalArgumentException("Image upload failed.")
            })
      } else {
        throw IllegalArgumentException("Image must not be blank.")
      }
    } catch (e: IllegalArgumentException) {
      _publishStatus.value = e.message
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firestore = FirebaseFirestore.getInstance()
            val repository = FirestoreRecipesRepository(firestore)
            val repoImg = ImageRepositoryFirebase(Firebase.storage)
            return CreateRecipeViewModel(repository, repoImg) as T
          }
        }
  }
}
