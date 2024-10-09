package com.android.sample.camera

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
        FakePermissionState(isGranted = true, willBeGranted = true)
      }
      RequestCameraPermission(
          cameraPermissionState = permissionState,
          onPermissionGranted = { TestContent("Granted") },
          onPermissionDenied = { TestContent("Denied") })
    }

    composeTestRule.onNodeWithText("Granted").assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionWasNotPermittedThenDenied() {
    composeTestRule.setContent {
      val permissionState = remember {
        FakePermissionState(isGranted = false, willBeGranted = false)
      }
      RequestCameraPermission(
          cameraPermissionState = permissionState,
          onPermissionGranted = { TestContent("Granted") },
          onPermissionDenied = { TestContent("Denied") })
    }

    composeTestRule.onNodeWithText("Denied").assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionWasNotPermittedThenAccepted() {
    composeTestRule.setContent {
      val permissionState = remember {
        FakePermissionState(isGranted = false, willBeGranted = true)
      }
      RequestCameraPermission(
          cameraPermissionState = permissionState,
          onPermissionGranted = { TestContent("Granted") },
          onPermissionDenied = { TestContent("Denied") })
    }

    composeTestRule.onNodeWithText("Granted").assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun testCameraPermissionStateIsGranted() {
    composeTestRule.setContent {
      val permissionState = remember {
        FakePermissionState(isGranted = true, willBeGranted = true)
      }
      RequestCameraPermission(
          cameraPermissionState = permissionState,
          onPermissionGranted = { TestContent("Permission is Granted") },
          onPermissionDenied = { TestContent("Permission is Denied") })
    }

    composeTestRule.onNodeWithText("Permission is Granted").assertExists()
  }

  @Composable
  fun TestContent(text: String) {
    Text(text = text)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  private class FakePermissionState(isGranted: Boolean, private val willBeGranted: Boolean) :
      PermissionState {
    private val _isGranted = mutableStateOf(isGranted)
    override val permission: String
      get() = "android.permission.CAMERA"

    override val status: PermissionStatus
      get() = if (_isGranted.value) PermissionStatus.Granted else PermissionStatus.Denied(false)

    override fun launchPermissionRequest() {
      _isGranted.value = willBeGranted
    }
  }
}
