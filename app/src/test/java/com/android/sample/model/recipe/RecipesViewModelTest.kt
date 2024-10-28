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
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun nextRecipeStateFlowUpdatesCorrectly() = runTest {
    // Arrange: Mock the repository to return dummy recipes
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Act: Initialize the ViewModel and fetch recipes
    recipesViewModel = RecipesViewModel(recipeRepository)
    advanceUntilIdle()

    // Assert: Verify that the next recipe is set correctly
    assertNotNull(recipesViewModel.nextRecipe.value) // Ensure next recipe is not null
    assertThat(
        recipesViewModel.nextRecipe.value,
        `is`(dummyRecipes[1])) // Check the next recipe is the second one
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun updateCurrentRecipeUpdatesNextRecipe() = runTest {
    // Arrange: Mock the repository to return dummy recipes
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Act: Initialize the ViewModel and fetch recipes
    recipesViewModel = RecipesViewModel(recipeRepository)
    advanceUntilIdle()

    // Act: Update the current recipe
    recipesViewModel.updateCurrentRecipe(dummyRecipes[0])
    advanceUntilIdle()

    // Assert: Verify that the next recipe is set correctly
    assertNotNull(recipesViewModel.nextRecipe.value) // Ensure next recipe is not null
    assertThat(
        recipesViewModel.nextRecipe.value,
        `is`(dummyRecipes[1])) // Check the next recipe is the second one
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun nextRecipeUpdatesCurrentAndNextRecipe() = runTest {
    // Arrange: Mock the repository to return dummy recipes
    val extendedDummyRecipes =
        dummyRecipes +
            Recipe(
                idMeal = "3",
                strMeal = "Beef Stroganoff",
                strCategory = "Non-Vegetarian",
                strArea = "Russian",
                strInstructions = "Instructions here...",
                strMealThumbUrl = "https://www.example.com/beef-stroganoff/",
                ingredientsAndMeasurements =
                    listOf(Pair("Beef", "1 pound"), Pair("Sour cream", "1 cup")))

    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(extendedDummyRecipes) // Return the extended dummy recipes
    }

    // Act: Initialize the ViewModel and fetch recipes
    recipesViewModel = RecipesViewModel(recipeRepository)
    advanceUntilIdle()

    // Print the list of recipes
    println("Fetched recipes: ${recipesViewModel.recipes.value}")

    // Set the first recipe as the current recipe
    recipesViewModel.updateCurrentRecipe(extendedDummyRecipes[0])
    advanceUntilIdle()

    // Act: Call nextRecipe
    recipesViewModel.nextRecipe()
    advanceUntilIdle()

    // Assert: Verify that the current recipe is updated to the next one
    assertThat(recipesViewModel.currentRecipe.value, `is`(extendedDummyRecipes[1]))
    assertThat(
        recipesViewModel.nextRecipe.value,
        `is`(extendedDummyRecipes[2])) // Next recipe is the third one
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun nextRecipeFetchesNewRecipesWhenThreeLeft() = runTest {
    // Arrange: Mock the repository to return dummy recipes
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Spy on the RecipesViewModel
    val spyViewModel = spy(RecipesViewModel(recipeRepository))

    // Set the first recipe as the current recipe
    spyViewModel.updateCurrentRecipe(dummyRecipes[0])
    advanceUntilIdle()

    // Act: Call nextRecipe until there are three recipes left
    spyViewModel.nextRecipe()
    advanceUntilIdle()
    spyViewModel.nextRecipe()
    advanceUntilIdle()

    // Assert: Verify that fetchRandomRecipes is called twice (once during init and once during the
    // test)
    verify(spyViewModel, times(2)).fetchRandomRecipes(eq(2))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun nextRecipeDoesNotFetchNewRecipesWhenMoreThanThree() = runTest {
    // Arrange: Mock the repository to return extended dummy recipes
    val extendedDummyRecipes =
        dummyRecipes +
            listOf(
                Recipe(
                    idMeal = "3",
                    strMeal = "Beef Stroganoff",
                    strCategory = "Non-Vegetarian",
                    strArea = "Russian",
                    strInstructions = "Instructions here...",
                    strMealThumbUrl = "https://www.example.com/beef-stroganoff/",
                    ingredientsAndMeasurements =
                        listOf(Pair("Beef", "1 pound"), Pair("Sour cream", "1 cup"))),
                Recipe(
                    idMeal = "4",
                    strMeal = "Chicken Curry",
                    strCategory = "Non-Vegetarian",
                    strArea = "Indian",
                    strInstructions = "Instructions here...",
                    strMealThumbUrl = "https://www.example.com/chicken-curry/",
                    ingredientsAndMeasurements =
                        listOf(Pair("Chicken", "1 kg"), Pair("Curry powder", "2 tbsp"))))

    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(extendedDummyRecipes) // Return the extended dummy recipes
    }

    // Spy on the RecipesViewModel
    val spyViewModel = spy(RecipesViewModel(recipeRepository))

    // Set the first recipe as the current recipe
    spyViewModel.updateCurrentRecipe(extendedDummyRecipes[0])
    advanceUntilIdle()

    // Act: Call nextRecipe
    spyViewModel.nextRecipe()
    advanceUntilIdle()

    // Assert: Verify that fetchRandomRecipes is called only once (during init)
    verify(spyViewModel, times(1)).fetchRandomRecipes(any())
  }
}
