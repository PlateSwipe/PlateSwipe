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
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  /** This method runs before the test execution. */
  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepository = mock(RecipeRepository::class.java)
    recipesViewModel = RecipesViewModel(mockRepository)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}
    `when`(mockRepository.random(eq(1), anyOrNull(), anyOrNull())).then {}
    composeTestRule.setContent {
      SwipePage(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
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

    composeTestRule.onNodeWithTag("recipeName", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("recipeStar", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("recipeRate", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).performClick()

    composeTestRule.onNodeWithTag("recipeImage", useUnmergedTree = true).assertExists()
  }

  /** This test checks the Dislike swipe of the image. */
  @Test
  fun testDraggingEventLeft() {
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem", useUnmergedTree = true).assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem", useUnmergedTree = true).performTouchInput {
      swipeLeft(0f, -500f)
    }
    composeTestRule.waitForIdle()

    // verify(recipesViewModel).nextRecipe()

  }

  /** This test checks the Like swipe of the image. */
  @Test
  fun testDraggingEventRight() {

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight() }
    composeTestRule.waitForIdle()

    // verify(recipesViewModel).nextRecipe()
  }
}
