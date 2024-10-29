package com.android.sample.ui.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class RecipeInstructionsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions = mock(NavigationActions::class.java)
  private val titleText = "Add Instructions"
  private val subtitleText = "Describe each step of how to prepare your dish."
  private val buttonText = "Add Step"

  @Test
  fun testRecipeInstructionsScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      RecipeInstructionsScreen(navigationActions = mockNavigationActions, currentStep = 2)
    }

    composeTestRule.onNodeWithText(titleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(subtitleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(buttonText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }

  @Test
  fun testAddStepButtonTriggersNavigation() {
    composeTestRule.setContent {
      RecipeInstructionsScreen(navigationActions = mockNavigationActions, currentStep = 2)
    }

    composeTestRule.onNodeWithText(buttonText).assertExists().performClick()
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
  }
}
