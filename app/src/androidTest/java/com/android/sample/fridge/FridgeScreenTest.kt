package com.android.sample.fridge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FridgeScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.FRIDGE)
  }

  @Test
  fun mainTextIsDisplayed() {
    composeTestRule.setContent { FridgeScreen(navigationActions = navigationActions) }
    composeTestRule.onNodeWithText("Fridge Screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("Work in progress…stay tuned!").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Chef Image").assertIsDisplayed()
  }
}
