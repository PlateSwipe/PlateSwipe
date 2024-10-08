package com.android.sample.ui.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
  val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
  val context = LocalContext.current

  if (cameraPermissionState.status.isGranted) {
    CameraPreview(
        onCapture = { photoFile ->
          // Save the photo to a desired location
          val savedFile = File(context.getExternalFilesDir(null), photoFile.name)
          photoFile.copyTo(savedFile, overwrite = true)
        })
  } else {
    LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
  }
}

@Composable
fun CameraPreview(onCapture: (File) -> Unit) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val previewView = remember { PreviewView(context) }
  val imageCapture = remember { ImageCapture.Builder().build() }

  AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize()) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener(
        {
          val cameraProvider = cameraProviderFuture.get()
          val preview =
              Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

          val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
          cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        },
        ContextCompat.getMainExecutor(context))
  }

  // Capture button
  Button(
      onClick = {
        val photoFile = File(context.filesDir, "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
              override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onCapture(photoFile)
              }

              override fun onError(exception: ImageCaptureException) {
                // Handle error
              }
            })
      }) {
        Text("Capture Photo")
      }
}
