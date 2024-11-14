package com.android.sample.feature.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.sample.R
import com.android.sample.resources.C

/**
 * Starts the camera and binds the lifecycle to the provided lifecycle owner.
 *
 * @param lifecycleOwner The lifecycle owner which controls the lifecycle of the camera.
 * @param context The context used to access system resources.
 * @param imageCapture The ImageCapture object used to take photos.
 * @param previewView The PreviewView where the camera preview will be displayed.
 * @param imageAnalyzer An optional ImageAnalysis.Analyzer for analyzing the camera frames.
 * @throws Exception If there is an error during the camera setup.
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
        val preview = createPreview().also { it.surfaceProvider = previewView.surfaceProvider }

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

/**
 * Takes a photo using the provided ImageCapture object and calls the onPhotoTaken callback with the
 * captured image.
 *
 * @param context The context used to access system resources.
 * @param imageCapture The ImageCapture object used to take the photo.
 * @param onPhotoTaken A callback function to be called after the photo is taken, receiving the
 *   captured ImageProxy.
 */
fun takePhoto(context: Context, imageCapture: ImageCapture, onPhotoTaken: (ImageProxy) -> Unit) {

  // Set up image capture listener, which is triggered after the photo has been taken
  imageCapture.takePicture(
      ContextCompat.getMainExecutor(context),
      object : ImageCapture.OnImageCapturedCallback() {

        /**
         * Called when an error occurs during image capture.
         *
         * @param exc The exception that occurred during image capture.
         */
        override fun onError(exc: ImageCaptureException) {
          Log.e(
              context.getString(R.string.take_photo),
              context.getString(R.string.photo_capture_failed, exc.message),
              exc)
        }

        /**
         * Called when the image has been successfully captured.
         *
         * @param image The captured ImageProxy.
         */
        override fun onCaptureSuccess(image: ImageProxy) {
          onPhotoTaken(image)
        }
      })
}
