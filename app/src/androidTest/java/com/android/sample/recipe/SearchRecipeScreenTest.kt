package com.android.sample.recipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.recipe.SearchRecipeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SearchRecipeScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH)
  }

  @Test
  fun mainTextIsDisplayed() {
    composeTestRule.setContent { SearchRecipeScreen(navigationActions = navigationActions) }
    composeTestRule.onNodeWithText("Search Recipe Screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("Work in progress... Stay tuned!").assertIsDisplayed()
  }
}
