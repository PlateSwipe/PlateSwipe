package com.android.sample.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.filter.FilterPage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class FilterPageTest {

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipesRepository
  private lateinit var recipesViewModel: RecipesViewModel
  private val difficultyNames =
      listOf(Difficulty.Easy.toString(), Difficulty.Medium.toString(), Difficulty.Hard.toString())

  private val recipe1 =
      Recipe(
          "Recipe 1",
          "",
          "url1",
          "Instructions 1",
          "Category 1",
          "Area 1",
          listOf(Pair("Ingredient 1", "Ingredient 1")))
  private val recipe2 =
      Recipe(
          "Recipe 2",
          "",
          "url2",
          "Instructions 2",
          "Category 2",
          "Area 2",
          listOf(Pair("Ingredient 2", "Ingredient 2")))
  private val mockedRecipesList = listOf(recipe1, recipe2)

  private val mockedCategoriesList = listOf("Dessert", "Vegetarian")

  @get:Rule val composeTestRule = createComposeRule()

  /** This method runs before the test execution. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() = runTest {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepository = mock(RecipesRepository::class.java)

    // Setup the mock to trigger onSuccess
    `when`(mockRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    `when`(mockRepository.listCategories(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<String>) -> Unit>(0)
      onSuccess(mockedCategoriesList)
      null
    }

    recipesViewModel = RecipesViewModel(mockRepository)
    advanceUntilIdle()

    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}

    recipesViewModel.updateCategory(mockedCategoriesList[0])
    recipesViewModel.updateCategory(mockedCategoriesList[1])
    composeTestRule.setContent {
      FilterPage(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    advanceUntilIdle()
    Intents.init()
  }

  /** This method runs after the test execution. */
  @After fun tearDown() = runTest { Intents.release() }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun filterPageCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
  }

  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true).assertIsDisplayed()

    difficultyNames.forEach { difficulty ->
      composeTestRule.onNodeWithTag("difficultyCheckbox${difficulty}").assertExists()
      composeTestRule.onNodeWithText(difficulty).assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testValueTimeRangeSlider() {
    val min = recipesViewModel.filter.value.timeRange.min

    // Find the RangeSlider node and calculate its bounds
    val sliderNode = composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true)
    val sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.left, sliderBounds.height),
          end = Offset(sliderBounds.right, sliderBounds.height))
    }

    // Assert that the range has changed
    assertNotEquals(recipesViewModel.filter.value.timeRange.min, min)
  }

  @Test
  fun testValuePriceRangeSlider() {
    val max = recipesViewModel.filter.value.priceRange.max

    // Find the RangeSlider node and calculate its bounds
    val sliderNode = composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true)
    val sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.right, sliderBounds.height),
          end = Offset(sliderBounds.left, sliderBounds.height))
    }

    composeTestRule.waitForIdle()
    // Assert that the range has changed
    assertNotEquals(recipesViewModel.filter.value.priceRange.max, max)
  }

  @Test
  fun testNoDifficultySelectedInitially() {
    difficultyNames.forEach { difficulty ->
      composeTestRule.onNodeWithTag("difficultyCheckbox${difficulty}").assertIsOff()
    }
    assertEquals(Difficulty.Undefined, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun testSelectingEasyCheckboxUpdatesStateCorrectly() {
    // Select "Easy" checkbox and verify it is selected
    val easyCheckbox = composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Easy}")
    easyCheckbox.performClick()
    composeTestRule.waitForIdle() // Ensure UI is updated

    easyCheckbox.assertIsOn()
    composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Medium}").assertIsOff()
    composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Hard}").assertIsOff()
  }

  @Test
  fun testSelectingMediumCheckboxUpdatesStateCorrectly() {
    // Select "Medium" checkbox and verify it is selected
    val mediumCheckbox = composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Medium}")
    mediumCheckbox.performClick()
    composeTestRule.waitForIdle()

    mediumCheckbox.assertIsOn()
    composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Easy}").assertIsOff()
    composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Hard}").assertIsOff()
  }

  @Test
  fun testSelectingHardCheckboxUpdatesStateCorrectly() {
    // Select "Hard" checkbox and verify it is selected
    val hardCheckbox = composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Hard}")
    hardCheckbox.performClick()
    composeTestRule.waitForIdle()

    hardCheckbox.assertIsOn()
    composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Easy}").assertIsOff()
    composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Medium}").assertIsOff()
  }

  @Test
  fun testViewModelIsUpdatedWithCorrectDifficulty_whenEasyIsSelected() {
    // Select "Easy" checkbox and verify ViewModel is updated
    val easyCheckbox = composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Easy}")
    easyCheckbox.performClick()
    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Easy
    assertEquals(Difficulty.Easy, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun testViewModelIsUpdatedWithCorrectDifficulty_whenMediumIsSelected() {
    // Select "Medium" checkbox and verify ViewModel is updated
    val mediumCheckbox = composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Medium}")
    mediumCheckbox.performClick()
    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Medium
    assertEquals(Difficulty.Medium, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun testViewModelIsUpdatedWithCorrectDifficulty_whenHardIsSelected() {
    // Select "Hard" checkbox and verify ViewModel is updated
    val hardCheckbox = composeTestRule.onNodeWithTag("difficultyCheckbox${Difficulty.Hard}")
    hardCheckbox.performClick()
    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Hard
    assertEquals(Difficulty.Hard, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun categoriesCheckboxesAreDisplayedCorrectly() {

    // Verify that each category checkbox is displayed
    assertEquals(recipesViewModel.categories.value.size, mockedCategoriesList.size)
    recipesViewModel.categories.value.forEach { category ->
      composeTestRule.onNodeWithTag("categoryCheckbox$category").assertExists()
      composeTestRule.onNodeWithTag("categoryCheckbox$category").assertIsDisplayed()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun selectingCheckboxUpdatesViewModel() = runTest {
    advanceUntilIdle()

    // Select the "Dessert" checkbox
    composeTestRule.onNodeWithTag("categoryCheckboxDessert").performClick()

    advanceUntilIdle()

    // Verify that the ViewModel's category is updated to "Dessert"
    assertEquals("Dessert", recipesViewModel.filter.value.category)

    // Select the "Main Course" checkbox
    composeTestRule.onNodeWithTag("categoryCheckboxVegetarian").performClick()

    advanceUntilIdle()

    // Verify that the ViewModel's category is updated to "Main Course" and "Dessert" is unselected
    assertEquals("Vegetarian", recipesViewModel.filter.value.category)
    composeTestRule.onNodeWithTag("categoryCheckboxDessert").assertIsOff()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun onlyOneCheckboxCanBeSelectedAtATime() = runTest {
    // Select "Dessert" checkbox
    composeTestRule.onNodeWithTag("categoryCheckboxDessert").performClick()

    advanceUntilIdle()

    // Ensure "Dessert" is checked and others are not
    composeTestRule.onNodeWithTag("categoryCheckboxDessert").assertIsOn()
    composeTestRule.onNodeWithTag("categoryCheckboxVegetarian").assertIsOff()

    // Select "Appetizer" checkbox
    composeTestRule.onNodeWithTag("categoryCheckboxVegetarian").performClick()

    advanceUntilIdle()

    // Ensure "Appetizer" is checked and "Dessert" is unchecked
    composeTestRule.onNodeWithTag("categoryCheckboxVegetarian").assertIsOn()
    composeTestRule.onNodeWithTag("categoryCheckboxDessert").assertIsOff()
  }
}
