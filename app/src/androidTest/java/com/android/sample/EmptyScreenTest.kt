package com.android.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class EmptyScreenTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH)
  }

  @Test
  fun mainTextIsDisplayed() {
    composeTestRule.setContent { EmptyScreen(navigationActions = navigationActions, "Test") }
    composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    composeTestRule.onNodeWithText("Work in progress... Stay tuned!").assertIsDisplayed()
  }
}
