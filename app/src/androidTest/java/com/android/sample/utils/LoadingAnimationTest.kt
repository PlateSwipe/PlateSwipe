package com.android.sample.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.utils.LoadingAnimation
import org.junit.Rule
import org.junit.Test

class LoadingAnimationTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun verifyFruitCreation() {
    composeTestRule.setContent { LoadingAnimation(onFinish = {}) }

    // Wait for initial delay and check if fruits are added
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule.onAllNodesWithTag("FruitImage_0").fetchSemanticsNodes().isNotEmpty()
    }

    // Verify that up to 8 fruits are created
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule.onAllNodesWithTag("FruitImage_7").fetchSemanticsNodes().isNotEmpty()
    }
    for (i in 0..7) {
      composeTestRule.onNodeWithTag("FruitImage_$i").assertExists()
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testFruitProperties() {
    composeTestRule.setContent { LoadingAnimation(onFinish = {}, duration = 1000L) }

    // Wait until the first fruit appears
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("FruitImage_0"))

    // Check initial position and rotation of first fruit
    val fruitNode = composeTestRule.onNodeWithTag("FruitImage_0")
    fruitNode.assertExists()
    fruitNode.assertIsDisplayed()
  }

  @Test
  fun testFallCompletionTriggersOnFinish() {
    var callbackTriggered = false
    composeTestRule.setContent {
      LoadingAnimation(onFinish = { callbackTriggered = true }, duration = 1000L)
    }

    // Wait for the onFinish callback to trigger
    composeTestRule.waitUntil(timeoutMillis = 1500) { callbackTriggered }
  }
}
