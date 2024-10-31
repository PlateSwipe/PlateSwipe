package com.android.sample.model.recipe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRecipeViewModelTest {

  private lateinit var mockRepository: FirestoreRecipesRepository
  private lateinit var createRecipeViewModel: CreateRecipeViewModel

  @Before
  fun setUp() {
    val testDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(testDispatcher)

    mockRepository = mock(FirestoreRecipesRepository::class.java)
    createRecipeViewModel = CreateRecipeViewModel(mockRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  private fun createDefaultRecipe(): Recipe {
    return Recipe(
        idMeal = "unique-id",
        strMeal = "Test Recipe",
        strCategory = "Dessert",
        strArea = "Italian",
        strInstructions = "Some instructions",
        strMealThumbUrl = "https://example.com/image.jpg",
        ingredientsAndMeasurements = listOf(Pair("Banana", "3")))
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
  fun `test publishRecipe publishes the recipe successfully`() = runTest {
    val defaultRecipe = createDefaultRecipe()
    createRecipeViewModel.updateRecipeName(defaultRecipe.strMeal)
    createRecipeViewModel.updateRecipeInstructions(defaultRecipe.strInstructions)
    createRecipeViewModel.updateRecipeThumbnail(defaultRecipe.strMealThumbUrl)
    createRecipeViewModel.addIngredient("Banana", "3")

    `when`(mockRepository.getNewUid()).thenReturn(defaultRecipe.idMeal)

    val recipeCaptor = argumentCaptor<Recipe>()
    val onSuccessCaptor = argumentCaptor<() -> Unit>()
    val onFailureCaptor = argumentCaptor<(Exception) -> Unit>()

    createRecipeViewModel.publishRecipe()
    advanceUntilIdle()

    verify(mockRepository)
        .addRecipe(recipeCaptor.capture(), onSuccessCaptor.capture(), onFailureCaptor.capture())

    assertEquals(defaultRecipe.idMeal, recipeCaptor.firstValue.idMeal)
    assertEquals(defaultRecipe.strMeal, recipeCaptor.firstValue.strMeal)
    assertEquals(defaultRecipe.strInstructions, recipeCaptor.firstValue.strInstructions)
    assertEquals(defaultRecipe.strMealThumbUrl, recipeCaptor.firstValue.strMealThumbUrl)
    assertEquals(
        defaultRecipe.ingredientsAndMeasurements,
        recipeCaptor.firstValue.ingredientsAndMeasurements)

    onSuccessCaptor.firstValue.invoke()

    assertEquals("Recipe published successfully!", createRecipeViewModel.publishError.value)
  }

  @Test
  fun `test publishRecipe handles failure correctly`() = runTest {
    val exception = Exception("Network error")

    createRecipeViewModel.updateRecipeName("Test Recipe")
    createRecipeViewModel.updateRecipeInstructions("Some instructions")
    createRecipeViewModel.updateRecipeThumbnail("https://example.com/image.jpg")
    createRecipeViewModel.addIngredient("Banana", "3")

    `when`(mockRepository.getNewUid()).thenReturn("unique-id")

    val recipeCaptor = argumentCaptor<Recipe>()
    val onSuccessCaptor = argumentCaptor<() -> Unit>()
    val onFailureCaptor = argumentCaptor<(Exception) -> Unit>()

    createRecipeViewModel.publishRecipe()
    advanceUntilIdle()

    verify(mockRepository)
        .addRecipe(recipeCaptor.capture(), onSuccessCaptor.capture(), onFailureCaptor.capture())

    onFailureCaptor.firstValue.invoke(exception)

    assertEquals(
        "Failed to publish recipe: Network error", createRecipeViewModel.publishError.value)
  }

  @Test
  fun `test clearPublishError resets publish error`() = runTest {
    `when`(mockRepository.getNewUid()).thenReturn("unique-id")

    createRecipeViewModel.updateRecipeName("")
    createRecipeViewModel.publishRecipe()
    advanceUntilIdle()

    assertNotNull(createRecipeViewModel.publishError.value)

    createRecipeViewModel.clearPublishError()
    assertNull(createRecipeViewModel.publishError.value)
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
}
