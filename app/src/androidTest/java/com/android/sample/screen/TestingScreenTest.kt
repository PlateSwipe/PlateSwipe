package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.TestingScreen
import com.android.sample.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class TestingScreenTest : TestCase() {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  /** This method runs before the test execution. */
  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    composeTestRule.setContent { TestingScreen(navigationActions = navigationActions) }
  }

  /** This test checks if the page is correctly displayed */
  @Test
  fun pageCorrectlyDisplayed() {

    // Check that the elements are displayed
    composeTestRule.onNodeWithTag("textInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("buttonText", useUnmergedTree = true).assertIsDisplayed()
  }

  /** This test checks if the rows are correctly displayed */
  @Test
  fun rowCorrectlyDisplayed() {

    // Check that the elements are displayed
    composeTestRule.onNodeWithTag("Row1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Row2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Row3").assertIsDisplayed()
  }
}