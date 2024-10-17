package com.android.sample.model.recipe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

/** Unit tests for RecipesViewModel. */
class RecipesViewModelTest {
  private lateinit var recipeRepository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  // Dummy recipes for testing
  private val dummyRecipes: List<Recipe> =
      listOf(
          Recipe(
              idMeal = "1",
              strMeal = "Spicy Arrabiata Penne",
              strCategory = "Vegetarian",
              strArea = "Italian",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
              ingredientsAndMeasurements =
                  listOf(Pair("Penne", "1 pound"), Pair("Olive oil", "1/4 cup"))),
          Recipe(
              idMeal = "2",
              strMeal = "Chicken Curry",
              strCategory = "Non-Vegetarian",
              strArea = "Indian",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/",
              ingredientsAndMeasurements =
                  listOf(Pair("Chicken", "1 pound"), Pair("Curry powder", "2 tbsp"))))

  @Before
  fun setUp() {
    // Set the main dispatcher for tests
    val testDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(testDispatcher)

    // Mock the RecipeRepository
    recipeRepository = mock(RecipeRepository::class.java)
    // Initialize the RecipesViewModel with the mocked repository
    recipesViewModel = RecipesViewModel(recipeRepository)
  }

  @After
  fun tearDown() {
    // Reset the main dispatcher after tests
    Dispatchers.resetMain()
  }

  @Test
  fun initialStateIsCorrect() {
    // Assert initial state is correct
    assertThat(recipesViewModel.recipes.value, `is`(emptyList<Recipe>()))
    assertThat(recipesViewModel.loading.value, `is`(false))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun initFetchesInitialRecipeAndUpdatesCurrentRecipe() = runTest {
    // Arrange: Mock the repository to return dummy recipes
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Act: Initialize the ViewModel
    recipesViewModel = RecipesViewModel(recipeRepository)

    // Wait for the coroutine to complete
    advanceUntilIdle()

    // Assert: Verify that the initial recipe is fetched and set as the current recipe
    assertNotNull(recipesViewModel.currentRecipe.value) // Ensure current recipe is not null
    assertThat(
        recipesViewModel.currentRecipe.value,
        `is`(dummyRecipes[0])) // Check the current recipe is the first one
  }

  @Test
  fun fetchRandomRecipesUpdatesState() {
    // Simulate the behavior of the repository
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Act
    recipesViewModel.fetchRandomRecipes(2)

    // Assert
    assertThat(
        recipesViewModel.recipes.value,
        `is`(dummyRecipes)) // Check if the ViewModel's recipes are updated
    assertThat(recipesViewModel.loading.value, `is`(false)) // Check loading is false after fetching
  }

  @Test
  fun fetchRandomRecipesCallsRepository() {
    // Arrange
    val numberOfRecipes = 2

    // Act
    recipesViewModel.fetchRandomRecipes(numberOfRecipes)

    // Assert
    verify(recipeRepository)
        .random(
            eq(numberOfRecipes),
            any(),
            any()) // Verify that the repository's random method is called
  }

  @Test
  fun fetchRandomRecipesHandlesFailure() {
    // Simulate the failure of the repository
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Network error")) // Simulate a failure
    }

    // Act
    recipesViewModel.fetchRandomRecipes(2)

    // Assert
    assertThat(recipesViewModel.loading.value, `is`(false)) // Check loading is false after fetch
    assertThat(
        recipesViewModel.recipes.value,
        `is`(emptyList<Recipe>())) // Ensure no recipes are set on failure
  }

  @Test
  fun fetchRandomRecipesThrowsExceptionForInvalidNumber() {
    // Arrange
    val invalidNumberOfRecipes = 0

    // Act & Assert
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          recipesViewModel.fetchRandomRecipes(invalidNumberOfRecipes)
        }
    assertThat(exception.message, `is`("Number of fetched recipes must be at least 1"))
  }

  @Test
  fun fetchRandomRecipesAppendsToExistingList() {
    // Arrange: Mock the repository to return dummy recipes
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Act: Initially fetch 2 dummy recipes
    recipesViewModel.fetchRandomRecipes(2)

    // Fetch additional random recipes
    recipesViewModel.fetchRandomRecipes(2) // Fetch 2 more recipes

    // Assert: Verify that the recipes in the ViewModel now contain the original and the new ones
    assertThat(recipesViewModel.recipes.value.size, `is`(4)) // Expecting 4 total recipes
    assertThat(
        recipesViewModel.recipes.value,
        `is`(
            dummyRecipes +
                dummyRecipes)) // Check if the ViewModel's recipes are the combination of the
    // originals
  }

  @Test
  fun updateCurrentRecipeUpdatesState() {
    // Arrange
    val recipe = dummyRecipes[0]

    // Act
    recipesViewModel.updateCurrentRecipe(recipe)

    // Assert
    assertThat(recipesViewModel.currentRecipe.value, `is`(recipe))
  }

  @Test
  fun clearCurrentRecipeClearsState() {
    // Arrange
    val recipe = dummyRecipes[0]
    recipesViewModel.updateCurrentRecipe(recipe)

    // Act
    recipesViewModel.clearCurrentRecipe()

    // Assert
    assertThat(recipesViewModel.currentRecipe.value, `is`(nullValue()))
  }

  @Test
  fun nextRecipeCyclesCorrectly() {
    // Arrange
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // First fetch recipes to populate the ViewModel
    recipesViewModel.fetchRandomRecipes(2)
    recipesViewModel.updateCurrentRecipe(dummyRecipes[0]) // Set the first recipe as current

    // Act
    recipesViewModel.nextRecipe() // Get the next recipe

    // Assert
    assertThat(
        recipesViewModel.currentRecipe.value,
        `is`(dummyRecipes[1])) // Check the current recipe is now the second one
    assertThat(
        recipesViewModel.recipes.value.size, `is`(1)) // Check that one recipe has been removed
    assertThat(
        recipesViewModel.recipes.value[0],
        `is`(dummyRecipes[0])) // Ensure the first recipe is still in the list
  }

  @Test
  fun nextRecipeWrapsAround() {
    // Arrange
    recipesViewModel.fetchRandomRecipes(2) // Fetch dummy recipes
    recipesViewModel.updateCurrentRecipe(dummyRecipes[0]) // Set the first recipe as current

    // Act
    recipesViewModel.nextRecipe() // Get the next recipe
    recipesViewModel.nextRecipe() // Get the next recipe again (should wrap around)

    // Assert
    assertThat(
        recipesViewModel.currentRecipe.value,
        `is`(dummyRecipes[0])) // Check we are back to the first recipe
  }
}
