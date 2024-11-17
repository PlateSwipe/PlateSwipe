package com.android.sample.model.recipe

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.feature.camera.rotateBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepository
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.ERROR_NULL_IMAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISHED_SUCCESS_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISH_ERROR_MESSAGE
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlin.NullPointerException
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

  // Fields for the selected Instruction when modifying a recipe
  private val selectedDescription = MutableStateFlow<Int?>(null)
  private val selectedIcon = MutableStateFlow<IconType?>(null)

  /**
   * Selects the description at the given index. This is used when modifying a recipe.
   *
   * @param index The index of the description to select.
   */
  fun selectDescription(index: Int) {
    selectedDescription.value = index
  }

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

  /**
   * Updates the name of the recipe.
   *
   * @param name The new name of the recipe.
   */
  fun updateRecipeName(name: String) {
    require(name.isNotBlank()) { "Recipe name must not be blank." }
    recipeBuilder.setName(name)
  }

  /**
   * Updates the category of the recipe.
   *
   * @param category The new category of the recipe.
   */
  fun updateRecipeCategory(category: String) {
    recipeBuilder.setCategory(category)
  }

  /**
   * Updates the area of the recipe.
   *
   * @param area The new area of the recipe.
   */
  fun updateRecipeArea(area: String) {
    recipeBuilder.setOrigin(area)
  }

  /**
   * Updates the instructions for the recipe. WARNING : This method should be updated in the next
   * version of the instruction implementation.
   *
   * @param instructions The new instructions for the recipe.
   */
  fun updateRecipeInstructions(instructions: String) {
    require(instructions.isNotBlank()) { "Instructions must not be blank." }
    recipeBuilder.setInstructions(instructions)
  }

  /**
   * Updates the thumbnail for the recipe.
   *
   * @param url The new thumbnail URL for the recipe.
   */
  fun updateRecipeThumbnail(url: String) {
    recipeBuilder.setPictureID(url)
  }

  /**
   * Updates the time required to prepare the recipe. WARNING : This method should be updated in the
   * next version of the instruction implementation.
   *
   * @param time The new time required to prepare the recipe.
   */
  fun updateRecipeTime(time: String) {
    recipeBuilder.setTime(time)
  }

  /**
   * Updates the difficulty level of the recipe.
   *
   * @param difficulty The new difficulty level of the recipe.
   */
  fun updateRecipeDifficulty(difficulty: String) {
    recipeBuilder.setDifficulty(difficulty)
  }

  /**
   * Updates the price of the recipe.
   *
   * @param price The new price of the recipe.
   */
  fun updateRecipePrice(price: String) {
    recipeBuilder.setPrice(price)
  }

  /**
   * Adds an ingredient and its measurement to the recipe.
   *
   * @param ingredient The ingredient to add.
   * @param measurement The measurement of the ingredient.
   */
  fun addIngredient(ingredient: String, measurement: String) {
    require(ingredient.isNotBlank()) { "Ingredient must not be blank." }
    require(measurement.isNotBlank()) { "Measurement must not be blank." }
    recipeBuilder.addIngredientAndMeasurement(ingredient, measurement)
  }

  /**
   * Removes an ingredient and its measurement from the recipe.
   *
   * @param ingredient The ingredient to remove.
   * @param measurement The measurement of the ingredient.
   */
  fun removeIngredientAndMeasurement(ingredient: String, measurement: String) {
    recipeBuilder.deleteIngredientAndMeasurement(ingredient = ingredient, measurement = measurement)
  }

  /**
   * Updates an ingredient and its measurement in the recipe.
   *
   * @param ingredient The ingredient to update.
   * @param measurement The measurement of the ingredient.
   * @param newIngredient The new ingredient to replace the old one.
   * @param newMeasurement The new measurement to replace the old one.
   */
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

  /**
   * Gets the list of ingredients and their measurements.
   *
   * @return The list of ingredients and their measurements.
   */
  fun getIngredientsAndMeasurements(): List<Pair<String, String>> {
    return recipeBuilder.getIngredientsAndMeasurements()
  }

  /**
   * Gets the name of the recipe.
   *
   * @return The name of the recipe.
   */
  fun getRecipeName(): String {
    return recipeBuilder.getName()
  }

  /**
   * Gets the instructions for the recipe. WARNING : This method should be updated in the next
   * version of the instruction implementation.
   *
   * @return The instructions for the recipe.
   */
  fun getRecipeInstructions(): String {
    return recipeBuilder.getInstructions()
  }

  /**
   * Gets the category of the recipe.
   *
   * @return The category of the recipe.
   */
  fun getRecipeCategory(): String? {
    return recipeBuilder.getCategory()
  }

  /**
   * Gets the area of the recipe.
   *
   * @return The area of the recipe.
   */
  fun getRecipeArea(): String? {
    return recipeBuilder.getOrigin()
  }

  /**
   * Gets the thumbnail for the recipe.
   *
   * @return The thumbnail for the recipe.
   */
  fun getRecipeThumbnail(): String {
    return recipeBuilder.getPictureID()
  }

  /**
   * Gets the time required to prepare the recipe. WARNING : This method should be updated in the
   * next version of the instruction implementation.
   *
   * @return The time required to prepare the recipe.
   */
  fun getRecipeTime(): String? {
    return recipeBuilder.getTime()
  }

  /**
   * Gets the difficulty level of the recipe.
   *
   * @return The difficulty level of the recipe.
   */
  fun getRecipeDifficulty(): String? {
    return recipeBuilder.getDifficulty()
  }

  /**
   * Gets the price of the recipe.
   *
   * @return The price of the recipe.
   */
  fun getRecipePrice(): String? {
    return recipeBuilder.getPrice()
  }

  /** Clears the publish status. */
  fun clearPublishError() {
    _publishStatus.value = null
  }

  /**
   * Publishes the recipe to the repository.
   *
   * This function generates a new unique ID for the recipe, sets the ID in the recipe builder, and
   * uploads the photo associated with the recipe to the image repository. Once the image is
   * successfully uploaded, it retrieves the image URL and sets it in the recipe builder. Finally,
   * it adds the recipe to the repository and updates the publish status accordingly.
   *
   * If the photo is not set or if any error occurs during the process, it updates the publish
   * status with the appropriate error message.
   *
   * @throws IllegalArgumentException if the recipe ID is blank or if the image upload fails.
   */
  fun publishRecipe(onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    val newUid = repository.getNewUid()
    recipeBuilder.setId(newUid)
    try {
      if (_photo.value != null) {
        // Upload the image to the Image Repository with Name FIRESTORE_RECIPE_IMAGE_NAME and the
        // generated UID
        repoImg.uploadImage(
            newUid,
            C.Tag.FIRESTORE_RECIPE_IMAGE_NAME,
            ImageDirectoryType.RECIPE,
            _photo.value!!.asImageBitmap(),
            onSuccess = {
              // Set the Image UID to the Builder
              recipeBuilder.setPictureID(newUid)

              // Get the Image URL from the Image Repository
              repoImg.getImageUrl(
                  newUid,
                  C.Tag.FIRESTORE_RECIPE_IMAGE_NAME,
                  ImageDirectoryType.RECIPE,
                  onSuccess = { uri ->

                    // Set the URL to the Builder
                    val url = uri.toString()
                    recipeBuilder.setUrl(url)

                    // Build the Recipe
                    val recipe = recipeBuilder.build()

                    // Add the Recipe to the Repository
                    repository.addRecipe(
                        recipe,
                        onSuccess = {
                          onSuccess(recipe)
                          _publishStatus.value = RECIPE_PUBLISHED_SUCCESS_MESSAGE
                          recipeBuilder.clear()
                        },
                        onFailure = { exception ->
                          _publishStatus.value =
                              RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
                          onFailure(exception)
                        })
                  },
                  onFailure = { exception ->
                    _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
                  })
            },
            onFailure = { exception ->
              // Throw an error if the image upload fails
              _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
            })
      } else {
        throw NullPointerException(ERROR_NULL_IMAGE)
      }
    } catch (e: NullPointerException) {
      _publishStatus.value = e.message
    }
  }

  /**
   * Selects the icon for the recipe. WARNING : This method should be updated in the next version of
   * the instruction implementation.
   *
   * @param icon The icon to select.
   */
  fun selectIcon(icon: IconType) {
    selectedIcon.value = icon
  }

  /**
   * Gets the selected icon for the recipe. WARNING : This method should be updated in the next
   * version of the instruction implementation.
   *
   * @return The selected icon for the recipe.
   */
  fun getSelectedIcon(): IconType? {
    return selectedIcon.value
  }

  /**
   * Gets the icon at the given index. WARNING : This method should be updated in the next version
   * of the instruction implementation.
   *
   * @param index The index of the icon to get.
   * @return The icon at the given index.
   */
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
            val repoImg = ImageRepositoryFirebase(Firebase.storage)
            return CreateRecipeViewModel(repository, repoImg) as T
          }
        }
  }
}
