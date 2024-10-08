package com.android.sample.feature.camera

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXFragment : Fragment() {

  private lateinit var safeContext: Context
  private lateinit var cameraExecutor: ExecutorService

  override fun onAttach(context: Context) {
    super.onAttach(context)
    safeContext = context
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    cameraExecutor = Executors.newSingleThreadExecutor()
    return ComposeView(requireContext()).apply { setContent { CameraXFragmentComposable() } }
  }

  override fun onDestroy() {
    super.onDestroy()
    cameraExecutor.shutdown()
  }

  private fun startCamera(previewView: PreviewView) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)
    cameraProviderFuture.addListener(
        {
          val cameraProvider = cameraProviderFuture.get()
          val preview =
              Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
          val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
          try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
          } catch (exc: Exception) {
            Log.e("CameraXFragment", "Use case binding failed", exc)
          }
        },
        ContextCompat.getMainExecutor(safeContext))
  }

  @Composable
  fun CameraXFragmentComposable() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) { startCamera(previewView) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
  }
}
