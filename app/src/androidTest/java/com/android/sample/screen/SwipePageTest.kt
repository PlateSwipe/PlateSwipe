package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.swipePage.SwipePage
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class SwipePageTest : TestCase() {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

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

  @get:Rule val composeTestRule = createComposeRule()

  /** This method runs before the test execution. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() = runTest {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepository = mock(RecipeRepository::class.java)

    // Setup the mock to trigger onSuccess
    `when`(mockRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    recipesViewModel = RecipesViewModel(mockRepository)
    advanceUntilIdle()

    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}
    composeTestRule.setContent {
      SwipePage(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    Intents.init()
  }

  /** This method runs after the test execution. */
  @After fun tearDown() = runTest { Intents.release() }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun swipePageCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
  }

  /**
   * This test checks if the RecipeDisplay composable displays the recipe information correctly :
   * the recipe image and the recipe description.
   */
  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() = runTest {
    composeTestRule.onNodeWithTag("recipeName", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeStar", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeRate", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag("recipeImage1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeImage2", useUnmergedTree = true).assertIsDisplayed()
  }

  /**  */
  @Test
  fun navigateToRecipeInfoWhenDescriptionIsPushed() = runTest {
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).performClick()
    verify(mockNavigationActions).navigateTo(Screen.OVERVIEW_RECIPE)
  }

  /** This test checks if nextRecipe called update the current correctly */
  @Test
  fun testNextRecipeWithoutJobAndWaitForIdle() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value

    // Simulate a drag event
    recipesViewModel.nextRecipe()
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks the Dislike swipe of the image. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventLeft() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeLeft(0f, -10000f) }

    advanceUntilIdle()

    composeTestRule.waitForIdle()
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks the Like swipe of the image. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventRightWithoutJob() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight(0f, 10000f) }

    advanceUntilIdle()

    composeTestRule.waitForIdle()

    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks when the swipe is not enough. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingNotEnough() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput {
      swipeRight(0f, 1f)
      swipeLeft(0f, -1f)
    }

    advanceUntilIdle()

    composeTestRule.waitForIdle()

    assertEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }
}
