package com.android.sample.topbar

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.topbar.MyAppBar
import org.junit.Rule
import org.junit.Test

class MyAppBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testAppBarDisplaysCorrectlyWithBackButton() {
    // Start the composable with the back button visible
    composeTestRule.setContent {
      MyAppBar(title = "PlateSwipe", showBackButton = true, onBackClick = { /* Do nothing */})
    }

    // Verify that the top app bar is displayed
    composeTestRule.onNodeWithTag("TopAppBar").assertExists()

    // Verify that the back button is displayed
    composeTestRule.onNodeWithTag("BackButton").assertExists()

    // Verify that the chef's hat icon is displayed
    composeTestRule.onNodeWithTag("ChefHatIcon").assertExists()

    // Verify that the title is displayed with the correct text
    composeTestRule.onNodeWithTag("AppBarTitle").assertTextEquals("PlateSwipe")
  }

  @Test
  fun testAppBarDisplaysCorrectlyWithoutBackButton() {
    // Start the composable with the back button hidden
    composeTestRule.setContent { MyAppBar(title = "PlateSwipe", showBackButton = false) }

    // Verify that the top app bar is displayed
    composeTestRule.onNodeWithTag("TopAppBar").assertExists()

    // Verify that the back button is not displayed
    composeTestRule.onNodeWithTag("BackButton").assertDoesNotExist()

    // Verify that the chef's hat icon is displayed
    composeTestRule.onNodeWithTag("ChefHatIcon").assertExists()

    // Verify that the title is displayed with the correct text
    composeTestRule.onNodeWithTag("AppBarTitle").assertTextEquals("PlateSwipe")
  }
}
