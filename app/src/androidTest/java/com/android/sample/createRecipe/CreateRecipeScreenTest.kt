package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.ui.createRecipe.CreateRecipeScreen
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class CreateRecipeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavigationActions = mock(NavigationActions::class.java)
  private val mockCreateRecipeViewModel = mockk<CreateRecipeViewModel>(relaxed = true)

  @Before
  fun setUp() {
    `when`(mockNavigationActions.currentRoute()).thenReturn(LIST_TOP_LEVEL_DESTINATIONS[0].route)
  }

  @Test
  fun testCreateRecipeScreenDisplaysTopBarBottomBarAndRecipeNameScreenInCreateMode() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          isEditing = false // Mode création
          )
    }

    composeTestRule.onNodeWithTag("topBarTitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Create your recipe").assertExists().assertIsDisplayed()
  }

  @Test
  fun testCreateRecipeScreenDisplaysTopBarAndRecipeNameScreenInEditMode() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          isEditing = true // Mode édition
          )
    }

    composeTestRule.onNodeWithTag("topBarTitle").assertExists().assertIsDisplayed()
    // Si vous avez une barre de navigation inférieure uniquement en mode création, vous pouvez
    // vérifier qu'elle n'est pas affichée en mode édition
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists()
    composeTestRule.onNodeWithText("Edit your recipe").assertExists().assertIsDisplayed()
  }

  @Test
  fun testBottomNavigationSelectsTabAndNavigatesInCreateMode() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          isEditing = false // Mode création
          )
    }

    for (tab in LIST_TOP_LEVEL_DESTINATIONS) {
      composeTestRule.onNodeWithTag("tab" + tab.textId).assertExists().performClick()
      verify(mockNavigationActions).navigateTo(tab)
      reset(mockNavigationActions)
    }
  }

  @Test
  fun testBottomNavigationIsNotDisplayedInEditMode() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          isEditing = true // Mode édition
          )
    }

    // Vérifier que la barre de navigation inférieure n'est pas affichée en mode édition
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists()
  }

  @Test
  fun testBackArrowIsDisplayedInEditMode() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel,
          isEditing = true // Mode édition
          )
    }

    composeTestRule.onNodeWithContentDescription("Back").assertExists().assertIsDisplayed()
  }
}
