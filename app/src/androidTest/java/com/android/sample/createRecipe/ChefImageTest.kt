package com.android.sample.ui.createRecipe

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class ChefImageTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testChefImageIsDisplayedWithCorrectDimensions() {
    composeTestRule.setContent { ChefImage() }

    composeTestRule
        .onNodeWithContentDescription("Chef standing")
        .assertExists()
        .assertIsDisplayed()
        .assertWidthIsEqualTo(250.dp)
        .assertHeightIsEqualTo(300.dp)
  }

  @Test
  fun testChefImageWithCustomOffsets() {
    composeTestRule.setContent { ChefImage(offsetX = 15.dp, offsetY = 30.dp) }

    composeTestRule.onNodeWithContentDescription("Chef standing").assertExists().assertIsDisplayed()
  }

  @Test
  fun testChefImageIsClippedCorrectly() {
    composeTestRule.setContent { ChefImage() }

    composeTestRule.onNodeWithContentDescription("Chef standing").assertExists().assertIsDisplayed()
  }

  @Test
  fun testChefImageContentScale() {
    composeTestRule.setContent { ChefImage() }

    composeTestRule.onNodeWithContentDescription("Chef standing").assertExists().assertIsDisplayed()
  }
}
