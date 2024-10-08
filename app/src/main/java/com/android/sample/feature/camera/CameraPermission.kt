package com.android.sample.feature.camera

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestCameraPermission(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

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