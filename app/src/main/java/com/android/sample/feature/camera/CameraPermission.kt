package com.android.sample.feature.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(
    cameraPermissionState: PermissionState,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit
) {
  if (cameraPermissionState.status.isGranted) {
    onPermissionGranted()
  } else {
    LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
    if (cameraPermissionState.status.isGranted) {
      onPermissionGranted()
    } else {
      onPermissionDenied()
    }
  }
}
