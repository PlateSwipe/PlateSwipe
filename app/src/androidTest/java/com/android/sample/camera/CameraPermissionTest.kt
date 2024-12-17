package com.android.sample.camera

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.feature.camera.RequestCameraPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraPermissionTest {

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionGranted() {
    composeTestRule.setContent {
      val permissionState = remember {
        FakePermissionState(initialIsGranted = true, willBeGranted = true)
      }
      RequestCameraPermission(
          permissionState = permissionState, onPermissionGranted = { TestContent("Granted") })
    }

    composeTestRule.onNodeWithText("Granted").assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionNotGranted() {
    composeTestRule.setContent {
      val permissionState = remember {
        FakePermissionState(initialIsGranted = false, willBeGranted = true)
      }
      RequestCameraPermission(
          permissionState = permissionState, onPermissionGranted = { TestContent("Granted") })
    }
    composeTestRule
        .onNodeWithText("Camera permission is required to use this feature.")
        .assertExists()
    composeTestRule.onNodeWithText("Request Camera Permission").assertExists().performClick()
    composeTestRule.onNodeWithText("Granted").assertExists()
  }

  @Composable
  fun TestContent(text: String) {
    Text(text = text)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  private class FakePermissionState(
      initialIsGranted: Boolean,
      private val willBeGranted: Boolean,
      private val showRationale: Boolean = false
  ) : PermissionState {

    override val permission: String
      get() = "android.permission.CAMERA"

    private val _status =
        mutableStateOf<PermissionStatus>(
            if (initialIsGranted) PermissionStatus.Granted
            else PermissionStatus.Denied(showRationale))
    override val status: PermissionStatus
      get() = _status.value

    override fun launchPermissionRequest() {
      _status.value =
          if (willBeGranted) PermissionStatus.Granted else PermissionStatus.Denied(showRationale)
    }

    fun simulateStatusChange(isGranted: Boolean) {
      _status.value =
          if (isGranted) PermissionStatus.Granted else PermissionStatus.Denied(showRationale)
    }
  }
}
