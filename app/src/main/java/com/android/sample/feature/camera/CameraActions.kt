package com.android.sample.feature.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.sample.resources.C

/**
 * @param lifecycleOwner
 * @param context
 * @param imageCapture
 * @param previewView
 * @return Unit
 * @throws Exception
 *
 * Function to start camera
 *
 * @author https://developer.android.com/codelabs/camerax-getting-started
 */
fun startCamera(
    lifecycleOwner: LifecycleOwner,
    context: Context,
    imageCapture: ImageCapture,
    previewView: PreviewView,
    imageAnalyzer: ImageAnalysis.Analyzer? = null
) {
  val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
  cameraProviderFuture.addListener(
      {
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val preview = createPreview().also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val imageAnalysis =
            imageAnalyzer?.let {
              ImageAnalysis.Builder().build().also { analysis ->
                analysis.setAnalyzer(ContextCompat.getMainExecutor(context), it)
              }
            }
        try {
          cameraProvider.unbindAll()
          if (imageAnalysis != null) {
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis)
          } else {
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
          }
        } catch (exc: Exception) {
          Log.e(C.Tag.LOG_TAG_CAMERA_ACTIONS, C.Tag.UNBINDING_ERR, exc)
        }
      },
      ContextCompat.getMainExecutor(context))
}
