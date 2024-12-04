package com.android.sample.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.resources.C.TestTag.SwipePage.CATEGORY_CHIP
import com.android.sample.resources.C.TestTag.SwipePage.DELETE_SUFFIX
import com.android.sample.resources.C.TestTag.SwipePage.DIFFICULTY_CHIP
import com.android.sample.resources.C.TestTag.SwipePage.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.SwipePage.FILTER
import com.android.sample.resources.C.TestTag.SwipePage.FILTER_ROW
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_2
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_NAME
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_RATE
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_STAR
import com.android.sample.resources.C.TestTag.SwipePage.TIME_RANGE_CHIP
import com.android.sample.resources.C.TestTag.SwipePage.VIEW_RECIPE_BUTTON
import com.android.sample.resources.C.TestTag.Utils.BOTTOM_BAR
import com.android.sample.resources.C.TestTag.Utils.TOP_BAR
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.swipePage.SwipePage
import com.android.sample.ui.utils.testRecipes
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class SwipePageTest : TestCase() {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipesRepository
  private lateinit var recipesViewModel: RecipesViewModel

  private val mockedRecipesList = testRecipes

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

    `when`(mockRepository.filterSearch(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    recipesViewModel = RecipesViewModel(mockRepository)
    advanceUntilIdle()

    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}

    // Init the filter
    recipesViewModel.updateTimeRange(0f, 100f)
    recipesViewModel.updateTimeRange(5f, 99f)
    recipesViewModel.updateDifficulty(Difficulty.Easy)
    recipesViewModel.updateCategory("Dessert")
    recipesViewModel.applyChanges()

    composeTestRule.setContent {
      SwipePage(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
  }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun swipePageCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag(BOTTOM_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TOP_BAR).assertIsDisplayed()
  }

  /**
   * This test checks if the RecipeDisplay composable displays the recipe information correctly :
   * the recipe image and the recipe description.
   */
  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag(RECIPE_NAME, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_STAR, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_RATE, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(RECIPE_IMAGE_1, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_IMAGE_2, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun shawRecipeButtonIsDisplayed() = runTest {
    composeTestRule.onNodeWithTag(VIEW_RECIPE_BUTTON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(VIEW_RECIPE_BUTTON, useUnmergedTree = true).performClick()
    verify(mockNavigationActions).navigateTo(Screen.OVERVIEW_RECIPE)
  }

  /** This test checks if nextRecipe called update the current correctly */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testNextRecipeWithoutJobAndWaitForIdle() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value

    // Simulate a drag event
    recipesViewModel.nextRecipe()
    advanceUntilIdle()
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventLeftSpamming() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value

    // Ensure the draggable item is displayed
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).assertIsDisplayed()

    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput {
      down(center) // Start a touch gesture at the center
      moveBy(Offset(-1000f, 0f))
      up()

      down(center)
      moveBy(Offset(-1000f, 0f))
      up()
    }

    advanceUntilIdle()
    composeTestRule.waitForIdle()

    // Wait until the recipe changes or timeout occurs
    composeTestRule.waitUntil(5000) { recipesViewModel.currentRecipe.value != currentRecipe }

    // Assert that the recipe has changed after the swipes
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventRightSpamming() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value

    // Ensure the draggable item is displayed
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).assertIsDisplayed()

    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput {
      down(center)
      moveBy(Offset(1000f, 0f))
      up()

      down(center)
      moveBy(Offset(1000f, 0f))
      up()
    }

    advanceUntilIdle()
    composeTestRule.waitForIdle()

    // Wait until the recipe changes or timeout occurs
    composeTestRule.waitUntil(5000) { recipesViewModel.currentRecipe.value != currentRecipe }

    // Assert that the recipe has changed after the swipes
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks the Dislike swipe of the image. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventLeft() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput { swipeLeft(0f, -10000f) }

    advanceUntilIdle()

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(5000) { recipesViewModel.currentRecipe.value != currentRecipe }
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks the Like swipe of the image. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventRightWithoutJob() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput { swipeRight(0f, 10000f) }

    advanceUntilIdle()

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(5000) { recipesViewModel.currentRecipe.value != currentRecipe }

    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks when the swipe is not enough. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingNotEnough() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput {
      swipeRight(0f, 1f)
      swipeLeft(0f, -1f)
    }

    advanceUntilIdle()

    composeTestRule.waitForIdle()

    assertEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testOnlyTouchTheScreen() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput {
      down(center) // Start a touch gesture at the center
      moveBy(Offset(50f, 0f)) // Drag horizontally by 50 pixels
      cancel() // Cancel the drag
    }
    advanceUntilIdle()

    composeTestRule.waitForIdle()

    assertEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  @Test
  fun timeRangeChipDisplaysAndHidesCorrectly() {

    // Check if time range chip exists in the hierarchy
    composeTestRule.onNodeWithTag(TIME_RANGE_CHIP, useUnmergedTree = true).assertExists()

    // Scroll to bring the time range chip into view if needed
    composeTestRule
        .onNodeWithTag(FILTER_ROW, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(TIME_RANGE_CHIP))

    // Verify the chip is visible after scrolling
    composeTestRule.onNodeWithTag(TIME_RANGE_CHIP, useUnmergedTree = true).assertIsDisplayed()

    // Click the close icon to hide the chip
    composeTestRule
        .onNodeWithTag("timeRangeChip$DELETE_SUFFIX", useUnmergedTree = true)
        .performClick()

    // Confirm that the chip is no longer visible in the hierarchy
    composeTestRule.onNodeWithTag(TIME_RANGE_CHIP, useUnmergedTree = true).assertDoesNotExist()

    composeTestRule.waitUntil(5000) {
      recipesViewModel.filter.value.timeRange.min == recipesViewModel.filter.value.timeRange.minBorn
    }

    // Verify the ViewModel time range has been reset
    assertEquals(
        recipesViewModel.filter.value.timeRange.min,
        recipesViewModel.filter.value.timeRange.minBorn)
    assertEquals(
        recipesViewModel.filter.value.timeRange.max,
        recipesViewModel.filter.value.timeRange.maxBorn)
  }

  @Test
  fun difficultyChipDisplaysAndHidesCorrectly() {
    // Check if difficulty chip exists in the hierarchy
    composeTestRule.onNodeWithTag(DIFFICULTY_CHIP, useUnmergedTree = true).assertExists()

    // Scroll to bring the difficulty chip into view if needed
    composeTestRule
        .onNodeWithTag(FILTER_ROW, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(DIFFICULTY_CHIP))

    // Verify the chip is visible after scrolling
    composeTestRule.onNodeWithTag(DIFFICULTY_CHIP, useUnmergedTree = true).assertIsDisplayed()

    // Click the close icon to hide the chip
    composeTestRule
        .onNodeWithTag("difficultyChip$DELETE_SUFFIX", useUnmergedTree = true)
        .performClick()

    // Confirm that the chip is no longer visible in the hierarchy
    composeTestRule.onNodeWithTag(DIFFICULTY_CHIP, useUnmergedTree = true).assertDoesNotExist()

    // Verify the ViewModel difficulty has been reset
    composeTestRule.waitUntil(5000) {
      recipesViewModel.filter.value.difficulty == Difficulty.Undefined
    }
    assertEquals(recipesViewModel.filter.value.difficulty, Difficulty.Undefined)
  }

  @Test
  fun categoryChipDisplaysAndHidesCorrectly() {
    // Check if category chip exists in the hierarchy
    composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true).assertExists()

    // Scroll to bring the category chip into view if needed
    composeTestRule
        .onNodeWithTag(FILTER_ROW, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(CATEGORY_CHIP))

    // Verify the chip is visible after scrolling
    composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true).assertIsDisplayed()

    // Click the close icon to hide the chip
    composeTestRule
        .onNodeWithTag("categoryChip$DELETE_SUFFIX", useUnmergedTree = true)
        .performClick()

    // Confirm that the chip is no longer visible in the hierarchy
    composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true).assertDoesNotExist()

    composeTestRule.waitUntil(5000) { recipesViewModel.filter.value.category == null }

    // Verify the ViewModel category has been reset
    assertNull(recipesViewModel.filter.value.category)
  }

  @Test
  fun filterIconClickNavigatesToFilterPage() {
    composeTestRule.onNodeWithTag(FILTER).performClick()
    verify(mockNavigationActions).navigateTo(Screen.FILTER)
  }
}
