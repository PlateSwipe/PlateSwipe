package com.android.sample.ui.camera

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.android.sample.feature.camera.scan.CodeBarAnalyzer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScanCodeBarScreen() {
  val context = LocalContext.current
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
  val imageCapture = createImageCapture()

  RequestCameraPermission(
      cameraPermissionState = cameraPermissionState,
      onPermissionGranted = {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(modifier = Modifier.weight(1f).aspectRatio(1f).background(Color.Black)) {
                CameraView(
                    imageCapture,
                    CodeBarAnalyzer { barcode ->
                      Toast.makeText(context, "Barcode scanned: $barcode", Toast.LENGTH_SHORT)
                          .show()
                    })
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(32.dp)
                            .border(
                                width = 4.dp, color = Color.White, shape = RoundedCornerShape(0.dp))
                            .testTag("Barcode frame"))
              }
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                  text = "Align the barcode within the frame to scan",
                  color = Color.White,
                  modifier = Modifier.testTag("Info text"))
            }
      },
      onPermissionDenied = {
        Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
      })
}
