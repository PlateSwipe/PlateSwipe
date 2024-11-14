package com.android.sample.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.ui.utils.PlateSwipeButton
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlateSwipeButtonTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val text = "PlateSwipeButton"
  private val modifier = Modifier

  @Before
  fun setUp() {
    composeTestRule.setContent { PlateSwipeButton(text = text, modifier = modifier, onClick = {}) }
  }

  @Test
  fun testPlateSwipeButtonDisplayed() {
    composeTestRule.onNodeWithText(text).assertExists()
  }

  @Test
  fun testPlateSwipeButtonIsClickable() {
    composeTestRule.onNodeWithText(text).performClick()
  }
}
