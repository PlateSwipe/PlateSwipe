package com.android.sample.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class SignInTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.navigateTo(Screen.SWIPE)).then {}
    Intents.init()
    composeTestRule.setContent {
      SignInScreen(navigationActions) // Set up the SignInScreen directly
    }
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("loginTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome")

    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.waitForIdle()

    // Assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }

  @Test
  fun googleSignInButtonIsEnabledWhenConditionsMet() {
    composeTestRule.onNodeWithTag("loginButton").assertIsEnabled()
  }

  @Test
  fun logoImageIsDisplayed() {
    composeTestRule.onNodeWithTag("logoImage").assertIsDisplayed()
  }
}
