package com.android.sample.feature.camera

import androidx.camera.core.Preview
import androidx.compose.runtime.Composable
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem

@Composable
fun CameraView(
    onCapturePhoto: (File) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = createImageCapture()

    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize()) {
            startCamera(lifecycleOwner,context, imageCapture, previewView)
        }

        Button(
            onClick = { takePhoto(context, imageCapture, onCapturePhoto) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = 150.dp, height = 50.dp)
        ) {
            Text("Capture Photo")
        }
    }
}