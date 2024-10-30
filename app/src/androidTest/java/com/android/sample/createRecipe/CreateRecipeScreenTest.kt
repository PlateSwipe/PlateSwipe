package com.android.sample.ui.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.recipe.CreateRecipeViewModel
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
  fun testCreateRecipeScreenDisplaysTopBarBottomBarAndRecipeNameScreen() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel)
    }

    composeTestRule.onNodeWithTag("TopAppBar").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Create your recipe").assertExists().assertIsDisplayed()
  }

  @Test
  fun testBackButtonNavigatesBack() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel)
    }

    composeTestRule.onNodeWithTag("BackButton").assertExists().performClick()
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun testBottomNavigationSelectsTabAndNavigates() {
    composeTestRule.setContent {
      CreateRecipeScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = mockCreateRecipeViewModel)
    }

    for (tab in LIST_TOP_LEVEL_DESTINATIONS) {
      composeTestRule.onNodeWithTag(tab.textId).assertExists().performClick()
      verify(mockNavigationActions).navigateTo(tab)
      reset(mockNavigationActions)
    }
  }
}
