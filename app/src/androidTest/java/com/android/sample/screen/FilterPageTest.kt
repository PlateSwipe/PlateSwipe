package com.android.sample.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.Filter
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.resources.C.Tag.FilterPage.PRICE_RANGE_MAX
import com.android.sample.resources.C.Tag.FilterPage.PRICE_RANGE_MIN
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MAX
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MIN
import com.android.sample.ui.filter.FilterPage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.utils.testRecipes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
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

  private val mockedRecipesList = testRecipes

  private val mockedCategoriesList = Recipe.getCategories()

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

    `when`(mockRepository.searchByCategory(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
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
  }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun filterPageCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
  }

  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule
        .onNodeWithText("Reset", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Apply", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()

    difficultyNames.forEach { difficulty ->
      composeTestRule
          .onNodeWithTag("difficultyCheckbox${difficulty}", useUnmergedTree = true)
          .assertExists()
      composeTestRule.onNodeWithText(difficulty).assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testValueTimeRangeSlider() {
    val min = recipesViewModel.filter.value.timeRange.min
    composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    // Find the RangeSlider node and calculate its bounds
    val sliderNode = composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true)
    val sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.left, sliderBounds.height),
          end = Offset(sliderBounds.right, sliderBounds.height))
    }
    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    // Assert that the range has changed
    assertNotEquals(recipesViewModel.filter.value.timeRange.min, min)
  }

  @Test
  fun testValuePriceRangeSlider() {
    val max = recipesViewModel.filter.value.priceRange.max
    composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    // Find the RangeSlider node and calculate its bounds
    val sliderNode = composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true)
    val sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.right, sliderBounds.height),
          end = Offset(sliderBounds.left, sliderBounds.height))
    }
    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()
    // Assert that the range has changed
    assertNotEquals(recipesViewModel.filter.value.priceRange.max, max)
  }

  @Test
  fun testNoDifficultySelectedInitially() {
    difficultyNames.forEach { difficulty ->
      composeTestRule
          .onNodeWithTag("difficultyCheckbox${difficulty}", useUnmergedTree = true)
          .performScrollTo()
      composeTestRule.waitForIdle()

      composeTestRule
          .onNodeWithTag("difficultyCheckbox${difficulty}", useUnmergedTree = true)
          .assertIsOff()
    }
    assertEquals(Difficulty.Undefined, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun testSelectingEasyCheckboxUpdatesStateCorrectly() {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Easy}", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Easy" checkbox and verify it is selected
    val easyCheckbox =
        composeTestRule.onNodeWithTag(
            "difficultyCheckbox${Difficulty.Easy}", useUnmergedTree = true)
    easyCheckbox.performClick()
    composeTestRule.waitForIdle() // Ensure UI is updated

    easyCheckbox.assertIsOn()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Medium}", useUnmergedTree = true)
        .assertIsOff()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .assertIsOff()
  }

  @Test
  fun testSelectingMediumCheckboxUpdatesStateCorrectly() {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Medium}", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Medium" checkbox and verify it is selected
    val mediumCheckbox =
        composeTestRule.onNodeWithTag(
            "difficultyCheckbox${Difficulty.Medium}", useUnmergedTree = true)
    mediumCheckbox.performClick()
    composeTestRule.waitForIdle()

    mediumCheckbox.assertIsOn()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Easy}", useUnmergedTree = true)
        .assertIsOff()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .assertIsOff()
  }

  @Test
  fun testSelectingHardCheckboxUpdatesStateCorrectly() {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Hard" checkbox and verify it is selected
    val hardCheckbox =
        composeTestRule.onNodeWithTag(
            "difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
    hardCheckbox.performClick()
    composeTestRule.waitForIdle()

    hardCheckbox.assertIsOn()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Easy}", useUnmergedTree = true)
        .assertIsOff()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Medium}", useUnmergedTree = true)
        .assertIsOff()
  }

  @Test
  fun testViewModelIsUpdatedWithCorrectDifficulty_whenEasyIsSelected() {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Easy}", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Easy" checkbox and verify ViewModel is updated
    val easyCheckbox =
        composeTestRule.onNodeWithTag(
            "difficultyCheckbox${Difficulty.Easy}", useUnmergedTree = true)
    easyCheckbox.performClick()

    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Easy
    assertEquals(Difficulty.Easy, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun testViewModelIsUpdatedWithCorrectDifficulty_whenMediumIsSelected() {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Medium}", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Medium" checkbox and verify ViewModel is updated
    val mediumCheckbox =
        composeTestRule.onNodeWithTag(
            "difficultyCheckbox${Difficulty.Medium}", useUnmergedTree = true)
    mediumCheckbox.performClick()

    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Medium
    assertEquals(Difficulty.Medium, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun testViewModelIsUpdatedWithCorrectDifficulty_whenHardIsSelected() {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Hard" checkbox and verify ViewModel is updated
    val hardCheckbox =
        composeTestRule.onNodeWithTag(
            "difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
    hardCheckbox.performClick()

    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Hard
    assertEquals(Difficulty.Hard, recipesViewModel.filter.value.difficulty)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun categoriesCheckboxesAreDisplayedCorrectly() = runTest {
    advanceUntilIdle()
    // Verify that each category checkbox is displayed
    assertEquals(recipesViewModel.categories.value.size, mockedCategoriesList.size)
    recipesViewModel.categories.value.forEach { category ->
      composeTestRule
          .onNodeWithTag("categoryCheckbox${category}", useUnmergedTree = true)
          .performScrollTo()
      composeTestRule.waitForIdle()

      composeTestRule
          .onNodeWithTag("categoryCheckbox$category", useUnmergedTree = true)
          .assertExists()
      composeTestRule
          .onNodeWithTag("categoryCheckbox$category", useUnmergedTree = true)
          .assertIsDisplayed()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun selectingCheckboxUpdatesViewModel() = runTest {
    composeTestRule
        .onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true)
        .performScrollTo()

    composeTestRule.waitForIdle()

    advanceUntilIdle()

    // Select the "Dessert" checkbox
    composeTestRule.onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    advanceUntilIdle()

    // Verify that the ViewModel's category is updated to "Dessert"
    assertEquals("Dessert", recipesViewModel.filter.value.category)

    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .performScrollTo()

    composeTestRule.waitForIdle()

    // Select the "Main Course" checkbox
    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .performClick()

    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    advanceUntilIdle()

    // Verify that the ViewModel's category is updated to "Main Course" and "Dessert" is unselected
    assertEquals("Vegetarian", recipesViewModel.filter.value.category)
    composeTestRule.onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true).assertIsOff()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun onlyOneCheckboxCanBeSelectedAtATime() = runTest {
    composeTestRule
        .onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()

    // Select "Dessert" checkbox
    composeTestRule.onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true).performClick()

    advanceUntilIdle()

    // Ensure "Dessert" is checked and others are not
    composeTestRule.onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true).assertIsOn()

    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .assertIsOff()

    // Select "Appetizer" checkbox
    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    advanceUntilIdle()

    // Ensure "Appetizer" is checked and "Dessert" is unchecked
    composeTestRule.onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true).assertIsOn()

    composeTestRule
        .onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true)
        .performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("categoryCheckboxDessert", useUnmergedTree = true).assertIsOff()
  }

  @Test
  fun nothingChangedWhenNewTimeRangeNotApplied() = runTest {
    val min = recipesViewModel.filter.value.timeRange.min
    composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    // Find the RangeSlider node and calculate its bounds
    val sliderNode = composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true)
    val sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.left, sliderBounds.height),
          end = Offset(sliderBounds.right, sliderBounds.height))
    }
    composeTestRule.waitForIdle()

    assertEquals(recipesViewModel.filter.value.timeRange.min, min)
  }

  @Test
  fun nothingChangedWhenNewValueRangeNotApplied() = runTest {
    val max = recipesViewModel.filter.value.priceRange.max
    composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

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

    assertEquals(recipesViewModel.filter.value.priceRange.max, max)
  }

  @Test
  fun nothingChangedWhenNewDifficultyNotApplied() = runTest {
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .performScrollTo()
        .performClick()
    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Easy
    assertEquals(Difficulty.Undefined, recipesViewModel.filter.value.difficulty)
  }

  @Test
  fun nothingChangedWhenNewCategoryNotApplied() = runTest {
    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Easy
    assertNull(recipesViewModel.filter.value.category)
  }

  @Test
  fun resetCorrectlyAllValues() = runTest {
    composeTestRule
        .onNodeWithTag("categoryCheckboxVegetarian", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag("difficultyCheckbox${Difficulty.Hard}", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    // Find the RangeSlider node and calculate its bounds
    var sliderNode = composeTestRule.onNodeWithTag("priceRangeSlider", useUnmergedTree = true)
    var sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.right, sliderBounds.height),
          end = Offset(sliderBounds.left, sliderBounds.height))
    }

    composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()

    // Find the RangeSlider node and calculate its bounds
    sliderNode = composeTestRule.onNodeWithTag("timeRangeSlider", useUnmergedTree = true)
    sliderBounds = sliderNode.fetchSemanticsNode().boundsInRoot

    // Swipe from start to end on the RangeSlider's horizontal bounds
    sliderNode.performTouchInput {
      swipe(
          start = Offset(sliderBounds.left, sliderBounds.height),
          end = Offset(sliderBounds.right, sliderBounds.height))
    }
    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Reset", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Apply", useUnmergedTree = true).performScrollTo().performClick()

    composeTestRule.waitForIdle()

    // Verify the ViewModel's updateDifficulty method was called with Difficulty.Easy
    assertEquals(TIME_RANGE_MIN, recipesViewModel.filter.value.timeRange.min)
    assertEquals(TIME_RANGE_MAX, recipesViewModel.filter.value.timeRange.max)
    assertEquals(PRICE_RANGE_MIN, recipesViewModel.filter.value.priceRange.min)
    assertEquals(PRICE_RANGE_MAX, recipesViewModel.filter.value.priceRange.max)
    assertEquals(Filter().category, recipesViewModel.filter.value.category)
    assertEquals(Filter().difficulty, recipesViewModel.filter.value.difficulty)
  }
}
