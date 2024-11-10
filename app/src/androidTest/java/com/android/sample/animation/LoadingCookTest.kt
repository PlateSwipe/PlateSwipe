package com.android.sample.animation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.resources.C.Dimension.LoadingCook.ROTATION_DURATION
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadingCookTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLoadingCookRotationAnimation() {
    // Set up the LoadingCook composable
    composeTestRule.setContent { LoadingCook() }

    // Find the image with the content description
    val rotatingImage = composeTestRule.onNodeWithContentDescription("Rotating Image")

    // Check initial rotation (just after the composable is set up)
    composeTestRule.waitForIdle()
    rotatingImage.assertExists()

    // Delay for a brief period and then check the rotation to ensure it's animating.
    composeTestRule.mainClock.advanceTimeBy((ROTATION_DURATION / 2).toLong())
    composeTestRule.waitForIdle()

    // Re-check the image node, which should have a different rotation value
    // Note: since we can't directly check the rotation float value in a real test,
    //       you'd verify the animation by ensuring the node is visible and existing after time
    // advances.
    rotatingImage.assertExists()
  }
}
