package com.android.sample.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.swipePage.ReceipeDisplay
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
class MainPageTest : TestCase() {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    Intents.init()
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun swipePageCorrectlyDisplayed() {
    composeTestRule.setContent {
      SwipePage(navigationActions) // Set up the SignInScreen directly
    }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
  }

  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() {
    composeTestRule.setContent { ReceipeDisplay(PaddingValues(0.dp)) }
    composeTestRule.onNodeWithTag("recipeDescription").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("swipeUIDescription").assertIsDisplayed()
  }

  @Test
  fun testBiggerDescription() {
    composeTestRule.setContent { ReceipeDisplay(PaddingValues(0.dp)) }

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("displayDescription").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("displayDescription").performClick()
    composeTestRule.onNodeWithTag("RecipeEntireDescription").assertIsDisplayed()
  }

  @Test
  fun testDraggingEvent() {
    composeTestRule.setContent { ReceipeDisplay(PaddingValues(0.dp)) }

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput {
      swipeLeft(durationMillis = 5000)
    }
  }
}
