package com.android.sample.feature.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.createRecipe.ChefImage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

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
    navigationActions: NavigationActions
) {

  RequestScreen(permissionState, onPermissionGranted, navigationActions)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestScreen(
    permissionState: PermissionState,
    onPermissionGranted: @Composable () -> Unit,
    navigationActions: NavigationActions
) {
  if (permissionState.status.isGranted) {
    onPermissionGranted()
  } else {
    PlateSwipeScaffold(
        navigationActions = navigationActions,
        selectedItem = navigationActions.currentRoute(),
        content = { padding ->
          val text =
              if (permissionState.status.shouldShowRationale) {
                stringResource(R.string.camera_permission_denied_please_allow)
              } else {
                stringResource(R.string.camera_permission_is_required_to_use_this_feature)
              }
          Column(
              modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                Text(
                    text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                  Text(
                      stringResource(R.string.request_camera_permission),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onPrimary,
                      textAlign = Center)
                }
                Spacer(modifier = Modifier.height(16.dp))
                ChefImage()
              }
        },
        showBackArrow = true,
    )
  }
}
