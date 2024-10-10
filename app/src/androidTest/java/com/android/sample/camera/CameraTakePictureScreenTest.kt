package com.android.sample.camera

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.ui.camera.CameraTakePictureScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTakePictureScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    composeTestRule.setContent { CameraTakePictureScreen() }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("capture_button").assertExists()
    composeTestRule.onNodeWithTag("capture_button").assertIsDisplayed()
  }
}
