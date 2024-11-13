package com.android.sample.ui.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class RecipeIngredientsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions = mock(NavigationActions::class.java)
  private val mockCreateRecipeViewModel = mockk<CreateRecipeViewModel>(relaxed = true)
  private val titleText = "No Ingredients"
  private val subtitleText = "List the ingredients needed for your recipe. Add as many as you need."
  private val buttonText = "Add Ingredients"

  @Test
  fun testRecipeIngredientsScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      RecipeIngredientsScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          currentStep = 1)
    }

    composeTestRule.onNodeWithText(titleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(subtitleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(buttonText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }

  @Test
  fun testAddIngredientsButtonNavigatesToNextScreen() {
    composeTestRule.setContent {
      RecipeIngredientsScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          currentStep = 1)
    }

    composeTestRule.onNodeWithText(buttonText).assertExists().performClick()
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
  }
}
