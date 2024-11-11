package com.android.sample.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.resources.C.TestTag.SignInScreen.AVOCADO
import com.android.sample.resources.C.TestTag.SignInScreen.BROCCOLI
import com.android.sample.resources.C.TestTag.SignInScreen.COOK_IMAGE
import com.android.sample.resources.C.TestTag.SignInScreen.LOGIN_BUTTON
import com.android.sample.resources.C.TestTag.SignInScreen.LOGIN_TITLE
import com.android.sample.resources.C.TestTag.SignInScreen.PANCAKES
import com.android.sample.resources.C.TestTag.SignInScreen.PASTA
import com.android.sample.resources.C.TestTag.SignInScreen.PEPPER
import com.android.sample.resources.C.TestTag.SignInScreen.PLATE_TEXT
import com.android.sample.resources.C.TestTag.SignInScreen.SALAD
import com.android.sample.resources.C.TestTag.SignInScreen.SUSHI
import com.android.sample.resources.C.TestTag.SignInScreen.SWIPE_TEXT
import com.android.sample.resources.C.TestTag.SignInScreen.TACO
import com.android.sample.resources.C.TestTag.SignInScreen.TOMATO
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class SignInTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockNavigationActions: NavigationActions

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    `when`(mockNavigationActions.navigateTo(Screen.SWIPE)).then {}
    Intents.init()
    composeTestRule.setContent {
      SignInScreen(mockNavigationActions) // Set up the SignInScreen directly
    }
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag(LOGIN_TITLE, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(PLATE_TEXT, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PLATE_TEXT, useUnmergedTree = true).assertTextEquals("Plate")
    composeTestRule.onNodeWithTag(SWIPE_TEXT, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SWIPE_TEXT, useUnmergedTree = true).assertTextEquals("Swipe")

    composeTestRule.onNodeWithTag(LOGIN_BUTTON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(LOGIN_BUTTON, useUnmergedTree = true).assertHasClickAction()
  }

  @Test
  fun imagesDisplayCorrectly() {
    composeTestRule.onNodeWithTag(TACO, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SUSHI, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AVOCADO, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TOMATO, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PANCAKES, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BROCCOLI, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PASTA, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SALAD, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PEPPER, useUnmergedTree = true).assertIsDisplayed()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun googleSignInReturnsInValidResult() = runTest {
    // Perform click on Google Sign-In button
    composeTestRule.onNodeWithTag(LOGIN_BUTTON, useUnmergedTree = true).assertIsEnabled()

    composeTestRule.onNodeWithTag(LOGIN_BUTTON, useUnmergedTree = true).performClick()

    // Wait for idle
    composeTestRule.waitForIdle()

    advanceUntilIdle()

    composeTestRule.onNodeWithTag(SWIPE_TEXT, useUnmergedTree = true).performClick()

    verify(mockNavigationActions, never()).navigateTo(Screen.OVERVIEW_RECIPE)
  }

  @Test
  fun cookImageIsDisplayed() {
    composeTestRule.onNodeWithTag(COOK_IMAGE, useUnmergedTree = true).assertIsDisplayed()
  }
}
