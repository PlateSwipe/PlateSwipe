package com.android.sample.ui.camera

import android.util.Log
import android.widget.Toast
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
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.requestCameraPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
/**
 * Composable that displays the camera screen.
 */
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    requestCameraPermission(
        onPermissionGranted = {
            CameraView(onCapturePhoto = { photoFile ->
                Toast.makeText(context, "Photo saved at: ${photoFile.absolutePath}", Toast.LENGTH_LONG).show()
            })
        },
        onPermissionDenied = {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    )
}