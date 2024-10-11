package com.android.sample.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.mainPage.MainPage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
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
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.MAIN)

    composeTestRule.setContent {
      MainPage(navigationActions) // Set up the SignInScreen directly
    }
  }

  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("recipeDescription").assertIsDisplayed()

    composeTestRule.onNodeWithTag("recipeImage").assertIsDisplayed()

    composeTestRule.onNodeWithTag("swipeUIDescription").assertIsDisplayed()
  }

  @Test
  fun testBiggerDescription() {
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("displayDescription").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("displayDescription").performClick()
    composeTestRule.onNodeWithTag("RecipeEntireDescription").assertIsDisplayed()
  }

  /* doesn't work properly*/
  @Test
  fun testDraggingEvent() {
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput {
      swipeLeft(durationMillis = 5000)
    }


  }

}
