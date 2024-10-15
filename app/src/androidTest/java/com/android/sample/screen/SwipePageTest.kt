package com.android.sample.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.swipePage.RecipeDisplay
import com.android.sample.ui.swipePage.SwipePage
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class SwipePageTest : TestCase() {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  /** This method runs before the test execution. */
  @Before
  fun setUp() {
    Intents.init()
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)
  }

  /** This method runs after the test execution. */
  @After
  fun tearDown() {
    Intents.release()
  }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun swipePageCorrectlyDisplayed() {
    composeTestRule.setContent {
      SwipePage(navigationActions) // Set up the SignInScreen directly
    }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
  }

  /**
   * This test checks if the RecipeDisplay composable displays the recipe information correctly :
   * the recipe image, the recipe description and the swipe UI description.
   */
  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() {
    composeTestRule.setContent { RecipeDisplay(PaddingValues(0.dp)) }
    composeTestRule.onNodeWithTag("recipeDescription").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("swipeUIDescription").assertIsDisplayed()
  }

  /**
   * This test checks if the RecipeDisplay composable displays the recipe expanded information
   * correctly.
   */
  @Test
  fun testDescriptionExpandCorrectly() {
    composeTestRule.setContent { RecipeDisplay(PaddingValues(0.dp)) }

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("displayDescription").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("displayDescription").performClick()
    composeTestRule.onNodeWithTag("RecipeEntireDescription").assertIsDisplayed()
  }

  /** This test checks the Dislike swipe of the image. */
  @Test
  fun testDraggingEventLeft() {
    composeTestRule.setContent { RecipeDisplay(PaddingValues(0.dp)) }

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeLeft() }
    composeTestRule.waitForIdle()
  }

  /** This test checks the Like swipe of the image. */
  @Test
  fun testDraggingEventRight() {
    composeTestRule.setContent { RecipeDisplay(PaddingValues(0.dp)) }

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight() }
    composeTestRule.waitForIdle()
  }
}
