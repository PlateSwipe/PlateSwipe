package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.theme.PlateSwipeTheme
import com.android.sample.ui.utils.PlateSwipeScaffold
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class PlateSwipeScaffoldTest {
  private lateinit var mockNavigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
  }

  @Test
  fun testPlateSwipeScaffoldDisplayed() {
    composeTestRule.setContent {
      PlateSwipeScaffold(mockNavigationActions, selectedItem = "Swipe", content = {})
    }

    composeTestRule.onNodeWithTag("plateSwipeScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBarTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backArrowIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun testBackArrowIsNotDisplayedWhenShouldNotBeShown() {
    composeTestRule.setContent {
      PlateSwipeScaffold(mockNavigationActions, selectedItem = "Swipe", content = {}, false)
    }

    composeTestRule.onNodeWithTag("backArrowIcon").assertIsNotDisplayed()
  }

  @Test
  fun testBackArrowCallsNavigationActionsWhenClicked() {
    composeTestRule.setContent {
      PlateSwipeScaffold(mockNavigationActions, selectedItem = "Swipe", content = {})
    }

    composeTestRule.onNodeWithTag("backArrowIcon").performClick()
    composeTestRule.waitForIdle()

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun testBottomNavigationMenuCallsNavigationWhenClicked() {
    composeTestRule.setContent {
      PlateSwipeTheme {
        PlateSwipeScaffold(mockNavigationActions, selectedItem = "Fridge", content = {})
      }
    }

    composeTestRule.onNodeWithTag("tabSwipe").performClick()
    composeTestRule.waitForIdle()

    verify(mockNavigationActions).navigateTo(TopLevelDestinations.SWIPE)
  }
}
