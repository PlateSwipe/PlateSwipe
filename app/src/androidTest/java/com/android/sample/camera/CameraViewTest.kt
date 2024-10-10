package com.android.sample.camera

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.feature.camera.CameraView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraViewTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent { CameraView() }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithText("Capture Photo").assertExists()
    composeTestRule.onNodeWithTag("camera_preview").assertExists()
    composeTestRule.onNodeWithTag("capture_button").assertExists()
    composeTestRule.onNodeWithTag("capture_button_text", useUnmergedTree = true).assertExists()

    // check if they are displayed
    composeTestRule.onNodeWithTag("camera_preview").assertIsDisplayed()
    composeTestRule.onNodeWithTag("capture_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("capture_button_text", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testClickCaptureButton() {
    composeTestRule.onNodeWithText("Capture Photo").performClick()
  }
}
