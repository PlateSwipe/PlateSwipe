package com.android.sample.model.recipe

import com.android.sample.model.filter.Difficulty
import com.android.sample.ui.utils.testRecipes
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
import org.junit.Assert.assertEquals
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
  private lateinit var mockRecipeRepository: RecipesRepository
  private lateinit var recipesViewModel: RecipesViewModel

  // Dummy recipes for testing
  private val dummyRecipes: List<Recipe> = testRecipes

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    // Set the main dispatcher for tests
    val testDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(testDispatcher)

    // Mock the RecipeRepository
    mockRecipeRepository = mock(RecipesRepository::class.java)
    // Initialize the RecipesViewModel with the mocked repository
    recipesViewModel = RecipesViewModel(mockRecipeRepository)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    // Reset the main dispatcher after tests
    Dispatchers.resetMain()
  }

  @Test
  fun initialStateIsCorrect() {
    // Assert initial state is correct
    assertThat(recipesViewModel.recipes.value, `is`(emptyList()))
    assertThat(recipesViewModel.loading.value, `is`(false))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun initFetchesInitialRecipeAndUpdatesCurrentRecipe() = runTest {
    // Arrange: Mock the repository to return dummy recipes

    // Setup the mock to trigger onSuccess
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(dummyRecipes)
      null
    }
    // Act: Initialize the ViewModel
    recipesViewModel = RecipesViewModel(mockRecipeRepository)

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
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(dummyRecipes)
      null
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
    verify(mockRecipeRepository)
        .random(
            eq(numberOfRecipes),
            any(),
            any()) // Verify that the repository's random method is called
  }

  @Test
  fun fetchRandomRecipesHandlesFailure() {
    // Simulate the failure of the repository
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument<(Throwable) -> Unit>(2)
      onFailure(Exception("Network error")) // Simulate a failure
      null
    }

    // Act
    recipesViewModel.fetchRandomRecipes(2)

    // Assert
    assertThat(recipesViewModel.loading.value, `is`(false)) // Check loading is false after fetch
    assertThat(
        recipesViewModel.recipes.value, `is`(emptyList())) // Ensure no recipes are set on failure
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
    val randomRecipes = dummyRecipes.take(2)

    // Arrange: Mock the repository to return dummy recipes
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(randomRecipes)
      null
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
            randomRecipes +
                randomRecipes)) // Check if the ViewModel's recipes are the combination of the
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
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(dummyRecipes)
      null
    }

    // Act: Initialize the ViewModel and fetch recipes
    recipesViewModel = RecipesViewModel(mockRecipeRepository)
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
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(dummyRecipes)
      null
    }

    // Act: Initialize the ViewModel and fetch recipes
    recipesViewModel = RecipesViewModel(mockRecipeRepository)
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
                uid = "3",
                name = "Beef Stroganoff",
                category = "Non-Vegetarian",
                origin = "Russian",
                instructions = "Instructions here...",
                strMealThumbUrl = "https://www.example.com/beef-stroganoff/",
                ingredientsAndMeasurements =
                    listOf(Pair("Beef", "1 pound"), Pair("Sour cream", "1 cup")))

    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(extendedDummyRecipes)
    }

    // Act: Initialize the ViewModel and fetch recipes
    recipesViewModel = RecipesViewModel(mockRecipeRepository)
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
    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(dummyRecipes.take(2))
      null
    }

    // Spy on the RecipesViewModel
    val spyViewModel = spy(RecipesViewModel(mockRecipeRepository))

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
                    uid = "3",
                    name = "Beef Stroganoff",
                    category = "Non-Vegetarian",
                    origin = "Russian",
                    instructions = "Instructions here...",
                    strMealThumbUrl = "https://www.example.com/beef-stroganoff/",
                    ingredientsAndMeasurements =
                        listOf(Pair("Beef", "1 pound"), Pair("Sour cream", "1 cup"))),
                Recipe(
                    uid = "4",
                    name = "Chicken Curry",
                    category = "Non-Vegetarian",
                    origin = "Indian",
                    instructions = "Instructions here...",
                    strMealThumbUrl = "https://www.example.com/chicken-curry/",
                    ingredientsAndMeasurements =
                        listOf(Pair("Chicken", "1 kg"), Pair("Curry powder", "2 tbsp"))))

    `when`(mockRecipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(dummyRecipes)
      null
    }

    // Spy on the RecipesViewModel
    val spyViewModel = spy(RecipesViewModel(mockRecipeRepository))

    // Set the first recipe as the current recipe
    spyViewModel.updateCurrentRecipe(extendedDummyRecipes[0])
    advanceUntilIdle()

    // Act: Call nextRecipe
    spyViewModel.nextRecipe()
    advanceUntilIdle()

    // Assert: Verify that fetchRandomRecipes is called only once (during init)
    verify(spyViewModel, times(1)).fetchRandomRecipes(any())
  }

  @Test
  fun searchFunctionCallsRepositoryAndHandlesSuccess() = runTest {
    // Arrange: Mock the repository to return a dummy recipe
    val dummyRecipe = dummyRecipes[0]
    `when`(mockRecipeRepository.search(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Recipe) -> Unit>(1)
      onSuccess(dummyRecipe)
      null
    }

    // Act: Call the search method
    var result: Recipe? = null
    recipesViewModel.search("1", onSuccess = { recipe -> result = recipe }, onFailure = {})

    // Assert: Verify that the onSuccess callback is invoked with the correct recipe
    assertNotNull(result)
    assertThat(result, `is`(dummyRecipe))
  }

  @Test
  fun searchFunctionCallsRepositoryAndHandlesFailure() = runTest {
    // Arrange: Mock the repository to simulate a failure
    val exception = Exception("Network error")
    `when`(mockRecipeRepository.search(any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument<(Throwable) -> Unit>(2)
      onFailure(exception)
      null
    }

    // Act: Call the search method
    var error: Exception? = null
    recipesViewModel.search("1", onSuccess = {}, onFailure = { e -> error = e })

    // Assert: Verify that the onFailure callback is invoked with the correct exception
    assertNotNull(error)
    assertThat(error?.message, `is`("Network error"))
  }

  /** Tests for the filter difficulty functionality. */
  @Test
  fun `test updateDifficulty updates the difficulty correctly`() {
    val newDifficulty = Difficulty.Medium
    recipesViewModel.updateDifficulty(newDifficulty)

    assertEquals(newDifficulty, recipesViewModel.tmpFilter.value.difficulty)
  }

  /** Tests for the filter price range functionality. */
  @Test
  fun `test updatePriceRange updates the price range correctly`() {
    val newMin = 10f
    val newMax = 50f
    recipesViewModel.updatePriceRange(newMin, newMax)

    assertEquals(newMin, recipesViewModel.tmpFilter.value.priceRange.min, 0.001f)
    assertEquals(newMax, recipesViewModel.tmpFilter.value.priceRange.max, 0.001f)
  }

  /** Tests for the filter time range functionality. */
  @Test
  fun `test updateTimeRange updates the time range correctly`() {
    val newMin = 1f
    val newMax = 5f
    recipesViewModel.updateTimeRange(newMin, newMax)

    assertEquals(newMin, recipesViewModel.tmpFilter.value.timeRange.min, 0.001f)
    assertEquals(newMax, recipesViewModel.tmpFilter.value.timeRange.max, 0.001f)
  }

  /** Tests for the filter category functionality. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test updateCategory updates the category correctly`() = runTest {
    val newCategory = "Dessert"
    recipesViewModel.updateCategory(newCategory)
    advanceUntilIdle()
    assertEquals(newCategory, recipesViewModel.tmpFilter.value.category)
  }

  @Test
  fun `test init filter updates filter correctly`() {
    recipesViewModel.initFilter()
    assertEquals(
        recipesViewModel.filter.value.difficulty, recipesViewModel.tmpFilter.value.difficulty)
    assertEquals(recipesViewModel.filter.value.category, recipesViewModel.tmpFilter.value.category)
    assertEquals(
        recipesViewModel.filter.value.priceRange.min,
        recipesViewModel.tmpFilter.value.priceRange.min,
        0.001f)
    assertEquals(
        recipesViewModel.filter.value.priceRange.max,
        recipesViewModel.tmpFilter.value.priceRange.max,
        0.001f)
    assertEquals(
        recipesViewModel.filter.value.timeRange.min,
        recipesViewModel.tmpFilter.value.timeRange.min,
        0.001f)
    assertEquals(
        recipesViewModel.filter.value.timeRange.max,
        recipesViewModel.tmpFilter.value.timeRange.max,
        0.001f)
  }
}
