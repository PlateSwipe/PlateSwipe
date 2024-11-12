package com.android.sample.camera

import androidx.camera.core.ImageCapture
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
    val imageCapture = ImageCapture.Builder().build()
    composeTestRule.setContent { CameraView(imageCapture) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("camera_preview").assertExists()

    // check if they are displayed
    composeTestRule.onNodeWithTag("camera_preview").assertIsDisplayed()
  }
}
