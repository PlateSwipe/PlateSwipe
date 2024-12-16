package com.android.sample.feature.camera

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.createRecipe.ChefImage
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
  val context = LocalContext.current

  // Track if the permission request has been processed after user interaction
  var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }
  var permissionRequestCompleted by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(permissionState.status) {
    // Check if the permission state has changed after the request
    if (hasRequestedPermission) {
      permissionRequestCompleted = true
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    when (val status = permissionState.status) {
      is PermissionStatus.Granted -> {
        // Permission granted, access the feature
        onPermissionGranted()
      }
      is PermissionStatus.Denied -> {
        if (permissionRequestCompleted) {
          Rational(
              status.shouldShowRationale,
              onClickTrue = {
                permissionState.launchPermissionRequest()
                hasRequestedPermission = true
              },
              onClickFalse = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
              })
        } else {
          RequestPermScreen(
              stringResource(R.string.camera_permission_is_required_to_use_this_feature),
              stringResource(R.string.request_camera_permission),
              onClick = {
                permissionState.launchPermissionRequest()
                hasRequestedPermission = true
              })
        }
      }
    }
  }
}

@Composable
fun RequestPermScreen(title: String, buttonText: String, onClick: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClick) {
          Text(
              buttonText,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              textAlign = Center)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ChefImage()
      }
}

@Composable
fun Rational(showRational: Boolean, onClickTrue: () -> Unit, onClickFalse: () -> Unit) {
  if (showRational) {
    RequestPermScreen(
        stringResource(R.string.camera_permission_is_required_to_use_this_feature),
        stringResource(R.string.request_camera_permission),
        onClick = onClickTrue)
  } else {
    // Show "Denied" message only after the user has denied permission
    RequestPermScreen(
        stringResource(
            R.string.camera_permission_denied_please_enable_it_in_the_app_settings_to_proceed),
        stringResource(R.string.open_app_settings),
        onClick = onClickFalse)
  }
}
