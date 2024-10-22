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
import com.android.sample.model.recipe.RecipeRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.swipePage.SwipePage
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull

@RunWith(AndroidJUnit4::class)
class SwipePageTest : TestCase() {
  private lateinit var navigationActions: NavigationActions
  private lateinit var repository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  /** This method runs before the test execution. */
  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    repository = mock(RecipeRepository::class.java)
    recipesViewModel = RecipesViewModel(repository)
    `when`(navigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(navigationActions.navigateTo(Route.AUTH)).then {}
    `when`(repository.random(eq(1), anyOrNull(), anyOrNull())).then {}
    composeTestRule.setContent {
      SwipePage(navigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    Intents.init()
  }

  /** This method runs after the test execution. */
  @After
  fun tearDown() {
    Intents.release()
  }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun swipePageCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
  }

  /**
   * This test checks if the RecipeDisplay composable displays the recipe information correctly :
   * the recipe image and the recipe description.
   */
  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() {

    composeTestRule.onNodeWithTag("recipeName", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeStar", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeRate", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).performClick()

    composeTestRule.onNodeWithTag("recipeImage", useUnmergedTree = true).assertIsDisplayed()
  }

  /** This test checks the Dislike swipe of the image. */
  @Test
  fun testDraggingEventLeft() {

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeLeft() }
    composeTestRule.waitForIdle()
  }

  /** This test checks the Like swipe of the image. */
  @Test
  fun testDraggingEventRight() {

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight() }
    composeTestRule.waitForIdle()
  }
}
