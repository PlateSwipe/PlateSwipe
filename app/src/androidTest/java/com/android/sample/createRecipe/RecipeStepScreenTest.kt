package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.createRecipe.RecipeStepScreen
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class RecipeStepScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions = mock(NavigationActions::class.java)
  private val titleText = "Recipe Title"
  private val subtitleText = "This is a subtitle for the recipe."
  private val buttonText = "Next Step"

  @Test
  fun testRecipeStepScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = {},
          navigationActions = mockNavigationActions,
          currentStep = 1)
    }

    composeTestRule.onNodeWithText(titleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(subtitleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(buttonText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }

  @Test
  fun testBackButtonFunctionality() {
    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = {},
          navigationActions = mockNavigationActions,
          currentStep = 1)
    }

    composeTestRule.onNodeWithTag("backArrowIcon").assertExists().performClick()
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun testOnButtonClickFunctionality() {
    val onButtonClick = mock<(Unit) -> Unit>()

    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = { onButtonClick(Unit) },
          navigationActions = mockNavigationActions,
          currentStep = 1)
    }

    composeTestRule.onNodeWithText(buttonText).assertExists().performClick()
    verify(onButtonClick).invoke(Unit)
  }

  @Test
  fun testChefImageIsDisplayedWithCorrectOffset() {
    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = {},
          navigationActions = mockNavigationActions,
          currentStep = 1)
    }

    composeTestRule.onNodeWithContentDescription("Chef standing").assertExists().assertIsDisplayed()
  }

  @Test
  fun testRecipeStepScreenComponentsAreDisplayedInEditMode() {
    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = {},
          navigationActions = mockNavigationActions,
          currentStep = 1,
          isEditing = true)
    }

    composeTestRule.onNodeWithText(titleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(subtitleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(buttonText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }

  @Test
  fun testBackButtonFunctionalityInEditMode() {
    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = {},
          navigationActions = mockNavigationActions,
          currentStep = 1,
          isEditing = true)
    }

    composeTestRule.onNodeWithTag("backArrowIcon").assertExists().performClick()
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun testOnButtonClickFunctionalityInEditMode() {
    val onButtonClick = mock<(Unit) -> Unit>()

    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = { onButtonClick(Unit) },
          navigationActions = mockNavigationActions,
          currentStep = 1,
          isEditing = true)
    }

    composeTestRule.onNodeWithText(buttonText).assertExists().performClick()
    verify(onButtonClick).invoke(Unit)
  }

  @Test
  fun testChefImageIsDisplayedWithCorrectOffsetInEditMode() {
    composeTestRule.setContent {
      RecipeStepScreen(
          title = titleText,
          subtitle = subtitleText,
          buttonText = buttonText,
          onButtonClick = {},
          navigationActions = mockNavigationActions,
          currentStep = 1)
    }

    composeTestRule.onNodeWithContentDescription("Chef standing").assertExists().assertIsDisplayed()
  }
}
