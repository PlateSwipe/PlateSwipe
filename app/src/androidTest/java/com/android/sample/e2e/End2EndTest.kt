package com.android.sample.e2e

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.PlateSwipeApp
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.swipePage.SwipePage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    private lateinit var navigationActions: NavigationActions

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        navigationActions = mock(NavigationActions::class.java)
    }

    @Test
    fun testNavigationThroughBottomNav() {

        `when`(navigationActions.currentRoute()).thenReturn(Screen.SWIPE)
        // Set the initial content to the MainScreen
        composeTestRule.setContent { SwipePage(navigationActions = navigationActions) }

        // Click on Fridge Icon
        composeTestRule.onNodeWithTag("Fridge").assertExists().performClick()
        verify(navigationActions).navigateTo(TopLevelDestinations.FRIDGE)

        // Click on Create Recipe Icon
        composeTestRule.onNodeWithTag("Add Recipe").assertExists().performClick()
        verify(navigationActions).navigateTo(TopLevelDestinations.ADD_RECIPE)

        // Click on Search Icon
        composeTestRule.onNodeWithTag("Search").assertExists().performClick()
        verify(navigationActions).navigateTo(TopLevelDestinations.SEARCH)

        // Click on Account Icon
        composeTestRule.onNodeWithTag("Account").assertExists().performClick()
        verify(navigationActions).navigateTo(TopLevelDestinations.ACCOUNT)

        // Click on Swipe Icon
        composeTestRule.onNodeWithTag("Swipe").assertExists().performClick()
        verify(navigationActions).navigateTo(TopLevelDestinations.SWIPE)
    }

}