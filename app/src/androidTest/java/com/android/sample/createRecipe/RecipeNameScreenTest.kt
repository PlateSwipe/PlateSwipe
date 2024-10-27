package com.android.sample.ui.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class RecipeNameScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions = mock(NavigationActions::class.java)

  @Test
  fun testRecipeNameScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      RecipeNameScreen(navigationActions = mockNavigationActions, currentStep = 0)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("RecipeTitle").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("RecipeSubtitle").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("recipeNameTextField").assertExists().assertIsDisplayed()

    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().assertIsDisplayed()
  }

  @Test
  fun testErrorDisplayedWhenRecipeNameIsEmpty() {
    composeTestRule.setContent {
      RecipeNameScreen(navigationActions = mockNavigationActions, currentStep = 0)
    }

    composeTestRule.onNodeWithText("Next Step").performClick()
    composeTestRule.onNodeWithText("Please enter a recipe name").assertExists().assertIsDisplayed()
  }

  @Test
  fun testNoErrorDisplayedWhenRecipeNameIsEntered() {
    composeTestRule.setContent {
      RecipeNameScreen(navigationActions = mockNavigationActions, currentStep = 0)
    }

    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("Chocolate Cake")
    composeTestRule.onNodeWithText("Next Step").performClick()
    composeTestRule.onNodeWithText("Please enter a recipe name").assertDoesNotExist()
  }

  @Test
  fun testNextStepButtonNavigatesToIngredientsScreenWhenRecipeNameIsEntered() {
    composeTestRule.setContent {
      RecipeNameScreen(navigationActions = mockNavigationActions, currentStep = 0)
    }

    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("Chocolate Cake")
    composeTestRule.onNodeWithText("Next Step").performClick()
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_INGREDIENTS)
  }
}
