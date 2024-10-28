package com.android.sample.feature.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView

/**
 * @param imageCapture
 * @param imageAnalyzer
 * @return Unit
 *
 * Function to display camera view
 *
 * @see CameraView
 * @see startCamera
 * @see
 *   "https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/views-in-compose"
 */
@Composable
fun CameraView(imageCapture: ImageCapture, imageAnalyzer: ImageAnalysis.Analyzer? = null) {

  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val previewView = remember { PreviewView(context) }

  Box(modifier = Modifier.fillMaxSize()) {
    // AndroidView :  to use UI elements that are not yet available in Compose
    AndroidView(
        factory = { previewView }, modifier = Modifier.fillMaxSize().testTag("camera_preview")) {
          startCamera(lifecycleOwner, context, imageCapture, previewView, imageAnalyzer)
        }
  }
}
