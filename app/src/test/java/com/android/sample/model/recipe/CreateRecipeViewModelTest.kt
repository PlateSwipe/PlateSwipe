package com.android.sample.model.recipe

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.feature.camera.rotateBitmap
import com.android.sample.model.image.ImageRepositoryFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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
    return Recipe(
        uid = "unique-id",
        name = "Test Recipe",
        category = "Dessert",
        origin = "Italian",
        instructions = "Some instructions",
        strMealThumbUrl = "unique-id",
        ingredientsAndMeasurements = listOf(Pair("Banana", "3")),
        url = null)
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
            .getDeclaredField("strMeal")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test updateRecipeCategory updates the category correctly`() {
    val newCategory = "Dessert"
    createRecipeViewModel.updateRecipeCategory(newCategory)
    assertEquals(
        newCategory,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("strCategory")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test updateRecipeInstructions updates the instructions correctly`() {
    val newInstructions = "New Instructions"
    createRecipeViewModel.updateRecipeInstructions(newInstructions)
    assertEquals(
        newInstructions,
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("strInstructions")
            .apply { isAccessible = true }
            .get(createRecipeViewModel.recipeBuilder))
  }

  @Test
  fun `test addIngredient adds the ingredient correctly`() {
    val ingredient = "Sugar"
    val measurement = "1 cup"
    createRecipeViewModel.addIngredient(ingredient, measurement)
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

    createRecipeViewModel.publishRecipe(onSuccess = {}, onFailure = {})

    assertEquals("Image is null", createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publishRecipe() with non null image call uploadImage`() {
    val defaultRecipe = createDefaultRecipe()
    // Check that the exception is thrown with the correct message
    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.setBitmap(bitmap, 90)
    createRecipeViewModel.publishRecipe(onSuccess = {}, onFailure = {})
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
    createRecipeViewModel.publishRecipe(onSuccess = {}, onFailure = {})
    assertEquals(
        "Failed to publish recipe: Failed to upload image",
        createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publisRecipe() with correct image call getImageUrl`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    createRecipeViewModel.updateRecipeInstructions(defaultRecipe.instructions)
    createRecipeViewModel.addIngredient("Banana", "3")
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)

    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    createRecipeViewModel.publishRecipe(onSuccess = {}, onFailure = {})

    // Ensure all coroutines have completed
    advanceUntilIdle()

    verify(mockImageRepository, times(1)).getImageUrl(any(), any(), any(), any(), any())
  }

  @Test
  fun `test publishRecipe publishes the recipe successfully`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    createRecipeViewModel.updateRecipeInstructions(defaultRecipe.instructions)
    createRecipeViewModel.updateRecipeThumbnail(defaultRecipe.strMealThumbUrl)
    createRecipeViewModel.addIngredient("Banana", "3")
    defaultRecipe.category?.let { createRecipeViewModel.updateRecipeCategory(it) }
    defaultRecipe.origin?.let { createRecipeViewModel.updateRecipeArea(it) }
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
    createRecipeViewModel.updateRecipeInstructions(defaultRecipe.instructions)
    createRecipeViewModel.addIngredient("Banana", "3")
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

    createRecipeViewModel.publishRecipe(onSuccess = {}, onFailure = {})
    assertEquals(
        "Failed to publish recipe: Failed to get Image Url",
        createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test publishRecipe addRecipe handles failure correctly`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    createRecipeViewModel.updateRecipeName(defaultRecipe.name)
    createRecipeViewModel.updateRecipeInstructions(defaultRecipe.instructions)
    createRecipeViewModel.addIngredient("Banana", "3")
    createRecipeViewModel.setBitmap(bitmap, 90)

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.uid)
    createRecipeViewModel.setBitmap(bitmap, 90)
    // Define the onFailure callback to validate if it was called correctly
    var onFailureCalled = false
    createRecipeViewModel.publishRecipe(
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

    createRecipeViewModel.publishRecipe(onSuccess = {}, onFailure = {})
    assertEquals(
        "Failed to publish recipe: Failed to add Recipe", createRecipeViewModel.publishStatus.value)
  }

  @Test
  fun `test clearPublishError resets publish error`() = runTest {
    `when`(mockRepository.getNewUid()).thenReturn("unique-id")

    // Call publishRecipe with onSuccess and onFailure callbacks
    createRecipeViewModel.publishRecipe(
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
    createRecipeViewModel.updateRecipeArea(newArea)

    val updatedArea =
        createRecipeViewModel.recipeBuilder.javaClass
            .getDeclaredField("strArea")
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
    createRecipeViewModel.addIngredient("", "1 cup")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `test addIngredient throws exception for blank measurement`() {
    createRecipeViewModel.addIngredient("Sugar", "")
  }

  @Test(expected = IllegalArgumentException::class)
  fun `test updateRecipeInstructions throws exception for blank instructions`() {
    createRecipeViewModel.updateRecipeInstructions("")
  }

  @Test
  fun `test all getters`() {
    val recipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(recipe.name)
    createRecipeViewModel.updateRecipeInstructions(recipe.instructions)
    createRecipeViewModel.updateRecipeThumbnail(recipe.strMealThumbUrl)
    createRecipeViewModel.addIngredient("Banana", "3")
    createRecipeViewModel.updateRecipeTime("30 minutes")
    createRecipeViewModel.updateRecipeDifficulty("Medium")
    createRecipeViewModel.updateRecipePrice("15.99")
    createRecipeViewModel.updateRecipeCategory("Dessert")
    createRecipeViewModel.updateRecipeArea("Italian")

    assertEquals(recipe.name, createRecipeViewModel.getRecipeName())
    assertEquals(recipe.instructions, createRecipeViewModel.getRecipeInstructions())
    assertEquals(recipe.strMealThumbUrl, createRecipeViewModel.getRecipeThumbnail())
    assertEquals(listOf(Pair("Banana", "3")), createRecipeViewModel.getIngredientsAndMeasurements())
    assertEquals("30 minutes", createRecipeViewModel.getRecipeTime())
    assertEquals("Medium", createRecipeViewModel.getRecipeDifficulty())
    assertEquals("15.99", createRecipeViewModel.getRecipePrice())
    assertEquals("Dessert", createRecipeViewModel.getRecipeCategory())
    assertEquals("Italian", createRecipeViewModel.getRecipeArea())
  }

  @Test
  fun `test update ingredientAndMeasurement`() {
    val recipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(recipe.name)
    createRecipeViewModel.updateRecipeInstructions(recipe.instructions)
    createRecipeViewModel.updateRecipeThumbnail(recipe.strMealThumbUrl)
    createRecipeViewModel.addIngredient("Banana", "3")
    createRecipeViewModel.updateRecipeTime("30 minutes")
    createRecipeViewModel.updateRecipeDifficulty("Medium")
    createRecipeViewModel.updateRecipePrice("15.99")
    createRecipeViewModel.updateRecipeCategory("Dessert")
    createRecipeViewModel.updateRecipeArea("Italian")

    createRecipeViewModel.updateIngredientAndMeasurement("Banana", "3", "Apple", "4")
    assertEquals(listOf(Pair("Apple", "4")), createRecipeViewModel.getIngredientsAndMeasurements())
  }

  @Test
  fun `test remove ingredientAndMeasurement`() {
    val recipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(recipe.name)
    createRecipeViewModel.updateRecipeInstructions(recipe.instructions)
    createRecipeViewModel.updateRecipeThumbnail(recipe.strMealThumbUrl)
    createRecipeViewModel.addIngredient("Banana", "3")
    createRecipeViewModel.updateRecipeTime("30 minutes")
    createRecipeViewModel.updateRecipeDifficulty("Medium")
    createRecipeViewModel.updateRecipePrice("15.99")
    createRecipeViewModel.updateRecipeCategory("Dessert")
    createRecipeViewModel.updateRecipeArea("Italian")

    createRecipeViewModel.removeIngredientAndMeasurement("Banana", "3")
    assertEquals(
        emptyList<Pair<String, String>>(), createRecipeViewModel.getIngredientsAndMeasurements())
  }
}
