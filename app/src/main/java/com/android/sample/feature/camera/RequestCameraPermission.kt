package com.android.sample.feature.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

/**
 * @param onPermissionGranted
 * @return Unit
 *
 * Function to request camera permission
 *
 * @see RequestCameraPermission
 * @author based on https://google.github.io/accompanist/permissions/
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(
    permissionState: PermissionState,
    onPermissionGranted: @Composable () -> Unit,
) {
  when (permissionState.status) {
    PermissionStatus.Granted -> {
      onPermissionGranted()
    }
    // First time the user will connect to the APP he will need to click on the button to request
    // the permission
    is PermissionStatus.Denied -> {
      Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { permissionState.launchPermissionRequest() },
            modifier = Modifier.fillMaxWidth(1f / 2f).fillMaxHeight(1f / 10f)) {
              Text("Request permission")
            }
      }
    }
  }
}
