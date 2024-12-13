package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.createRecipe.RecipeProgressBar
import com.android.sample.ui.theme.PlateSwipeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeProgressBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testInitialProgressBarState() {
    // Set up the composable with currentStep = 0 (initial state)
    composeTestRule.setContent { PlateSwipeTheme { RecipeProgressBar(currentStep = 0) } }

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
    composeTestRule.setContent { PlateSwipeTheme { RecipeProgressBar(currentStep = 2) } }

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
    composeTestRule.setContent { PlateSwipeTheme { RecipeProgressBar(currentStep = 4) } }

    // Verify that all steps are displayed
    for (index in 0 until 4) {
      composeTestRule.onNodeWithTag("step_$index").assertExists().assertIsDisplayed()
    }

    // Verify that all lines are displayed
    for (index in 0 until 3) {
      composeTestRule.onNodeWithTag("line_$index").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testNegativeOutOfBoundsCurrentStep() {
    // Set up the composable with an out-of-bounds currentStep (-1)
    composeTestRule.setContent { PlateSwipeTheme { RecipeProgressBar(currentStep = -1) } }

    // Verify that only the first step is highlighted as the current step
    composeTestRule.onNodeWithTag("step_0").assertExists().assertIsDisplayed()
    for (index in 1 until 4) {
      composeTestRule.onNodeWithTag("step_$index").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testExcessiveOutOfBoundsCurrentStep() {
    // Set up the composable with an out-of-bounds currentStep (greater than the max index)
    composeTestRule.setContent { PlateSwipeTheme { RecipeProgressBar(currentStep = 5) } }

    // Verify that the last step is highlighted as the current step
    for (index in 0 until 4) {
      composeTestRule.onNodeWithTag("step_$index").assertExists().assertIsDisplayed()
    }
  }
}
