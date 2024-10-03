package com.android.sample

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService

object CameraUtils {
    private const val TAG = "CameraXApp"

    fun startCamera(
        activity: MainActivity,
        cameraExecutor: ExecutorService,
        imageCapture: ImageCapture?,
        cameraProvider: ProcessCameraProvider
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(activity.findViewById<PreviewView>(R.id.preview_view).surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, CodeBarAnalyzer(activity, provider))
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    activity, cameraSelector, preview, imageCapture, imageAnalyzer)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(activity))
    }
}