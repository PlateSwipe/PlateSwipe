package com.android.sample.ui.camera

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.RequestCameraPermission
import com.android.sample.feature.camera.createImageCapture
import com.android.sample.feature.camera.takePhoto
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraTakePictureScreen() {
  val context = LocalContext.current
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
  val imageCapture = createImageCapture()

  RequestCameraPermission(
      cameraPermissionState = cameraPermissionState,
      onPermissionGranted = {
        Box(modifier = Modifier.fillMaxSize()) {
          CameraView(imageCapture)
          Button(
              onClick = {
                takePhoto(
                    context,
                    imageCapture,
                    onPhotoSaved = { output ->
                      val msg = "Photo capture succeeded: ${output.savedUri}"
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    })
              },
              modifier =
                  Modifier.padding(bottom = 20.dp)
                      .align(Alignment.BottomCenter)
                      .size(80.dp)
                      .testTag("capture_button"),
              shape = CircleShape,
              colors =
                  ButtonColors(
                      containerColor = Color.White,
                      contentColor = Color.Black,
                      disabledContentColor = Color.Gray,
                      disabledContainerColor = Color.Gray)) {}
        }
      },
      onPermissionDenied = {
        Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
      })
}
