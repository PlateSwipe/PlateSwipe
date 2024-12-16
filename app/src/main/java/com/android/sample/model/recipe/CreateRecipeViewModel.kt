package com.android.sample.model.recipe

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.feature.camera.rotateBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepository
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.model.recipe.networkData.RecipeNetworkRepository
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.ERROR_NULL_IMAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISHED_SUCCESS_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_PUBLISH_ERROR_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_UPDATED_SUCCESS_MESSAGE
import com.android.sample.resources.C.Tag.RECIPE_UPDATE_ERROR_MESSAGE
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
    private val repository: RecipeNetworkRepository,
    private val repoImg: ImageRepository
) : ViewModel() {

  val recipeBuilder = RecipeBuilder()

  // Fields for the selected Instruction when modifying a recipe
  private val selectedInstruction = MutableStateFlow<Int?>(null)

  // Replace StateFlow with a simple mutable boolean
  var isRecipeInitialized = false

  /**
   * Initializes the recipe for editing.
   *
   * @param recipe The recipe to initialize.
   */
  fun initializeRecipeForEditing(recipe: Recipe) {
    recipeBuilder.initializeFromRecipe(recipe)
    isRecipeInitialized = true // Mark as initialized
  }

  /** Starts a new recipe. */
  fun startNewRecipe() {
    recipeBuilder.clear()
    isRecipeInitialized = true // Mark as initialized for new recipe
  }

  /** Resets the initialization state. */
  fun resetInitializationState() {
    recipeBuilder.clear()
    isRecipeInitialized = false // Reset state
  }

  /**
   * Selects the description at the given index. This is used when modifying a recipe.
   *
   * @param index The index of the description to select.
   */
  fun selectInstruction(index: Int) {
    selectedInstruction.value = index
  }

  /**
   * Resets the selected instruction. This is used when modifying a recipe.
   *
   * This function resets the selected instruction to null.
   */
  fun resetSelectedInstruction() {
    selectedInstruction.value = null
  }

  /**
   * Gets the selected instruction. This is used when modifying a recipe.
   *
   * @return The selected instruction index.
   */
  fun getSelectedInstruction(): Int? {
    return selectedInstruction.value
  }

  /**
   * Gets a single instruction at the given index.
   *
   * @param index The index of the instruction to get.
   */
  fun getInstruction(index: Int): Instruction {
    return recipeBuilder.getInstruction(index)
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
  fun updateRecipeCategory(category: String?) {
    recipeBuilder.setCategory(category)
  }

  /**
   * Updates the area of the recipe.
   *
   * @param origin The new area of the recipe.
   */
  fun updateRecipeOrigin(origin: String) {
    recipeBuilder.setOrigin(origin)
  }

  /**
   * Updates the instructions for the recipe.
   *
   * @param instructions The new instructions for the recipe.
   */
  fun addRecipeInstruction(instructions: Instruction) {
    recipeBuilder.addInstruction(instructions)
  }

  /**
   * Updates an instruction for the recipe.
   *
   * @param index The index of the instruction to update.
   * @param instruction The new instruction to replace the old one.
   */
  fun updateRecipeInstruction(index: Int, instruction: Instruction) {
    recipeBuilder.modifyInstruction(index, instruction)
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
   * Updates the time required to prepare the recipe.
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
  fun updateRecipeDifficulty(difficulty: String?) {
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
  fun addIngredientAndMeasurement(ingredient: String, measurement: String) {
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
   * Gets the instructions for the recipe.
   *
   * @return The instructions for the recipe.
   */
  fun getRecipeListOfInstructions(): List<Instruction> {
    return recipeBuilder.getInstructions()
  }

  /**
   * Gets the i th instruction of the recipe.
   *
   * @param index The index of the instruction.
   * @return The instruction at the given index.
   */
  fun getRecipeInstruction(index: Int): Instruction {
    return recipeBuilder.getInstruction(index)
  }

  /**
   * Deletes the i th instruction of the recipe.
   *
   * @param index The index of the instruction.
   */
  fun deleteRecipeInstruction(index: Int) {
    recipeBuilder.deleteInstruction(index)
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
  fun getRecipeOrigin(): String? {
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
   * Gets the time required to prepare the recipe.
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
   * Gets the ID of the recipe.
   *
   * @return The ID of the recipe.
   */
  fun getId(): String {
    return recipeBuilder.getId()
  }

  /**
   * Publishes a recipe to the repository, handling both creation and editing modes.
   *
   * For new recipes, generates a unique ID, uploads a photo if provided, and saves the recipe. For
   * edited recipes, updates the recipe with a new photo if provided, or reuses the existing image
   * URL. Calls `finalizeRecipePublish` to save or update the recipe in the repository.
   *
   * @param isEditing Whether the recipe is being edited (true) or newly created (false).
   * @param onSuccess Callback invoked on successful publishing.
   * @param onFailure Callback invoked on publishing failure.
   * @throws NullPointerException If no photo is provided and no existing image URL is available.
   */
  fun publishRecipe(
      isEditing: Boolean,
      onSuccess: (Recipe) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (!isEditing && recipeBuilder.getId().isBlank()) {
      val newUid = repository.getNewUid()
      recipeBuilder.setId(newUid)
    }

    try {
      if (_photo.value != null) {
        // Upload the new image
        repoImg.uploadImage(
            recipeBuilder.getId(),
            C.Tag.FIRESTORE_RECIPE_IMAGE_NAME,
            ImageDirectoryType.RECIPE,
            _photo.value!!.asImageBitmap(),
            onSuccess = {
              recipeBuilder.setPictureID(recipeBuilder.getId())
              repoImg.getImageUrl(
                  recipeBuilder.getId(),
                  C.Tag.FIRESTORE_RECIPE_IMAGE_NAME,
                  ImageDirectoryType.RECIPE,
                  onSuccess = { uri ->
                    recipeBuilder.setUrl(uri.toString())
                    finalizeRecipePublish(isEditing, onSuccess, onFailure)
                  },
                  onFailure = { exception ->
                    _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
                  })
            },
            onFailure = { exception ->
              _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
            })
      } else if (isEditing && recipeBuilder.getUrl() != null) {
        // Use the existing image URL in edit mode
        finalizeRecipePublish(isEditing, onSuccess, onFailure)
      } else {
        throw NullPointerException(ERROR_NULL_IMAGE)
      }
    } catch (e: NullPointerException) {
      _publishStatus.value = e.message
    }
  }

  /**
   * Saves the recipe to the repository, handling both creation and editing.
   *
   * Builds the recipe from the recipe builder and either adds it to the repository (for new
   * recipes) or updates it (for edited recipes). Invokes the appropriate callback on success or
   * failure.
   *
   * @param isEditing Whether the recipe is being edited (true) or newly created (false).
   * @param onSuccess Callback invoked on successful save or update.
   * @param onFailure Callback invoked on failure.
   * @throws IllegalArgumentException If the recipe builder fails to create a valid recipe.
   */
  private fun finalizeRecipePublish(
      isEditing: Boolean,
      onSuccess: (Recipe) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Build the recipe
      val recipe = recipeBuilder.build()

      if (isEditing) {
        // Update the existing recipe
        repository.updateRecipe(
            recipe,
            onSuccess = {
              onSuccess(recipe)
              _publishStatus.value = RECIPE_UPDATED_SUCCESS_MESSAGE
            },
            onFailure = { exception ->
              _publishStatus.value = RECIPE_UPDATE_ERROR_MESSAGE.format(exception.message)
              onFailure(exception)
            })
      } else {
        // Add the new recipe
        repository.addRecipe(
            recipe,
            onSuccess = {
              onSuccess(recipe)
              _publishStatus.value = RECIPE_PUBLISHED_SUCCESS_MESSAGE
              recipeBuilder.clear()
            },
            onFailure = { exception ->
              _publishStatus.value = RECIPE_PUBLISH_ERROR_MESSAGE.format(exception.message)
              onFailure(exception)
            })
      }
    } catch (e: IllegalArgumentException) {
      _publishStatus.value = e.message
      onFailure(e)
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
