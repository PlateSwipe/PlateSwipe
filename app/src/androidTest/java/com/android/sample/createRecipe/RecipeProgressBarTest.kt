package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.createRecipe.RecipeProgressBar
import com.android.sample.ui.theme.SampleAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeProgressBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testInitialProgressBarState() {
    // Set up the composable with currentStep = 0 (initial state)
    composeTestRule.setContent { SampleAppTheme { RecipeProgressBar(currentStep = 0) } }

    // Verify that all icons are displayed
    for (index in 0 until 4) {
      composeTestRule.onNodeWithTag("step_$index").assertExists().assertIsDisplayed()

      if (index < 3) {
        composeTestRule.onNodeWithTag("line_$index").assertExists().assertIsDisplayed()
      }
    }
  }

  @Test
  fun testProgressAtStepTwo() {
    // Set up the composable with currentStep = 2
    composeTestRule.setContent { SampleAppTheme { RecipeProgressBar(currentStep = 2) } }

    // Verify that the first two icons are displayed
    for (index in 0 until 2) {
      composeTestRule.onNodeWithTag("step_$index").assertExists().assertIsDisplayed()
    }

    // Verify that the third and fourth icons are displayed
    composeTestRule.onNodeWithTag("step_2").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("step_3").assertExists().assertIsDisplayed()
  }

  @Test
  fun testProgressAtFinalStep() {
    // Set up the composable with currentStep = 4 (all steps completed)
    composeTestRule.setContent { SampleAppTheme { RecipeProgressBar(currentStep = 4) } }

    // Verify that all steps are displayed
    for (index in 0 until 4) {
      composeTestRule.onNodeWithTag("step_$index").assertExists().assertIsDisplayed()
    }

    // Verify that all lines are displayed
    for (index in 0 until 3) {
      composeTestRule.onNodeWithTag("line_$index").assertExists().assertIsDisplayed()
    }
  }
}
