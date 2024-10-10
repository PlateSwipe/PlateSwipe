package com.android.sample.ui.camera

import android.Manifest
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.RequestCameraPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

/** Composable that displays the camera screen. */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraTakePictureScreen() {
  val context = LocalContext.current
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
  RequestCameraPermission(
      cameraPermissionState = cameraPermissionState,
      onPermissionGranted = { CameraView() },
      onPermissionDenied = {
        Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
      })
}
