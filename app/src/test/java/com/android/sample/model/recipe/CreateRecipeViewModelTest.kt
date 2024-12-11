package com.android.sample.model.recipe

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.feature.camera.rotateBitmap
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.resources.C.Tag.RECIPE_UPDATED_SUCCESS_MESSAGE
import com.android.sample.ui.utils.testRecipes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CreateRecipeViewModelTest {

  private lateinit var mockRepository: FirestoreRecipesRepository
  private lateinit var mockImageRepository: ImageRepositoryFirebase
  private lateinit var createRecipeViewModel: CreateRecipeViewModel

  @Before
  fun setUp() {
    val testDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(testDispatcher)

    mockRepository = mock(FirestoreRecipesRepository::class.java)
    mockImageRepository = mock(ImageRepositoryFirebase::class.java)
    createRecipeViewModel = CreateRecipeViewModel(mockRepository, mockImageRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  private fun createDefaultRecipe(): Recipe {
    return testRecipes[0]
  }

  @Test
  fun setBitmap() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    // Check if the photo is set to null by default
    assert(createRecipeViewModel.photo.value == null)
    createRecipeViewModel.setBitmap(bitmap, 90)
    val rotatedBitmap = rotateBitmap(bitmap, 90)
    assertTrue(rotatedBitmap.sameAs(createRecipeViewModel.photo.value))
  }

  @Test
  fun `test updateRecipeName updates the name correctly`() {
    val newName = "New Recipe Name"
    createRecipeViewModel.updateRecipeName(newName)
    assertEquals(
        newName,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("name")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test addRecipeInstruction updates the category correctly`() {
    val newCategory = "Dessert"
    createRecipeViewModel.updateRecipeCategory(newCategory)
    assertEquals(
        newCategory,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("category")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test addRecipeInstruction updates the instructions correctly`() {
    val newInstructions = "New Instructions"
    createRecipeViewModel.addRecipeInstruction(Instruction(newInstructions, null, null))
    assertEquals(
        listOf(Instruction(newInstructions, null, null)),
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("instructions")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test addIngredient adds the ingredient correctly`() {
    val ingredient = "Sugar"
    val measurement = "1 cup"
    createRecipeViewModel.addIngredientAndMeasurement(ingredient, measurement)
    val ingredients =
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("ingredientsAndMeasurements")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder) as List<Pair<String, String>>
    assertTrue(ingredients.contains(Pair(ingredient, measurement)))
  }

  @Test
  fun `test updateRecipeThumbnail updates the thumbnail URL correctly`() {
    val newUrl = "https://example.com/new-thumbnail.jpg"
    createRecipeViewModel.updateRecipeThumbnail(newUrl)
    assertEquals(
        newUrl,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("strMealThumbUrl")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test updateRecipeTime updates the time correctly`() {
    val newTime = "30 minutes"
    createRecipeViewModel.updateRecipeTime(newTime)
    assertEquals(
        newTime,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("time")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test updateRecipeDifficulty updates the difficulty correctly`() {
    val newDifficulty = "Medium"
    createRecipeViewModel.updateRecipeDifficulty(newDifficulty)
    assertEquals(
        newDifficulty,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("difficulty")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test updateRecipePrice updates the price correctly`() {
    val newPrice = "15.99"
    createRecipeViewModel.updateRecipePrice(newPrice)
    assertEquals(
        newPrice,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("price")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test publishRecipe() with null image throw IllegalArgument`() {
    assert(createRecipeViewModel.photo.value == null)
    val defaultRecipe = createDefaultRecipe()
    // Check that the exception is thrown with the correct message
    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})

    assertEquals("Image is null", createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publishRecipe() with non null image call uploadImage`() {
    val defaultRecipe = createDefaultRecipe()
    // Check that the exception is thrown with the correct message
    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.setBitmap(bitmap, 90)
    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})
    verify(mockImageRepository, times(1))
        .uploadImage(any(), any(), any(), any(), onSuccess = any(), onFailure = any())
  }

  @Test
  fun `test publishRecipe() fail to Upload Image throw IllegalArgument`() {
    val defaultRecipe = createDefaultRecipe()
    // Check that the exception is thrown with the correct message
    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.setBitmap(bitmap, 90)
    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer {
          (it.arguments[5] as (Exception) -> Unit).invoke(Exception("Failed to upload image"))
        }
    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})
    assertEquals(
        "Failed to publish recipe: Failed to upload image",
        createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publisRecipe() with correct image call getImageUrl`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    for (instruction in defaultRecipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.addIngredientAndMeasurement("Banana", "3")
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})

    // Ensure all coroutines have completed
    advanceUntilIdle()

    verify(mockImageRepository, times(1)).getImageUrl(any(), any(), any(), any(), any())
  }

  @Test
  fun `test publishRecipe publishes the recipe successfully`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    for (instruction in defaultRecipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.updateRecipeThumbnail(defaultRecipe.strMealThumbUrl)
    defaultRecipe.ingredientsAndMeasurements.forEach {
      createRecipeViewModel.addIngredientAndMeasurement(it.first, it.second)
    }
    defaultRecipe.category?.let { createRecipeViewModel.updateRecipeCategory(it) }
    defaultRecipe.origin?.let { createRecipeViewModel.updateRecipeOrigin(it) }
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[3] as (Uri) -> Unit
          onSuccessCallback(Uri.EMPTY)
        }

    val recipeCaptor = argumentCaptor<Recipe>()
    val onSuccessCaptor = argumentCaptor<() -> Unit>()
    val onFailureCaptor = argumentCaptor<(Exception) -> Unit>()

    // Define the onSuccess callback to validate if it was called correctly
    var onSuccessCalled = false
    createRecipeViewModel.publishRecipe(
        isEditing = false,
        onSuccess = { recipe -> onSuccessCalled = true },
        onFailure = { fail("Expected onSuccess, but onFailure was called instead.") })

    advanceUntilIdle()

    verify(mockRepository)
        .addRecipe(recipeCaptor.capture(), onSuccessCaptor.capture(), onFailureCaptor.capture())

    assertEquals(defaultRecipe.uid, recipeCaptor.firstValue.uid)
    assertEquals(defaultRecipe.name, recipeCaptor.firstValue.name)
    assertEquals(defaultRecipe.instructions, recipeCaptor.firstValue.instructions)
    assertEquals(defaultRecipe.uid, recipeCaptor.firstValue.strMealThumbUrl)
    assertEquals(
        defaultRecipe.ingredientsAndMeasurements,
        recipeCaptor.firstValue.ingredientsAndMeasurements)

    onSuccessCaptor.firstValue.invoke()

    assertEquals("Recipe published successfully!", createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publishRecipe getImageUrl handles failure correctly`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    for (instruction in defaultRecipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.addIngredientAndMeasurement("Banana", "3")
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer {
          (it.arguments[4] as (Exception) -> Unit).invoke(Exception("Failed to get Image Url"))
        }

    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})
    assertEquals(
        "Failed to publish recipe: Failed to get Image Url",
        createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publishRecipe addRecipe handles failure correctly`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    for (instruction in defaultRecipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.addIngredientAndMeasurement("Banana", "3")
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)
    createRecipeViewModel.setBitmap(bitmap, 90)
    // Define the onFailure callback to validate if it was called correctly
    var onFailureCalled = false
    createRecipeViewModel.publishRecipe(
        isEditing = false,
        onSuccess = { fail("Expected onFailure, but onSuccess was called instead.") },
        onFailure = { errorMessage ->
          onFailureCalled = true
          assertEquals("Network error", errorMessage.message)
        })

    advanceUntilIdle()

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }
    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[3] as (Uri) -> Unit
          onSuccessCallback(Uri.EMPTY)
        }

    `when`(mockRepository.addRecipe(any(), onSuccess = any(), onFailure = any())).thenAnswer {
      (it.arguments[2] as (Exception) -> Unit).invoke(Exception("Failed to add Recipe"))
    }

    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})
    assertEquals(
        "Failed to publish recipe: Failed to add Recipe", createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test clearPublishError resets publish error`() = runTest {
    `when`(mockRepository.getNewUid()).thenReturn("unique-id")

    // Call publishRecipe with onSuccess and onFailure callbacks
    createRecipeViewModel.publishRecipe(
        isEditing = false,
        onSuccess = {
          fail(
              "Expected onFailure to be called due to invalid recipe, but onSuccess was called instead.")
        },
        onFailure = { exception ->
          // Check if the publishStatus is set with the error message
          assertNotNull(
              "Expected publishStatus to be set on failure, but it was null.",
              createRecipeViewModel.publishStatus.value)
          assertEquals(
              "Recipe name is required and cannot be blank.",
              createRecipeViewModel.publishStatus.value)
        })

    advanceUntilIdle()

    assertNotNull(createRecipeViewModel.publishStatus.value)
    createRecipeViewModel.clearPublishError()
    assertNull(createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test updateRecipeArea updates the area correctly`() {
    val newArea = "Italian"
    createRecipeViewModel.updateRecipeOrigin(newArea)

    val updatedArea =
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("origin")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder)

    assertEquals(newArea, updatedArea)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `test updateRecipeName throws exception for blank name`() {
    createRecipeViewModel.updateRecipeName("")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `test addIngredient throws exception for blank ingredient`() {
    createRecipeViewModel.addIngredientAndMeasurement("", "1 cup")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `test addIngredient throws exception for blank measurement`() {
    createRecipeViewModel.addIngredientAndMeasurement("Sugar", "")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `test addRecipeInstruction throws exception for blank instructions`() {
    createRecipeViewModel.addRecipeInstruction(Instruction("", null, null))
  }

  @Test
  fun `test all getters`() {
    val recipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(recipe.name)
    for (instruction in recipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.updateRecipeThumbnail(recipe.strMealThumbUrl)
    createRecipeViewModel.addIngredientAndMeasurement("Banana", "3")
    createRecipeViewModel.updateRecipeTime("30 minutes")
    createRecipeViewModel.updateRecipeDifficulty("Medium")
    createRecipeViewModel.updateRecipePrice("15.99")
    createRecipeViewModel.updateRecipeCategory("Dessert")
    createRecipeViewModel.updateRecipeOrigin("Italian")

    assertEquals(recipe.name, createRecipeViewModel.getRecipeName())
    assertEquals(recipe.instructions, createRecipeViewModel.getRecipeListOfInstructions())
    assertEquals(recipe.strMealThumbUrl, createRecipeViewModel.getRecipeThumbnail())
    assertEquals(listOf(Pair("Banana", "3")), createRecipeViewModel.getIngredientsAndMeasurements())
    assertEquals("30 minutes", createRecipeViewModel.getRecipeTime())
    assertEquals("Medium", createRecipeViewModel.getRecipeDifficulty())
    assertEquals("15.99", createRecipeViewModel.getRecipePrice())
    assertEquals("Dessert", createRecipeViewModel.getRecipeCategory())
    assertEquals("Italian", createRecipeViewModel.getRecipeOrigin())
  }

  @Test
  fun `test update ingredientAndMeasurement`() {
    val recipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(recipe.name)
    for (instruction in recipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.updateRecipeThumbnail(recipe.strMealThumbUrl)
    createRecipeViewModel.addIngredientAndMeasurement("Banana", "3")
    createRecipeViewModel.updateRecipeTime("30 minutes")
    createRecipeViewModel.updateRecipeDifficulty("Medium")
    createRecipeViewModel.updateRecipePrice("15.99")
    createRecipeViewModel.updateRecipeCategory("Dessert")
    createRecipeViewModel.updateRecipeOrigin("Italian")

    createRecipeViewModel.updateIngredientAndMeasurement("Banana", "3", "Apple", "4")
    assertEquals(listOf(Pair("Apple", "4")), createRecipeViewModel.getIngredientsAndMeasurements())
  }

  @Test
  fun `test remove ingredientAndMeasurement`() {
    val recipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(recipe.name)
    for (instruction in recipe.instructions) {
      createRecipeViewModel.addRecipeInstruction(instruction)
    }
    createRecipeViewModel.updateRecipeThumbnail(recipe.strMealThumbUrl)
    createRecipeViewModel.addIngredientAndMeasurement("Banana", "3")
    createRecipeViewModel.updateRecipeTime("30 minutes")
    createRecipeViewModel.updateRecipeDifficulty("Medium")
    createRecipeViewModel.updateRecipePrice("15.99")
    createRecipeViewModel.updateRecipeCategory("Dessert")
    createRecipeViewModel.updateRecipeOrigin("Italian")

    createRecipeViewModel.removeIngredientAndMeasurement("Banana", "3")
    assertEquals(
        emptyList<Pair<String, String>>(), createRecipeViewModel.getIngredientsAndMeasurements())
  }

  @Test
  fun `test publishRecipe with incorrect recipe field throw error`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    createRecipeViewModel.updateRecipeName("Chocolat") // Blank name
    createRecipeViewModel.addRecipeInstruction(Instruction("www www www", null, null))
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)
    createRecipeViewModel.setBitmap(bitmap, 90)
    // Define the onFailure callback to validate if it was called correctly
    var onFailureCalled = false
    createRecipeViewModel.publishRecipe(
        isEditing = false,
        onSuccess = { fail("Expected onFailure, but onSuccess was called instead.") },
        onFailure = { errorMessage ->
          onFailureCalled = true
          assertEquals("At least one ingredient is required.", errorMessage.message)
        })

    advanceUntilIdle()

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }
    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[3] as (Uri) -> Unit
          onSuccessCallback(Uri.EMPTY)
        }

    createRecipeViewModel.publishRecipe(isEditing = false, onSuccess = {}, onFailure = {})
    assertEquals("At least one ingredient is required.", createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `deleteInstruction deletes the instruction correctly`() {
    val instruction = Instruction("Preheat oven to 180°C...")
    createRecipeViewModel.addRecipeInstruction(instruction)
    createRecipeViewModel.deleteRecipeInstruction(0)
    assertTrue(createRecipeViewModel.getRecipeListOfInstructions().isEmpty())
  }

  @Test
  fun `test updateRecipeCategory accepts null value`() {
    // Update the category with a non-null value first
    val initialCategory = "Dessert"
    createRecipeViewModel.updateRecipeCategory(initialCategory)
    assertEquals(
        initialCategory,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("category")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))

    // Update the category with a null value
    createRecipeViewModel.updateRecipeCategory(null)
    assertNull(
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("category")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test initializeRecipeForEditing initializes the recipe correctly`() {
    val recipe = createDefaultRecipe()

    createRecipeViewModel.initializeRecipeForEditing(recipe)

    // Verify recipe builder contains the recipe's data
    assertEquals(recipe.name, createRecipeViewModel.getRecipeName())
    assertEquals(recipe.instructions, createRecipeViewModel.getRecipeListOfInstructions())
    assertEquals(recipe.strMealThumbUrl, createRecipeViewModel.getRecipeThumbnail())
    assertEquals(
        recipe.ingredientsAndMeasurements, createRecipeViewModel.getIngredientsAndMeasurements())
    assertTrue(createRecipeViewModel.isRecipeInitialized)
  }

  @Test
  fun `test startNewRecipe clears recipeBuilder and initializes`() {
    val recipe = createDefaultRecipe()

    // Add some data to the builder
    createRecipeViewModel.initializeRecipeForEditing(recipe)

    // Start a new recipe
    createRecipeViewModel.startNewRecipe()

    // Verify the recipe builder is cleared and initialized
    assertEquals("", createRecipeViewModel.getRecipeName())
    assertTrue(createRecipeViewModel.getRecipeListOfInstructions().isEmpty())
    assertTrue(createRecipeViewModel.getIngredientsAndMeasurements().isEmpty())
    assertTrue(createRecipeViewModel.isRecipeInitialized)
  }

  @Test
  fun `test resetInitializationState clears builder and resets state`() {
    val recipe = createDefaultRecipe()

    // Add some data to the builder and set initialization state
    createRecipeViewModel.initializeRecipeForEditing(recipe)

    // Reset initialization state
    createRecipeViewModel.resetInitializationState()

    // Verify the builder is cleared and initialization state is reset
    assertEquals("", createRecipeViewModel.getRecipeName())
    assertTrue(createRecipeViewModel.getRecipeListOfInstructions().isEmpty())
    assertTrue(createRecipeViewModel.getIngredientsAndMeasurements().isEmpty())
    assertFalse(createRecipeViewModel.isRecipeInitialized)
  }

  @Test
  fun `test publishRecipe on edit mode updates the recipe successfully`() = runTest {
    // Arrange
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    // Mock existing recipe details in the recipeBuilder
    createRecipeViewModel.initializeRecipeForEditing(defaultRecipe)
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[3] as (Uri) -> Unit
          onSuccessCallback(Uri.EMPTY)
        }

    `when`(mockRepository.updateRecipe(any(), onSuccess = any(), onFailure = any())).thenAnswer {
        invocation ->
      val onSuccessCallback = invocation.arguments[1] as () -> Unit
      onSuccessCallback()
    }

    // Act
    var onSuccessCalled = false
    createRecipeViewModel.publishRecipe(
        isEditing = true,
        onSuccess = { onSuccessCalled = true },
        onFailure = { fail("Expected onSuccess, but onFailure was called instead.") })

    // Ensure all coroutines complete
    advanceUntilIdle()

    // Assert
    assertTrue(onSuccessCalled)
    assertEquals(RECIPE_UPDATED_SUCCESS_MESSAGE, createRecipeViewModel.publishStatus.value)
    verify(mockRepository).updateRecipe(any(), any(), any())
  }

  @Test
  fun `test getId returns correct recipe ID`() {
    // Arrange
    val recipeId = "test-recipe-id"
    createRecipeViewModel.recipeBuilder.setId(recipeId)

    // Act
    val retrievedId = createRecipeViewModel.getId()

    // Assert
    assertEquals(recipeId, retrievedId)
  }
}
