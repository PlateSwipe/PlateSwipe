package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.ui.utils.Tag
import org.junit.Rule
import org.junit.Test

class TagTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tag_DisplayedCorrectly() {
    val tagName = "Vegetarian"

    // Set the content of the composable in the test environment
    composeTestRule.setContent { Tag(tag = tagName) }

    // Verify that the tag is displayed with the correct text
    composeTestRule.onNodeWithTag("TagTestTag").assertIsDisplayed()
    composeTestRule.onNodeWithText(tagName).assertIsDisplayed()
  }

  @Test
  fun tag_DoesNotDisplayedEmptyText() {
    val tagName = ""

    // Set the content of the composable in the test environment
    composeTestRule.setContent { Tag(tag = tagName) }

    // Verify that the tag is displayed with the correct text
    composeTestRule.onNodeWithTag("TagTestTag").assertIsNotDisplayed()
  }
}
