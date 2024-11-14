package com.android.sample.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.android.sample.ui.createRecipe.ChefImage
import org.junit.Rule
import org.junit.Test

class ChefImageTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testChefImageIsClippedCorrectly() {
    composeTestRule.setContent { ChefImage() }

    composeTestRule.onNodeWithContentDescription("Chef standing").assertExists().assertIsDisplayed()
  }
}
